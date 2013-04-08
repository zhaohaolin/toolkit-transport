/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    TCPRouter.java
 * Creator:     qiaofeng
 * Create-Date: 2011-5-3 上午09:25:40
 *******************************************************************************/
package com.toolkit.transport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.lang.Closure;
import com.toolkit.transport.cache.DefaultHolder;
import com.toolkit.transport.cache.Holder;
import com.toolkit.transport.codec.XipCodecFactory;
import com.toolkit.transport.receiver.Receiver;
import com.toolkit.transport.response.ResponseClosure;
import com.toolkit.transport.routing.ChooseFirst;
import com.toolkit.transport.routing.RoundRobin;
import com.toolkit.transport.routing.Scheduling;
import com.toolkit.transport.routing.SchedulingStrategy;
import com.toolkit.transport.sender.Sender;
import com.toolkit.transport.util.IpPortPair;

/**
 * TCP路由
 * 
 * @author qiaofeng
 * @version $Id: TCPRouter.java 670 2011-09-23 04:57:57Z qiaofeng $
 */
public class TCPRouter implements Sender {
	
	private final static Logger						LOG					= LoggerFactory
																				.getLogger(TCPRouter.class);
	private ConcurrentMap<IpPortPair, TCPConnector>	connectors			= new ConcurrentHashMap<IpPortPair, TCPConnector>();
	private XipCodecFactory							codecFactory		= null;
	private Closure									nextClosure			= null;
	private Receiver								receiver			= null;
	private int										cachedMessageCount	= 1024;
	private int										maxSession			= 1;
	private Holder									responseContext		= new DefaultHolder();
	private long									reconnectTimeout	= 1;
	private List<IpPortPair>						snapshot			= new ArrayList<IpPortPair>();
	private AtomicReference<IpPortPair[]>			routesRef			= new AtomicReference<IpPortPair[]>(
																				new IpPortPair[0]);
	private Scheduling								scheduling			= new RoundRobin();
	private String									name;
	
	private TCPConnector next() {
		int index = scheduling.next();
		if (index >= 0) {
			IpPortPair pair = routesRef.get()[index];
			if (connectors.containsKey(pair)) {
				return connectors.get(pair);
			}
			return createConnector(pair.getIp(), pair.getPort());
		}
		return null;
	}
	
	/**
	 * 创建连接
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	private TCPConnector createConnector(String ip, int port) {
		IpPortPair pair = new IpPortPair(ip, port);
		TCPConnector connector = connectors.get(pair);
		
		if (null == connector) {
			connector = new TCPConnector();
			TCPConnector oldConnector = connectors.putIfAbsent(pair, connector);
			if (null != oldConnector) {
				connector.stop();
				connector = oldConnector;
			} else {
				
				connector.setCachedMessageCount(this.cachedMessageCount);
				connector.setNextClosure(this.nextClosure);
				connector.setReceiver(this.receiver);
				connector.setResponseContext(this.responseContext);
				connector.setMaxSession(this.maxSession);
				
				connector.setCodecFactory(this.codecFactory);
				connector.setDestIp(ip);
				connector.setDestPort(port);
				connector.setReconnectTimeout(this.reconnectTimeout);
				
				connector.start();
			}
		}
		return connector;
	}
	
	/**
	 * 发送
	 */
	@Override
	public void send(Object bean) {
		TCPConnector connector = next();
		if (connector != null) {
			connector.send(bean);
			if (LOG.isTraceEnabled()) {
				LOG.trace("send: connector=[{}], bean=[{}]", new Object[] {
						connector, bean });
			}
		} else {
			if (LOG.isErrorEnabled()) {
				LOG.error("send: no route, msg [{}] lost. route=[{}]",
						new Object[] { bean, name });
			}
		}
	}
	
	/**
	 * 带回调类的发送
	 */
	@Override
	public void send(Object bean, ResponseClosure<?> callback) {
		TCPConnector connector = next();
		if (connector != null) {
			connector.send(bean, callback);
			if (LOG.isTraceEnabled()) {
				LOG.trace("send: connector=[{}], bean=[{}]", new Object[] {
						connector, bean });
			}
		} else {
			if (LOG.isErrorEnabled()) {
				LOG.error("send: no route, msg [{}] lost. route=[{}]",
						new Object[] { bean, name });
			}
		}
	}
	
