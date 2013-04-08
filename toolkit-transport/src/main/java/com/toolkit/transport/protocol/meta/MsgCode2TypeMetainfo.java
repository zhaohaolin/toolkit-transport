/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    MsgCode2TypeMetainfo.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-28 上午09:46:29
 *******************************************************************************/
package com.toolkit.transport.protocol.meta;

import java.util.List;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: MsgCode2TypeMetainfo.java 283 2011-06-07 05:16:40Z qiaofeng $
 */
public interface MsgCode2TypeMetainfo {
	
	List<Class<?>> getAllXip();
	
	Class<?> find(int id);
	
	int find(Class<?> clazz);
	
}
