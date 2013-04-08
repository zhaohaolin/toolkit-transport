/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    ResponseClosure.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-14 下午06:39:06
 *******************************************************************************/
package com.toolkit.transport.response;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: ResponseClosure.java 546 2011-07-08 04:52:28Z qiaofeng $
 */
public interface ResponseClosure<T> {

    void onResponse(T resp);

}