	/**
	 * 超时发送，直到发送失败为止
	 */
	@Override
	public Object sendAndWait(Object bean) {
		TCPConnector connector = next();
		if (connector != null) {
			LOG.trace("sendAndWait: connector=[{}], bean=[{}]", new Object[] {
					connector, bean });
			return connector.sendAndWait(bean);
		}
		
		if (LOG.isErrorEnabled())
			LOG.error("send: no route, msg [{}] lost. route=[{}]",
					new Object[] { bean, name });
		return null;
	}
	
	/**
	 * 带超时设置的发送
	 */
	@Override
	public Object sendAndWait(Object bean, long timeout, TimeUnit timeUnit) {
		TCPConnector connector = next();
		if (connector != null) {
			LOG.trace(
					"sendAndWait: connector=[{}], bean=[{}], timeout=[{}], timeUnit=[{}]",
					new Object[] { connector, bean, timeout, timeUnit });
			return connector.sendAndWait(bean, timeout, timeUnit);
		}
		
		if (LOG.isErrorEnabled())
			LOG.error("send: no route, msg [{}] lost. route=[{}]",
					new Object[] { bean, name });
		return null;
	}
	
	/**
	 * 刷新路由
	 * 
	 * @param infos
	 */
	public void doRefreshRoute(List<IpPortPair> infos) {
		
		Collections.sort(infos);
		
		if (!snapshot.equals(infos)) {
			LOG.info(
					"doRefreshRoute [{}]: update routes info:[{}]/lastRoutes:[{}].",
					new Object[] { name, infos, snapshot });
			
			snapshot.clear();
			snapshot.addAll(infos);
			routesRef.set(snapshot.toArray(new IpPortPair[0]));
			scheduling.setTotal(routesRef.get().length);
		}
		
		// 删除无效连接
		for (IpPortPair key : connectors.keySet()) {
			if (!snapshot.contains(key)) {
				TCPConnector out = connectors.get(key);
				if (null != out) {
					out.stop();
				}
				connectors.remove(key);
			}
		}
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
	
	// 以IP和port直接配置
	public void setHosts(String hosts) {
		try {
			String[] hostArray;
			if (hosts.indexOf("/") == -1) {
				hostArray = new String[] { hosts };
			} else {
				hostArray = hosts.split("/");
			}
			
			List<IpPortPair> infos = new ArrayList<IpPortPair>();
			for (int i = 0; i < hostArray.length; i++) {
				String ipPort = hostArray[i];
				if (ipPort == null)
					break;
				String[] server = ipPort.split(":");
				if (server.length == 2) {
					IpPortPair ipPortPair = new IpPortPair(server[0].trim(),
							Integer.parseInt(server[1].trim()));
					infos.add(ipPortPair);
				} else {
					throw new RuntimeException("host [" + ipPort
							+ "] not match IP:PORT");
				}
			}
			this.doRefreshRoute(infos);
		} catch (Exception ex) {
			LOG.error(">>>> config occurs error. (hosts ParseException)", ex);
			System.exit(0);
		}
	}
	
	public void setCodecFactory(XipCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}
	
	public void setNextClosure(Closure nextClosure) {
		this.nextClosure = nextClosure;
	}
	
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	
	public void setCachedMessageCount(int cachedMessageCount) {
		this.cachedMessageCount = cachedMessageCount;
	}
	
	public void setMaxSession(int maxSession) {
		this.maxSession = maxSession;
	}
	
	public void setResponseContext(Holder responseContext) {
		this.responseContext = responseContext;
	}
	
	public void setReconnectTimeout(long reconnectTimeout) {
		this.reconnectTimeout = reconnectTimeout;
	}
	
	public ConcurrentMap<IpPortPair, TCPConnector> getConnectors() {
		return connectors;
	}
	
	public List<IpPortPair> getSnapshot() {
		return snapshot;
	}
	
	public AtomicReference<IpPortPair[]> getRoutesRef() {
		return routesRef;
	}
	
	public Scheduling getScheduling() {
		return scheduling;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setRoutingStrategy(SchedulingStrategy strategy) {
		if (SchedulingStrategy.CHOOSE_FIRST == strategy) {
			this.scheduling = new ChooseFirst();
		} else if (SchedulingStrategy.ROUND_ROBIN == strategy) {
			this.scheduling = new RoundRobin();
		} else {
			throw new UnsupportedOperationException("SchedulingStrategy ["
					+ strategy + "] not implemend yet.");
		}
	}
}
