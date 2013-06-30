/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    DefaultMsgCode2TypeMetainfo.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-28 上午09:47:26
 *******************************************************************************/
package com.toolkit.transport.protocol.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: DefaultMsgCode2TypeMetainfo.java 283 2011-06-07 05:16:40Z
 *          qiaofeng $
 */
public class DefaultMsgCode2TypeMetainfo implements MsgCode2TypeMetainfo {
	
	private final static Map<Integer, Class<?>>	codes	= new ConcurrentHashMap<Integer, Class<?>>();
	private final static Map<Class<?>, Integer>	codes1	= new ConcurrentHashMap<Class<?>, Integer>();
	
	@Override
	public List<Class<?>> getAllXip() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		for (Map.Entry<Integer, Class<?>> entry : codes.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}
	
	@Override
	public Class<?> find(int value) {
		return codes.get(value);
	}
	
	@Override
	public int find(Class<?> clazz) {
		return codes1.get(clazz);
	}
	
	public void add(int tag, Class<?> type) {
		codes.put(tag, type);
		codes1.put(type, tag);
	}
	
	public static Map<Integer, String> getAllMetainfo() {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		for (Entry<Integer, Class<?>> entry : codes.entrySet()) {
			ret.put(entry.getKey(), entry.getValue().toString());
		}
		return ret;
	}
}
