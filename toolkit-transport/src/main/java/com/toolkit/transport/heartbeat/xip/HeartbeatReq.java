/**
 * 
 */
package com.toolkit.transport.heartbeat.xip;

import com.toolkit.transport.protocol.AbstractXipRequest;
import com.toolkit.transport.protocol.annotation.SignalCode;

/**
 * 心跳消息请求协议定义
 * 
 * @author qiaofeng
 * @version $Id: HeartbeatReq, v 0.1 2012-9-13 下午09:54:48 Exp $
 */
@SignalCode(messageCode = 100001)
public class HeartbeatReq extends AbstractXipRequest {
	
	private ServerStatus	serverStatus	= new ServerStatus();
	
	public ServerStatus getServerStatus() {
		return serverStatus;
	}
	
	public void setServerStatus(ServerStatus serverStatus) {
		this.serverStatus = serverStatus;
	}
	
	public ServerCategory getServerCategory() {
		return this.serverStatus.getCategory();
	}
	
	public HeartbeatReq setCategory(String category) {
		getServerCategory().setCategory(category);
		return this;
	}
	
	public String getDomain() {
		return getServerCategory().getDomain();
	}
	
	public String getGroup() {
		return getServerCategory().getGroup();
	}
	
	public void setIp(String ip) {
		this.serverStatus.setIp(ip);
	}
	
	public void setPort(int port) {
		this.serverStatus.setPort(port);
	}
	
	public void setVersion(String version) {
		this.serverStatus.setVersion(version);
	}
	
}
