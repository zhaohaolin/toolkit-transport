/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    AbstractXipSignal.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-28 上午09:33:47
 *******************************************************************************/
package com.toolkit.transport.protocol;

import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.toolkit.lang.DefaultPropertiesSupport;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: AbstractXipSignal.java 283 2011-06-07 05:16:40Z qiaofeng $
 */
public abstract class AbstractXipSignal extends DefaultPropertiesSupport
		implements XipSignal {
	
	private String	uuid	= UUID.randomUUID().toString();
	
	@Override
	public void setIdentification(String id) {
		this.uuid = id;
	}
	
	@Override
	public String getIdentification() {
		return this.uuid;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractXipSignal other = (AbstractXipSignal) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
	
}
