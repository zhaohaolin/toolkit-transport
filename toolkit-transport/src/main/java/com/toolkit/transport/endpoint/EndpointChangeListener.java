/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    IoSessionListener.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 下午02:27:31
 *******************************************************************************/
package com.toolkit.transport.endpoint;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: EndpointChangeListener.java 458 2011-06-22 04:51:31Z
 *          qiaofeng $
 */
public interface EndpointChangeListener {
	
	void addEndpoint(Endpoint endpoint);

	void removeEndpoint(Endpoint endpoint);
	
}
