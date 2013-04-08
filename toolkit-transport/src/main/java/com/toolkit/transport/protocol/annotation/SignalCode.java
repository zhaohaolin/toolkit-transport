/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    SignalCode.java
 * Creator:     qiaofeng
 * Create-Date: 2011-4-28 上午10:54:39
 *******************************************************************************/
package com.toolkit.transport.protocol.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: SignalCode.java 283 2011-06-07 05:16:40Z qiaofeng $
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SignalCode {

	int messageCode();

}
