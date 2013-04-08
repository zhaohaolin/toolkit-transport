/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    ResponseTask.java
 * Creator:     qiaofeng
 * Create-Date: 2011-7-8 上午09:25:48
 *******************************************************************************/
package com.toolkit.transport.response;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: ResponseFuture.java 545 2011-07-08 04:50:41Z qiaofeng $
 */
public class ResponseFuture<V> extends FutureTask<V> {
	
	public ResponseFuture() {
		super(new Callable<V>() {
			@Override
			public V call() throws Exception {
				return null;
			}
		});
	}
	
	@Override
	public void set(V v) {
		super.set(v);
	}
}
