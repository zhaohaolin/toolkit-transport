/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    First.java
 * Creator:     qiaofeng
 * Create-Date: 2011-7-7 下午03:46:41
 *******************************************************************************/
package com.toolkit.transport.routing;

/**
 * TCP路由规则 chooseFirst
 * 
 * @author qiaofeng
 * @version $Id: ChooseFirst.java 654 2011-09-08 09:13:49Z qiaofeng $
 */
public class ChooseFirst implements Scheduling {
	
	private int	total;
	
	@Override
	public void setTotal(int total) {
		this.total = total;
	}
	
	@Override
	public int next() {
		if (total > 0) {
			return 0;
		}
		return -1;
	}
	
	@Override
	public int getTotal() {
		return total;
	}
	
	@Override
	public int index() {
		if (total > 0) {
			return 0;
		}
		return -1;
	}
	
}
