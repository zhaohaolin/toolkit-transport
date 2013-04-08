/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    AbstractEndpoint.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-28 下午01:04:51
 *******************************************************************************/
package com.toolkit.transport.endpoint;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toolkit.lang.Closure;
import com.toolkit.transport.cache.DefaultHolder;
import com.toolkit.transport.cache.Holder;
import com.toolkit.transport.protocol.XipSignal;
import com.toolkit.transport.receiver.Receiver;
import com.toolkit.transport.response.ResponseClosure;
import com.toolkit.transport.response.ResponseFuture;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: DefaultEndpoint.java 613 2011-07-27 12:51:04Z qiaofeng $
 */
public class DefaultEndpoint implements Endpoint {
	
	private final static Logger		LOG				= LoggerFactory
															.getLogger(DefaultEndpoint.class);
	protected Closure				nextClosure		= null;									// 下一个业务接口
	protected Receiver				receiver		= null;
	private AtomicReference<Holder>	responseContext	= new AtomicReference<Holder>();
	protected BlockingQueue<Object>	pendings		= new LinkedBlockingQueue<Object>(
															1024);								// 处理阻塞队列
	private Channel					channel			= null;									// 通道
	private ExecutorService			exec			= Executors
															.newSingleThreadExecutor();		// 单线程执行器
	private long					waitTimeout		= 1;										// 等待超时时长
	private int						sendTimeout		= 10000;									// 发送超时时长
																								
	private void addToPending(Object msg) {
		if (null != msg) {
			while (!pendings.offer(msg)) {
				LOG.info("addToPending: offer msg to cache failed, try remove early cached msg.");
				pendings.poll();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void doSend() {
		while (true) {
			Object msg = null;
			
			try {
				msg = pendings.take();
			} catch (InterruptedException e) {
				LOG.error("get mesage:", e);
				return;
			}
			
			if (null != msg) {
				ChannelFuture future = channel.write(msg);
				future.addListener(new ChannelFutureListener() {
					
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if (!future.isSuccess()) {
							// TODO 失败的消息是否重发
							// addToPending(bean);
							if (future.isCancelled()) {
								LOG.error("send mesage failed, reason:", future
										.getCause().getMessage());
							} else {
								LOG.error("send mesage failed without reason");
							}
						}
					}
				});
			}
		}
	}
	
	private void doSendPending() {
		exec.submit(new Runnable() {
			
			@Override
			public void run() {
				sendPending();
			}
		});
	}
	
	private void sendPending() {
		try {
			if (null == channel) {
				Thread.sleep(waitTimeout); // sleep 1s
			} else {
				Object bean = pendings.poll(waitTimeout, TimeUnit.SECONDS);
				if (null != bean) {
					ChannelFuture future = channel.write(bean);
					future.addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future)
								throws Exception {
							if (!future.isSuccess()) {
								// TODO 失败的消息是否重发
								// addToPending(bean);
								if (future.isCancelled()) {
									LOG.error("send msg failed, reason:",
											future.getCause().getMessage());
								} else {
									LOG.error("send msg failed without reason");
								}
							}
						}
						
					});
				}
			}
		} catch (InterruptedException e) {
			// TODO
			// logger.error("get message:", e);
		} finally {
			doSendPending();
		}
	}
	
	@Override
	public void send(Object bean) {
		if (null != bean) {
			addToPending(bean);
		}
	}
	
	@Override
	public void send(Object bean, ResponseClosure<?> closure) {
		if (null != bean) {
			String seqNum = ((XipSignal) bean).getIdentification();
			getResponseContext().put(seqNum, closure);
			addToPending(bean);
		}
	}
	
	@Override
	public Object sendAndWait(Object bean) {
		return sendAndWait(bean, sendTimeout, TimeUnit.MILLISECONDS);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object sendAndWait(Object bean, long duration, TimeUnit units) {
		if (null == bean) {
			return null;
		}
		String seqNum = ((XipSignal) bean).getIdentification();
		ResponseFuture<Object> responseFuture = new ResponseFuture<Object>();
		getResponseContext().put(seqNum, responseFuture);
		
		addToPending(bean);
		try {
			return responseFuture.get(duration, units);
		} catch (Exception e) {
			LOG.error("", e);
			return null;
		} finally {
			responseFuture = (ResponseFuture<Object>) getResponseContext()
					.getAndRemove(seqNum);
			if (responseFuture != null) {
				responseFuture.cancel(false);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void messageReceived(final Object msg) {
		if (msg instanceof XipSignal) {
			String seqNum = ((XipSignal) msg).getIdentification();
			Object object = getResponseContext().getAndRemove(seqNum);
			if (null != object) {
				try {
					if (object instanceof ResponseFuture) {
						((ResponseFuture) object).set(msg);
					}
					if (object instanceof ResponseClosure) {
						((ResponseClosure) object).onResponse(msg);
					}
				} catch (Exception e) {
					LOG.error("onResponse error.", e);
				}
			} else {
				if (this.receiver != null) {
					this.receiver.messageReceived(msg);
				}
			}
		}
		if (null != nextClosure) {
			this.nextClosure.execute(msg);
		}
	}
	
	@Override
	public void stop() {
		this.exec.shutdownNow();
		this.pendings.clear();
		this.responseContext = null;
		this.nextClosure = null;
		this.receiver = null;
		this.channel = null;
	}
	
	@Override
	public void start() {
		exec.submit(new Runnable() {
			
			@Override
			public void run() {
				doSendPending();
			}
			
		});
	}
	
	@Override
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	@Override
	public void setQueueSize(int cachedMessageCount) {
		this.pendings = new LinkedBlockingQueue<Object>(cachedMessageCount);
	}
	
	public int getPendingCount() {
		if (null != this.pendings) {
			return this.pendings.size();
		}
		return -1;
	}
	
	@Override
	public void setNextClosure(Closure nextClosure) {
		this.nextClosure = nextClosure;
	}
	
	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	
	@Override
	public void setResponseContext(Holder cache) {
		this.responseContext.set(cache);
	}
	
	public Holder getResponseContext() {
		Holder ret = responseContext.get();
		if (ret == null) {
			ret = new DefaultHolder();
			responseContext.set(ret);
		}
		return ret;
	}
	
	public void setExec(ExecutorService exec) {
		this.exec = exec;
	}
	
	public void setWaitTimeout(long waitTimeout) {
		this.waitTimeout = waitTimeout;
	}
	
	public void setSendTimeout(int sendTimeout) {
		this.sendTimeout = sendTimeout;
	}
	
}
