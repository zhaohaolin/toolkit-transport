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
public class ServerCategory {
	
	/** 通信服务器的域{如：spider, search等，一般以业务组为单位} */
	private String	domain;
	
	/** 通信服务器的组{代表子系统的简短名称：如ccs, ics, tcs, tms等} */
	private String	group;
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public void setCategory(String category) {
		int idx = category.indexOf('@');
		if (-1 != idx) {
			setDomain(category.substring(idx + 1));
			setGroup(category.substring(0, idx));
		} else {
			setDomain(category);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
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
		ServerCategory other = (ServerCategory) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		return true;
	}
	
	public boolean isSameDomain(ServerCategory other) {
		if (null == other) {
			return false;
		}
		
		if (domain == null) {
			return (null == other.domain);
		}
		return domain.equals(other.domain);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
