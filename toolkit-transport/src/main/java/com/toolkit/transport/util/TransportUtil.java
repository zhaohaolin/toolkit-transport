/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    TransportUtil.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-27 下午08:10:00
 *******************************************************************************/
package com.toolkit.transport.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelLocal;

import com.toolkit.lang.Propertyable;
import com.toolkit.transport.endpoint.Endpoint;
import com.toolkit.transport.sender.Sender;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: TransportUtil.java 458 2011-06-22 04:51:31Z qiaofeng $
 */
public abstract class TransportUtil {
	
	private static final String											TRANSPORT_ENDPOINT	= "TRANSPORT_ENDPOINT";
	private static final String											TRANSPORT_SENDER	= "TRANSPORT_SENDER";
	private static final ChannelLocal<ConcurrentMap<String, Object>>	LOCAL				= new ChannelLocal<ConcurrentMap<String, Object>>();
	
	public final static void setAttribute(Channel channel, String key,
			Object value) {
		ConcurrentMap<String, Object> attributeMap = LOCAL.get(channel);
		if (null == attributeMap) {
			attributeMap = new ConcurrentHashMap<String, Object>();
			LOCAL.set(channel, attributeMap);
		}
		attributeMap.put(key, value);
	}
	
	public final static Object getAttribute(Channel channel, String key) {
		ConcurrentMap<String, Object> attributeMap = LOCAL.get(channel);
		if (null == attributeMap) {
			attributeMap = new ConcurrentHashMap<String, Object>();
			LOCAL.set(channel, attributeMap);
		}
		return attributeMap.get(key);
	}
	
	public final static boolean removeAttribute(Channel channel, String key) {
		boolean bool = false;
		ConcurrentMap<String, Object> attributeMap = LOCAL.get(channel);
		if (null == attributeMap) {
			bool = false;
		} else {
			Object obj = attributeMap.remove(key);
			if (null != obj)
				bool = true;
		}
		return bool;
	}
	
	public final static void addEndpointToChannel(Channel channel,
			Endpoint endpoint) {
		setAttribute(channel, TRANSPORT_ENDPOINT, endpoint);
	}
	
	public final static Endpoint getEndpointOfChannel(Channel channel) {
		return (Endpoint) getAttribute(channel, TRANSPORT_ENDPOINT);
	}
	
	public final static Object attachSender(Object propertyable, Sender sender) {
		if (propertyable instanceof Propertyable) {
			((Propertyable) propertyable).setProperty(TRANSPORT_SENDER, sender);
		}
		return propertyable;
	}
	
	public final static Sender getSenderOf(Object propertyable) {
		if (propertyable instanceof Propertyable) {
			return (Sender) ((Propertyable) propertyable)
					.getProperty(TRANSPORT_SENDER);
		}
		return null;
	}
}
