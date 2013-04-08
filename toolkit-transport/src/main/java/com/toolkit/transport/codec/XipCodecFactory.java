package com.toolkit.transport.codec;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipelineFactory;

/**
 * @author qiaofeng
 */
public interface XipCodecFactory extends ChannelPipelineFactory {
	
	void setHandlers(Map<String, ChannelHandler> handlers);
	
	void addHandler(String key, ChannelHandler handler);
	
	void setHandler(ChannelHandler handler);
	
	void setDecoder(ChannelHandler handler);
	
	void setEncoder(ChannelHandler handler);
	
}
