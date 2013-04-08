package com.toolkit.transport.codec;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.toolkit.transport.protocol.XipSignal;
import com.toolkit.transport.protocol.meta.MsgCode2TypeMetainfo;

/**
 * 编码器
 * 
 * @author qiaofeng
 * @version $Id: XipEncoder, v 0.1 2012-8-29 上午11:10:01 Exp $
 */
public class XipEncoder extends OneToOneEncoder {
	
	private final static Logger		LOG				= LoggerFactory
															.getLogger(XipEncoder.class);
	private final Kryo				kryo			= new Kryo();
	private MsgCode2TypeMetainfo	typeMetaInfo;
	private int						dumpBytes		= 256;
	private boolean					isDebugEnabled	= false;
	
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
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		byte[] bytes = null;
		if (msg instanceof XipSignal) {
			
			// 先写通讯握手的Id
			Class<?> clazz = msg.getClass();
			int id = typeMetaInfo.find(clazz);
			buffer.writeInt(id);
			
			// 再对数据进行编码
			Output output = new Output(4096, Integer.MAX_VALUE);
			kryo.writeObject(output, msg);
			bytes = output.toBytes();
			output.flush();
			output = null;
			
			if (null == bytes) {
				LOG.error("encode: [{}] can not generate byte stream.", msg);
				throw new RuntimeException("encode: bean " + msg
						+ " is not XipProtocol.");
			}
			
			// 写消息长度
			buffer.writeInt(bytes.length);
			// 写消息体
			buffer.writeBytes(bytes);
			
			if (LOG.isTraceEnabled()) {
				LOG.trace("bean type [{}] and encode size is [{}]",
						new Object[] { msg.getClass(), bytes.length });
			}
			
		} else if (byte[].class.isAssignableFrom(msg.getClass())) {
			bytes = (byte[]) msg;
		} else {
			LOG.error("encode: [{}] can not generate byte stream.", msg);
			throw new RuntimeException("encode: bean " + msg
					+ " is not XipSignal.");
		}
		
		return buffer;
	}
	
	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}
	
	public void setDebugEnabled(boolean isDebugEnabled) {
		this.isDebugEnabled = isDebugEnabled;
	}
	
	public int getDumpBytes() {
		return dumpBytes;
	}
	
	public void setDumpBytes(int dumpBytes) {
		this.dumpBytes = dumpBytes;
	}
	
	public MsgCode2TypeMetainfo getTypeMetaInfo() {
		return typeMetaInfo;
	}
	
	public void setTypeMetaInfo(MsgCode2TypeMetainfo typeMetaInfo) {
		this.typeMetaInfo = typeMetaInfo;
	}
	
}
