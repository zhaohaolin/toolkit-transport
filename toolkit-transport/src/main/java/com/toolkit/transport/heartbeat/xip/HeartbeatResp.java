/**
 * 
 */
package com.toolkit.transport.heartbeat.xip;

import java.util.ArrayList;
import java.util.List;

import com.toolkit.transport.protocol.AbstractXipResponse;
import com.toolkit.transport.protocol.annotation.SignalCode;

/**
 * 心跳响应协议定义
 * 
 * @author qiaofeng
 * @version $Id: HeartbeatResp, v 0.1 2012-9-13 下午09:57:57 Exp $
 */
@SignalCode(messageCode = 200001)
public class HeartbeatResp extends AbstractXipResponse {
	
	private List<ServerGroup>	candidates	= new ArrayList<ServerGroup>();
	
	public List<ServerGroup> getCandidates() {
		return candidates;
	}
	
	public void setCandidates(ArrayList<ServerGroup> candidates) {
		this.candidates = candidates;
	}
	
	public void addServerGroup(ServerGroup group) {
		candidates.add(group);
	}
	
}
