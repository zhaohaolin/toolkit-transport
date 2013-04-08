/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    AddressProvider.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 上午11:21:26
 *******************************************************************************/
package com.toolkit.transport.session;

import java.net.InetSocketAddress;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: AddressProvider.java 390 2011-06-15 08:46:03Z qiaofeng $
 */
public interface AddressProvider {
	
	InetSocketAddress getAddress();
	
}
