/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    TCPConnector.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-28 下午12:05:00
 *******************************************************************************/
package com.toolkit.transport;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.lang.Closure;
import com.toolkit.transport.cache.Holder;
import com.toolkit.transport.codec.XipCodecFactory;
import com.toolkit.transport.endpoint.DefaultEndpointFactory;
import com.toolkit.transport.endpoint.DefaultEndpointRepository;
import com.toolkit.transport.endpoint.Endpoint;
import com.toolkit.transport.endpoint.EndpointFactory;
import com.toolkit.transport.endpoint.EndpointRepository;
import com.toolkit.transport.receiver.Receiver;
import com.toolkit.transport.response.ResponseClosure;
import com.toolkit.transport.sender.Sender;
import com.toolkit.transport.session.DefaultAddressProvider;
import com.toolkit.transport.session.IoChannelController;
import com.toolkit.transport.util.TransportUtil;

/**
 * Client
 * 
 * @author qiaofeng
 * @version $Id: TCPConnector.java 651 2011-09-02 04:03:01Z qiaofeng $
 */
public class TCPConnector implements Sender {
	
	private final static Logger			LOG					= LoggerFactory
																	.getLogger(TCPConnector.class);
	private ScheduledExecutorService	exec				= Executors
																	.newSingleThreadScheduledExecutor();
	private String						destIp				= null;
	private int							destPort			= -1;
	private ClientBootstrap				client				= null;
	private XipCodecFactory				codecFactory		= null;
	private InternalLoggerFactory		loggerFactory		= new Slf4JLoggerFactory();
	private List<String>				options;
	private long						reconnectTimeout	= 1;
	private IoChannelController			controller			= null;
	private EndpointFactory				endpointFactory		= new DefaultEndpointFactory();
	private EndpointRepository			endpointRepository	= new DefaultEndpointRepository();
	
	public TCPConnector() {
		//
	}
	
	public void start() {
		
		// 注册日志
		InternalLoggerFactory.setDefaultFactory(loggerFactory);
		
		try {
			client = new ClientBootstrap(new NioClientSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));
			
			this.controller = new IoChannelController(client);
			this.codecFactory.addHandler("clientHandler",
					new SimpleChannelHandler() {
						
						@Override
						public void messageReceived(ChannelHandlerContext ctx,
								MessageEvent e) throws Exception {
							Channel channel = e.getChannel();
							if (LOG.isTraceEnabled()) {
								LOG.trace("messageReceived: [{}]"
										+ e.getMessage());
							}
							
							Endpoint endpoint = TransportUtil
									.getEndpointOfChannel(channel);
							if (null != endpoint) {
								endpoint.messageReceived(TransportUtil
										.attachSender(e.getMessage(), endpoint));
							} else {
								LOG.warn("missing endpoint, ignore incoming msg: [{}]"
										+ e.getMessage());
							}
						}
						
						@Override
						public void channelOpen(ChannelHandlerContext ctx,
								ChannelStateEvent e) throws Exception {
							if (LOG.isInfoEnabled()) {
								LOG.info("open channel: [{}]" + e.getChannel());
							}
						}
						
						@Override
						public void exceptionCaught(ChannelHandlerContext ctx,
								ExceptionEvent e) throws Exception {
							LOG.error("transport: [{}]", e);
							// 解码有错误的情况下，channel不关闭
							// e.getChannel().close();
						}
						
					});
			
			this.codecFactory.addHandler("timeout", new IdleStateHandler(
					new HashedWheelTimer(), 10, 10, 0));
			this.codecFactory.addHandler("heartbeat", new HeartBeatHandler());
			
			// set codeFactory
			client.setPipelineFactory(this.codecFactory);
			
			// set options
			if (null != options && options.size() > 0) {
				for (String option : options) {
					client.setOption(option, true);
				}
			}
			
			this.controller.setAddressProvider(new DefaultAddressProvider(
					destIp, destPort));
			
			this.controller.setEndpointRepository(endpointRepository);
			this.controller.setEndpointFactory(endpointFactory);
			this.controller.setReconnectTimeout(reconnectTimeout);
			this.controller.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		this.exec.shutdown();
		if (null != client) {
			client.shutdown();
			client.releaseExternalResources();
			client = null;
		}
	}
	
	public String getDestIp() {
		return destIp;
	}
	
	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}
	
	public int getDestPort() {
		return destPort;
	}
	
	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}
	
	public void setOptions(List<String> options) {
		this.options = options;
	}
	
	public XipCodecFactory getCodecFactory() {
		return codecFactory;
	}
	
	public void setCodecFactory(XipCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}
	
	public void setNextClosure(Closure nextClosure) {
		endpointFactory.setNextClosure(nextClosure);
	}
	
	public void setReceiver(Receiver receiver) {
		endpointFactory.setReceiver(receiver);
	}
	
	public void setCachedMessageCount(int cachedMessageCount) {
		endpointFactory.setCachedMessageCount(cachedMessageCount);
	}
	
	public void setResponseContext(Holder responseContext) {
		endpointFactory.setResponseContext(responseContext);
	}
	
	public void setMaxSession(int maxSession) {
		endpointRepository.setMaxSession(maxSession);
	}
	
	public void setLoggerFactory(InternalLoggerFactory loggerFactory) {
		this.loggerFactory = loggerFactory;
	}
	
	public void setReconnectTimeout(long reconnectTimeout) {
		this.reconnectTimeout = reconnectTimeout;
	}
	
	@Override
	public void send(Object bean) {
		// 无连接时线程阻塞
		Endpoint endpoint = endpointRepository.getEndpoint();
		if (endpoint != null) {
			endpoint.send(bean);
		}
	}
	
	@Override
	public void send(Object object, ResponseClosure<?> callback) {
		Endpoint endpoint = endpointRepository.getEndpoint();
		if (endpoint != null) {
			endpoint.send(object, callback);
		}
	}
	
	@Override
	public Object sendAndWait(Object bean) {
		Endpoint endpoint = endpointRepository.getEndpoint();
		if (endpoint != null) {
			return endpoint.sendAndWait(bean);
		}
		return null;
	}
	
	@Override
	public Object sendAndWait(Object bean, long timeout, TimeUnit units) {
		Endpoint endpoint = endpointRepository.getEndpoint();
		if (endpoint != null) {
			return endpoint.sendAndWait(bean, timeout, units);
		}
		return null;
	}
	
	private class HeartBeatHandler extends IdleStateAwareChannelHandler {
		
		private int	i	= 0;
		
		@Override
		public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
				throws Exception {
			super.channelIdle(ctx, e);
			
			if (e.getState() == IdleState.WRITER_IDLE)
				i++;
			
			if (i == 5) {
				e.getChannel().close();
				if (LOG.isWarnEnabled()) {
					LOG.warn("channel=[{}] is less the connection.", e
							.getChannel().getId());
				}
			}
		}
		
	}
	
}
