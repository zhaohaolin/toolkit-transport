/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    DefaultEndpointFactory.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-21 上午09:25:55
 *******************************************************************************/
package com.toolkit.transport.endpoint;

import org.jboss.netty.channel.Channel;

import com.toolkit.lang.Closure;
import com.toolkit.transport.cache.DefaultHolder;
import com.toolkit.transport.cache.Holder;
import com.toolkit.transport.receiver.Receiver;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: DefaultEndpointFactory.java 458 2011-06-22 04:51:31Z qiaofeng $
 */
public class DefaultEndpointFactory implements EndpointFactory {
	
	private Closure		nextClosure			= null;
	private Receiver	receiver			= null;
	private Holder		responseContext		= new DefaultHolder();
	private int			cachedMessageCount	= 1024;
	
	@Override
	public Endpoint createEndpoint(Channel channel) {
		Endpoint endpoint = new DefaultEndpoint();
		
		endpoint.setChannel(channel);
		endpoint.setQueueSize(this.cachedMessageCount);
		endpoint.setNextClosure(this.nextClosure);
		endpoint.setReceiver(this.receiver);
		endpoint.setResponseContext(this.responseContext);
		
		endpoint.start();
		
		return endpoint;
	}
	
	public Closure getNextClosure() {
		return nextClosure;
	}
	
	@Override
	public void setNextClosure(Closure nextClosure) {
		this.nextClosure = nextClosure;
	}
	
	public Receiver getReceiver() {
		return receiver;
	}
	
	@Override
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	
	public Holder getResponseContext() {
		return responseContext;
	}
	
	@Override
	public void setResponseContext(Holder responseContext) {
		this.responseContext = responseContext;
	}
	
	public int getCachedMessageCount() {
		return cachedMessageCount;
	}
	
	@Override
	public void setCachedMessageCount(int cachedMessageCount) {
		this.cachedMessageCount = cachedMessageCount;
	}
	
}
