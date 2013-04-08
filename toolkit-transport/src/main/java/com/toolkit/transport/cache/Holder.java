/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    Holder.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 上午09:28:09
 *******************************************************************************/
package com.toolkit.transport.cache;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: Holder.java 381 2011-06-15 02:31:55Z qiaofeng $
 */
public interface Holder {
	
	public void put(Object key, Object value);
	
	public Object get(Object key);
	
	public Object getAndRemove(Object key);
	
	public void remove(Object key);
	
}
