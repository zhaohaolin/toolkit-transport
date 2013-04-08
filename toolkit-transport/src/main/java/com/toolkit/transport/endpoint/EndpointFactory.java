/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    EndpointFactory.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-21 上午09:24:40
 *******************************************************************************/
package com.toolkit.transport.endpoint;

import org.jboss.netty.channel.Channel;

import com.toolkit.lang.Closure;
import com.toolkit.transport.cache.Holder;
import com.toolkit.transport.receiver.Receiver;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: EndpointFactory.java 568 2011-07-12 12:13:33Z qiaofeng $
 */
public interface EndpointFactory {
	
	Endpoint createEndpoint(Channel channel);
	
	void setNextClosure(Closure nextClosure);
	
	void setReceiver(Receiver receiver);
	
	void setResponseContext(Holder responseContext);
	
	void setCachedMessageCount(int cachedMessageCount);
	
}
