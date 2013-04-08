/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    Receiver.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-27 下午08:10:56
 *******************************************************************************/
package com.toolkit.transport.receiver;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: Receiver.java 391 2011-06-15 08:48:38Z qiaofeng $
 */
public interface Receiver {
	
	void messageReceived(Object msg);
	
}
