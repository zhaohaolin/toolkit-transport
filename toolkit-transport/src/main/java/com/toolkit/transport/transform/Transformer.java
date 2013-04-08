/*
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    Transformer.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-14 下午03:56:12
 */
package com.toolkit.transport.transform;

/**
 * 对象转换定义接口
 * 
 * @author qiaofeng
 * @version $Id: Transformer.java 377 2011-06-14 07:48:59Z qiaofeng $
 */
public interface Transformer<FROM, TO> {
	
	public TO transform(FROM from);
	
}
