/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    TCPSessionController.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 上午11:04:55
 *******************************************************************************/
package com.toolkit.transport.session;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.transport.endpoint.Endpoint;
import com.toolkit.transport.endpoint.EndpointFactory;
import com.toolkit.transport.endpoint.EndpointRepository;
import com.toolkit.transport.util.TransportUtil;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: IoSessionController.java 458 2011-06-22 04:51:31Z qiaofeng $
 */
public class IoChannelController {
	
	private final static Logger			LOG					= LoggerFactory
																	.getLogger(IoChannelController.class);
	private ScheduledExecutorService	exec				= Executors
																	.newSingleThreadScheduledExecutor();
	private ClientBootstrap				client				= null;
	private long						reconnectTimeout	= 1;
	private ScheduledFuture<?>			lunchConnectFuture	= null;
	private ChannelFuture				connectFuture		= null;
	private AddressProvider				addressProvider		= null;
	
	private EndpointRepository			endpointRepository;
	private EndpointFactory				endpointFactory;
	
	public IoChannelController(ClientBootstrap client) {
		this.client = client;
		this.client.getPipeline().addLast("controller",
				new SimpleChannelHandler() {
					
					@Override
					public void channelClosed(ChannelHandlerContext ctx,
							ChannelStateEvent e) throws Exception {
						final Channel channel = e.getChannel();
						exec.submit(new Runnable() {
							
							@Override
							public void run() {
								LOG.debug("channelClosed: remove "
										+ channel.getId() + " ok.");
								Endpoint endpoint = TransportUtil
										.getEndpointOfChannel(channel);
								if (null != endpoint) {
									endpoint.stop();
								}
								endpointRepository.removeEndpoint(endpoint);
								LOG.info("channelClosed: " + channel.getId()
										+ " closed.");
							}
							
						});
					}
					
				});
		
	}
	
	private void doScheduleNextConnect() {
		if (null == lunchConnectFuture || lunchConnectFuture.isDone()) {
			lunchConnectFuture = exec.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						doConnect();
					} catch (Exception e) {
						LOG.error("[{}]" + e);
					}
				}
			}, reconnectTimeout, TimeUnit.SECONDS);
			
			if (LOG.isTraceEnabled()) {
				LOG.trace("doScheduleNextConnect: next connect scheduled");
			}
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("doScheduleNextConnect: next connect !NOT! scheduled.");
			}
		}
	}
	
	private void doConnect() {
		
		exec.submit(new Runnable() {
			
			@Override
			public void run() {
				doScheduleNextConnect();
			}
			
		});
		
		if (endpointRepository.isFull()) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("doConnect: reach max channel: "
						+ endpointRepository.getMaxSession()
						+ ", cancel this action.");
			}
			return;
		}
		
		if (null != connectFuture && !connectFuture.isDone()) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("doConnect: connect already doing, cancel this action.");
			}
			return;
		}
		
		if (null == addressProvider) {
			LOG.error("address provider is null.");
			return;
		}
		
		if (LOG.isInfoEnabled()) {
			LOG.info("start connect using address provider");
		}
		
		InetSocketAddress addr = addressProvider.getAddress();
		if (null != addr) {
			if (LOG.isInfoEnabled()) {
				LOG.info("start connect [{}:{}]", addr.getAddress()
						.getHostAddress(), addr.getPort());
			}
			try {
				// 连接Socket
				connectFuture = client.connect(addr);
			} catch (Exception e) {
				LOG.error("[{}]" + e);
			}
		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("failed to using address provider get address");
			}
			return;
		}
		
		// 监听连接事件
		connectFuture.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(final ChannelFuture connectFuture) {
				// 连接完成后
				exec.submit(new Runnable() {
					
					@Override
					public void run() {
						onConnectComplete(connectFuture);
					}
					
				});
			}
		});
	}
	
	// 连接完成后要处理的业务
	private void onConnectComplete(ChannelFuture future) {
		Channel channel = future.getChannel();
		if (channel.isOpen()) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("onConnectComplete: channel [" + channel
						+ "] connected.");
			}
			
			// 创建endpoint并注册
			Endpoint endpoint = endpointFactory.createEndpoint(channel);
			TransportUtil.addEndpointToChannel(channel, endpoint);
			endpointRepository.addEndpoint(endpoint);
			
		} else {
			// not connected
			LOG.error("onConnectComplete: channel [" + channel
					+ "] connect failed.");
			if (null != connectFuture) {
				connectFuture = null;
			}
		}
	}
	
	public void start() {
		
		exec.submit(new Runnable() {
			
			@Override
			public void run() {
				doConnect();
			}
			
		});
	}
	
	public void stop() {
		exec.shutdownNow();
	}
	
	public void setReconnectTimeout(long reconnectTimeout) {
		this.reconnectTimeout = reconnectTimeout;
	}
	
	public void setAddressProvider(AddressProvider addressProvider) {
		this.addressProvider = addressProvider;
	}
	
	public void setEndpointRepository(EndpointRepository endpointRepository) {
		this.endpointRepository = endpointRepository;
	}
	
	public void setEndpointFactory(EndpointFactory endpointFactory) {
		this.endpointFactory = endpointFactory;
	}
	
}
