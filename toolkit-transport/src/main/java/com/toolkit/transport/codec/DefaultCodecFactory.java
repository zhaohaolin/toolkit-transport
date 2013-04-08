package com.toolkit.transport.codec;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;

/**
 * @author qiaofeng
 */
public class DefaultCodecFactory implements XipCodecFactory {
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
		if (null != encoder)
			pipeline.addLast("encoder", encoder);
		if (null != decoder)
			pipeline.addLast("decoder", decoder);
		if (null != handler)
			pipeline.addLast("handler", handler);
		
		if (handlers != null && !handlers.isEmpty()) {
			Iterator<Entry<String, ChannelHandler>> iter = handlers.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Entry<String, ChannelHandler> entry = iter.next();
				pipeline.addLast(entry.getKey(), entry.getValue());
			}
		}
		
		return pipeline;
	}
	
	@Override
	public void setHandlers(Map<String, ChannelHandler> handlers) {
		this.handlers = handlers;
	}
	
	@Override
	public void addHandler(String key, ChannelHandler handler) {
		this.handlers.put(key, handler);
	}
	
	@Override
	public void setHandler(ChannelHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void setDecoder(ChannelHandler decoder) {
		this.decoder = decoder;
	}
	
	@Override
	public void setEncoder(ChannelHandler encoder) {
		this.encoder = encoder;
	}
	
	private Map<String, ChannelHandler>	handlers	= new HashMap<String, ChannelHandler>();
	private ChannelHandler				handler;
	private ChannelHandler				decoder;
	private ChannelHandler				encoder;
	
}
