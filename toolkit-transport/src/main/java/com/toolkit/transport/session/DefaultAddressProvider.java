/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    DefaultAddressProvider.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 上午11:21:57
 *******************************************************************************/
package com.toolkit.transport.session;

import java.net.InetSocketAddress;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: DefaultAddressProvider.java 390 2011-06-15 08:46:03Z qiaofeng $
 */
public class DefaultAddressProvider implements AddressProvider {
	
	private String	destIp		= "127.0.0.1";
	private int		destPort	= 80;
	
	public DefaultAddressProvider(String ip, int port) {
		this.destIp = ip;
		this.destPort = port;
	}
	
	/**
	 * @param destIp
	 *            the destIp to set
	 */
	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}
	
	/**
	 * @param destPort
	 *            the destPort to set
	 */
	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return new InetSocketAddress(destIp, destPort);
	}
	
}
