/*
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    IpPortPair.java
 * Creator:     qiaofeng
 * Create-Date: 2011-5-3 上午09:26:58
 */
package com.toolkit.transport.util;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * TCP-IP-PORT对象定义类
 * 
 * @author qiaofeng
 * @version $Id: IpPortPair.java 395 2011-06-15 09:56:52Z qiaofeng $
 */
public class IpPortPair implements Comparable<IpPortPair> {
	
	private String	ip		= "127.0.0.1";
	private int		port	= 0;
	
	public IpPortPair() {
		//
	}
	
	public IpPortPair(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IpPortPair other = (IpPortPair) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
	
	@Override
	public int compareTo(IpPortPair o) {
		int rslt = this.ip.compareTo(o.ip);
		if (0 == rslt) {
			return this.port - o.port;
		}
		return rslt;
	}
}
