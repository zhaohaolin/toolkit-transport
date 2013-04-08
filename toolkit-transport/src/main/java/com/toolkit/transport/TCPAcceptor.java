package com.toolkit.transport;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
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
import com.toolkit.transport.endpoint.Endpoint;
import com.toolkit.transport.endpoint.EndpointFactory;
import com.toolkit.transport.receiver.Receiver;
import com.toolkit.transport.util.TransportUtil;

/**
 * @author qiaofeng
 */
public class TCPAcceptor {
	
	private final static Logger		LOG				= LoggerFactory
															.getLogger(TCPAcceptor.class);
	private EndpointFactory			endpointFactory	= new DefaultEndpointFactory();
	private String					ip				= "127.0.0.1";
	private int						port			= 7777;
	private int						maxRetryCount	= 20;
	private long					retryTimeout	= 30 * 1000;							// 30s
	private ServerBootstrap			bootstrap;
	private List<String>			options;
	private XipCodecFactory			codecFactory;
	private InternalLoggerFactory	loggerFactory	= new Slf4JLoggerFactory();
	
	public void start() throws Exception {
		
		// 注册日志
		InternalLoggerFactory.setDefaultFactory(loggerFactory);
		
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("init the tcp acceptor bossExecutor and workExecutor ok.");
		}
		
		// set codecFactory and set handler
		this.codecFactory
				.addHandler("acceptorHandler", new AcceptorIOHandler());
		this.codecFactory.addHandler("timeout", new IdleStateHandler(
				new HashedWheelTimer(), 10, 10, 0));
		this.codecFactory.addHandler("heartbeat", new HeartBeatHandler());
		bootstrap.setPipelineFactory(this.codecFactory);
		
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("reuseAddress", true);
		
		// set options
		if (null != options && options.size() > 0) {
			for (String option : options) {
				bootstrap.setOption(option, true);
			}
		}
		int retryCount = 0;
		boolean binded = false;
		do {
			try {
				bootstrap.bind(new InetSocketAddress(this.ip, this.port));
				binded = true;
			} catch (Exception e) {
				LOG.warn("start failed on port:[{}], " + e + ", and retry...",
						port);
				// 对绑定异常再次进行尝试
				retryCount++;
				if (retryCount >= maxRetryCount) {
					// 超过最大尝试次数
					throw e;
				}
				try {
					Thread.sleep(retryTimeout);
				} catch (InterruptedException e1) {
					//
				}
			}
		} while (!binded);
		
		if (LOG.isInfoEnabled()) {
			LOG.info("start succeed in [{}]:[{}]", new Object[] { ip, port });
		}
		
	}
	
	public void stop() {
		if (null != bootstrap) {
			bootstrap.shutdown();
			bootstrap = null;
		}
	}
	
	public void setAcceptIp(String acceptIp) {
		this.ip = acceptIp;
	}
	
	public void setAcceptPort(int acceptPort) {
		this.port = acceptPort;
	}
	
	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}
	
	public void setRetryTimeout(long retryTimeout) {
		this.retryTimeout = retryTimeout;
	}
	
	public void setLoggerFactory(InternalLoggerFactory loggerFactory) {
		this.loggerFactory = loggerFactory;
	}
	
	public void setOptions(List<String> options) {
		this.options = options;
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
	
	public void setCodecFactory(XipCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
	
	// private:
	private class AcceptorIOHandler extends SimpleChannelHandler {
		
		public AcceptorIOHandler() {
			super();
		}
		
		// 被连接上时
		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			// 注册endpoint
			Endpoint endpoint = endpointFactory.createEndpoint(e.getChannel());
			if (null != endpoint) {
				TransportUtil.addEndpointToChannel(e.getChannel(), endpoint);
			}
		}
		
		// 被开启时
		@Override
		public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			if (LOG.isInfoEnabled()) {
				LOG.info("channelOpened: channel [" + e.getChannel() + "]");
			}
		}
		
		// 接收消息时：
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			Object msg = e.getMessage();
			if (LOG.isTraceEnabled()) {
				LOG.trace("messageReceived: " + msg);
			}
			
			// 从连接上取得endpoint,并处理业务
			Endpoint endpoint = TransportUtil.getEndpointOfChannel(e
					.getChannel());
			if (null != endpoint) {
				endpoint.messageReceived(TransportUtil.attachSender(msg,
						endpoint));
			} else {
				LOG.warn("missing endpoint, ignore incoming msg:", msg);
			}
		}
		
		// 关闭时
		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			Channel channel = e.getChannel();
			if (LOG.isDebugEnabled()) {
				LOG.debug("channel: " + channel.getId());
			}
			Endpoint endpoint = TransportUtil.getEndpointOfChannel(channel);
			if (null != endpoint) {
				endpoint.stop();
			}
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
			LOG.warn("Unexpected exception from downstream." + e.getCause());
			// 解码有错误的情况下，session不关闭
			// e.getChannel().close();
		}
		
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
