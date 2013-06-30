package com.toolkit.transport.codec;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.toolkit.transport.protocol.meta.MsgCode2TypeMetainfo;

/**
 * 解码器
 * 
 * @author qiaofeng
 * @version $Id: XipDecoder, v 0.1 2012-8-29 上午11:10:57 Exp $
 */
public class XipDecoder extends FrameDecoder {
	
	// private:
	private final static Logger		LOG					= LoggerFactory
																.getLogger(XipDecoder.class);
	private final static Kryo		kryo				= new Kryo();
	private MsgCode2TypeMetainfo	typeMetaInfo;
	private int						maxMessageLength	= -1;
	private int						dumpBytes			= 256;
	private boolean					isDebugEnabled;
	
	// 注册所有协议类
	public void init() {
		kryo.setReferences(true);
		List<Class<?>> list = typeMetaInfo.getAllXip();
		for (Class<?> class1 : list) {
			kryo.register(class1);
		}
		
		// UUIDs don't have a no-arg constructor.
		kryo.register(java.util.UUID.class);
	}
	
	/**
	 * 解码方法
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		if (buffer.readable()) {
			// 读消息Id
			int msgId = buffer.readInt();
			Class<?> clazz = typeMetaInfo.find(msgId);
			// 读数据长度
			int offset = buffer.readInt();
			
			byte[] bytes = new byte[offset];
			buffer.readBytes(bytes);
			Input input = new Input(bytes);
			Object obj = kryo.readObject(input, clazz);
			input.close();
			input = null;
			if (LOG.isTraceEnabled()) {
				LOG.trace("decoder obj = [{}]", obj);
			}
			return obj;
		}
		return null;
	}
	
	public void setDumpBytes(int dumpBytes) {
		this.dumpBytes = dumpBytes;
	}
	
	public int getDumpBytes() {
		return dumpBytes;
	}
	
	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}
	
	public void setDebugEnabled(boolean isDebugEnabled) {
		this.isDebugEnabled = isDebugEnabled;
	}
	
	public MsgCode2TypeMetainfo getTypeMetaInfo() {
		return typeMetaInfo;
	}
	
	public void setTypeMetaInfo(MsgCode2TypeMetainfo typeMetaInfo) {
		this.typeMetaInfo = typeMetaInfo;
	}
	
	public int getMaxMessageLength() {
		return maxMessageLength;
	}
	
	public void setMaxMessageLength(int maxMessageLength) {
		this.maxMessageLength = maxMessageLength;
	}
	
}
