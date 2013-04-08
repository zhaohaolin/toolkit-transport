/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    EndPoint.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-27 下午08:12:06
 *******************************************************************************/
package com.toolkit.transport.endpoint;

import org.jboss.netty.channel.Channel;

import com.toolkit.lang.Closure;
import com.toolkit.transport.cache.Holder;
import com.toolkit.transport.receiver.Receiver;
import com.toolkit.transport.sender.Sender;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: Endpoint.java 458 2011-06-22 04:51:31Z qiaofeng $
 */
public interface Endpoint extends Sender, Receiver {
	
	void stop();
	
	void start();
	
	void setQueueSize(int cachedMessageCount);
	
	void setChannel(Channel channel);
	
	void setNextClosure(Closure nextClosure);
	
	void setReceiver(Receiver receiver);
	
	void setResponseContext(Holder context);
}
