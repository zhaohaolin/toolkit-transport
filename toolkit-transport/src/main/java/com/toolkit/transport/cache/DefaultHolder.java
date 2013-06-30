/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    DefaultHolder.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 上午09:32:47
 *******************************************************************************/
package com.toolkit.transport.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: DefaultHolder.java 381 2011-06-15 02:31:55Z qiaofeng $
 */
public class DefaultHolder implements Holder {
	
	// Use this cached map for all app
	private final static ConcurrentMap<Object, Object>	map	= new ConcurrentHashMap<Object, Object>();
	
	@Override
	public void put(Object key, Object value) {
		map.put(key, value);
	}
	
	@Override
	public Object get(Object key) {
		return map.get(key);
	}
	
	@Override
	public Object getAndRemove(Object key) {
		Object ret = map.get(key);
		map.remove(key);
		return ret;
	}
	
	@Override
	public void remove(Object key) {
		map.remove(key);
	}
	
}
