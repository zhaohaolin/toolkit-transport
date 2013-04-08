/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    AbstractXipNotify.java
 * Creator:     qiaofeng
 * Create-Date: 2011-7-4 上午10:04:34
 *******************************************************************************/
package com.toolkit.transport.protocol;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author qiaofeng
 * 
 */
public abstract class AbstractXipNotify extends AbstractXipSignal implements
		XipNotify {
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
	
}
