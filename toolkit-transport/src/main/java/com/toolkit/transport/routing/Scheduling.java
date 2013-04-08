/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    Routing.java
 * Creator:     qiaofeng
 * Create-Date: 2011-7-7 下午03:32:30
 *******************************************************************************/
package com.toolkit.transport.routing;

/**
 * TCP路由规则定义接口
 * 
 * @author qiaofeng
 * @version $Id: Scheduling.java 654 2011-09-08 09:13:49Z qiaofeng $
 */
public interface Scheduling {
	
	int getTotal();
	
	void setTotal(int total);
	
	int next();
	
	int index();
	
}
