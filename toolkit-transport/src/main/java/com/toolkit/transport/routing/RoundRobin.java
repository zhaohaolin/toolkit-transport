/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    RoutingRR.java
 * Creator:     qiaofeng
 * Create-Date: 2011-7-7 下午03:35:20
 *******************************************************************************/
package com.toolkit.transport.routing;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TCP路由规则 robin
 * 
 * @author qiaofeng
 * @version $Id: RoundRobin.java 659 2011-09-14 04:26:47Z qiaofeng $
 */
public class RoundRobin implements Scheduling {
	
	private int				total;
	private AtomicInteger	index	= new AtomicInteger(0);
	
	@Override
	public synchronized void setTotal(int total) {
		this.total = total;
		this.index.set(0);
	}
	
	@Override
	public synchronized int next() {
		if (total > 0) {
			int next = index.getAndIncrement();
			if (next < 0) {
				next = 0;
				index.set(next);
			}
			return next % total;
		}
		return -1;
	}
	
	@Override
	public synchronized int getTotal() {
		return total;
	}
	
	@Override
	public int index() {
		return index.get();
	}
	
}
