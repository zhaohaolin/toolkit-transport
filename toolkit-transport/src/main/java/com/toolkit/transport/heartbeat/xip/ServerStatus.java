/**
 * 
 */
package com.toolkit.transport.heartbeat.xip;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author qiaofeng
 * 
 */
public class ServerStatus {
	
	/** 通信服务器IP */
	private String			ip;
	
	/** 通信服务器端口 */
	private int				port;
	
	private String			version;
	
	private ServerCategory	category	= new ServerCategory();
	
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
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public ServerCategory getCategory() {
		return category;
	}
	
	public void setCategory(ServerCategory category) {
		this.category = category;
	}
	
	public void setDomain(String domain) {
		this.category.setDomain(domain);
	}
	
	public void setGroup(String group) {
		this.category.setGroup(group);
	}
	
	public boolean isSameDomain(ServerStatus server) {
		return this.category.isSameDomain(server.getCategory());
	}
	
	public String getDomain() {
		return this.category.getDomain();
	}
	
	public String getGroup() {
		return this.category.getGroup();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
