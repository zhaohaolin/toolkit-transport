/**
 * 
 */
package com.toolkit.transport.heartbeat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.transport.TCPConnector;
import com.toolkit.transport.TCPRouter;
import com.toolkit.transport.heartbeat.xip.HeartbeatReq;
import com.toolkit.transport.heartbeat.xip.HeartbeatResp;
import com.toolkit.transport.heartbeat.xip.ServerGroup;
import com.toolkit.transport.heartbeat.xip.ServerStatus;
import com.toolkit.transport.response.ResponseClosure;
import com.toolkit.transport.util.IpPortPair;

/**
 * 心跳消息生产器
 * 
 * @author qiaofeng
 * @version $Id: HeartbeatProducer, v 0.1 2012-8-29 上午10:18:19 Exp $
 */
public class HeartbeatProducer {
	
	private final static Logger			LOG					= LoggerFactory
																	.getLogger(HeartbeatProducer.class);
	private HeartbeatMessageProducer	messageProducer;
	private TCPConnector				connector;
	private ScheduledExecutorService	scheduler			= Executors
																	.newSingleThreadScheduledExecutor();
	private long						heartbeatInterval	= 5 * 1000;
	private Map<String, TCPRouter>		routers				= new ConcurrentHashMap<String, TCPRouter>();
	
	private List<IpPortPair> convert(ServerGroup group) {
		List<ServerStatus> servers = group.getServers();
		List<IpPortPair> ret = new ArrayList<IpPortPair>(null == servers ? 0
				: servers.size());
		if (null != servers) {
			for (ServerStatus info : servers) {
				ret.add(new IpPortPair(info.getIp(), info.getPort()));
			}
		}
		return ret;
	}
	
	public void start() {
		scheduler.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				// 生成心跳请求
				HeartbeatReq req = messageProducer.call();
				if (LOG.isTraceEnabled()) {
					LOG.trace("send connector=[{}], HeartbeatReq=[{}]",
							connector, req);
				}
				
				// 向中央控制器发送心跳包消息
				connector.send(req, new ResponseClosure<HeartbeatResp>() {
					
					@Override
					public void onResponse(HeartbeatResp resp) {
						
						// 返回心跳包响应
						if (LOG.isTraceEnabled()) {
							LOG.trace(
									"response connector=[{}], HeartbeatResp=[{}]",
									new Object[] { connector, resp });
						}
						
						// 当路由配置不为空时
						if (!routers.isEmpty()) {
							List<ServerGroup> groups = resp.getCandidates();
							for (ServerGroup group : groups) {
								TCPRouter router = routers.get(group
										.getServerType());
								if (null != router) {
									if (LOG.isTraceEnabled()) {
										LOG.trace(
												"refresh router router=[{}], group=[{}]",
												new Object[] { router, group });
									}
									router.doRefreshRoute(convert(group));
								}
							}
						}
					}
					
				});
				
			}
		}, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
	}
	
	public void stop() {
		scheduler.shutdown();
	}
	
	public void setMessageProducer(HeartbeatMessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}
	
	public HeartbeatMessageProducer getMessageProducer() {
		return messageProducer;
	}
	
	public void setHeartbeatInterval(long heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval * 1000;
	}
	
	public void setConnector(TCPConnector connector) {
		this.connector = connector;
	}
	
	public void setRouters(Map<String, TCPRouter> routers) {
		this.routers = routers;
	}
}
