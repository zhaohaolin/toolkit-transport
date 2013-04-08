/**
 * 
 */
package com.toolkit.transport.heartbeat.xip;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author qiaofeng
 * 
 */
public class ServerGroup {
	
	private String				serverType;
	
	private List<ServerStatus>	servers	= new ArrayList<ServerStatus>();
	
	public String getServerType() {
		return serverType;
	}
	
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	
	public List<ServerStatus> getServers() {
		return servers;
	}
	
	public void setServers(ArrayList<ServerStatus> servers) {
		this.servers = servers;
	}
	
	public void addServer(ServerStatus server) {
		this.servers.add(server);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
