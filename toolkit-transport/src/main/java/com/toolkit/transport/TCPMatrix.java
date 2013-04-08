/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    TCPMatrix.java
 * Creator:     qiaofeng
 * Create-Date: 2011-9-23 下午12:56:28
 *******************************************************************************/
package com.toolkit.transport;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.lang.Closure;
import com.toolkit.transport.cache.DefaultHolder;
import com.toolkit.transport.cache.Holder;
import com.toolkit.transport.codec.XipCodecFactory;
import com.toolkit.transport.receiver.Receiver;
import com.toolkit.transport.util.IpPortPair;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: TCPMatrix.java 675 2011-09-23 06:41:42Z qiaofeng $
 */
public class TCPMatrix {
	
	private final static Logger					LOG					= LoggerFactory
																			.getLogger(TCPMatrix.class);
	private ConcurrentMap<String, TCPRouter>	routers				= new ConcurrentHashMap<String, TCPRouter>();
	private XipCodecFactory						codecFactory		= null;
	private Closure								nextClosure			= null;
	private Receiver							receiver			= null;
	private int									cachedMessageCount	= 1024;
	private int									maxSession			= 1;
	private Holder								responseContext		= new DefaultHolder();
	private long								reconnectTimeout	= 1;
	
	public Map<String, TCPRouter> getRouters() {
		return routers;
	}
	
	public void doRefreshRoute(String serverType, List<IpPortPair> infos) {
		TCPRouter router = getRouter(serverType);
		if (router != null) {
			router.doRefreshRoute(infos);
		}
	}
	
	private TCPRouter getRouter(String serverGroup) {
		
		if (routers.containsKey(serverGroup)) {
			return routers.get(serverGroup);
		}
		LOG.debug("create server group. serverGroup=[{}]", serverGroup);
		return createRouter(serverGroup);
	}
	
	private TCPRouter createRouter(String serverType) {
		
		TCPRouter router = routers.get(serverType);
		
		if (null == router) {
			router = new TCPRouter();
			TCPRouter oldRouter = routers.putIfAbsent(serverType, router);
			if (null != oldRouter) {
				router = oldRouter;
			} else {
				router.setName(serverType);
				router.setCachedMessageCount(this.cachedMessageCount);
				router.setNextClosure(this.nextClosure);
				router.setReceiver(this.receiver);
				router.setResponseContext(this.responseContext);
				router.setMaxSession(this.maxSession);
				
				router.setCodecFactory(this.codecFactory);
				router.setReconnectTimeout(this.reconnectTimeout);
			}
		}
		return router;
	}
	
	public void setRouters(ConcurrentHashMap<String, TCPRouter> routers) {
		this.routers = routers;
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
	
}
