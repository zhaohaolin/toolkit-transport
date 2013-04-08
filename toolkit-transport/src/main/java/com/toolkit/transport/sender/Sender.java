/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    Sender.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-27 下午08:09:03
 *******************************************************************************/
package com.toolkit.transport.sender;

import java.util.concurrent.TimeUnit;

import com.toolkit.transport.response.ResponseClosure;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: Sender.java 547 2011-07-08 04:56:39Z qiaofeng $
 */
public interface Sender {
	
	void send(Object bean);
	
	void send(Object object, ResponseClosure<?> callback);
	
	Object sendAndWait(Object bean);
	
	Object sendAndWait(Object bean, long duration, TimeUnit units);
	
}
