/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    AbstractXipResponse.java
 * Creator:     qiaofeng
 * Create-Date: 2011-7-4 上午10:02:57
 *******************************************************************************/
package com.toolkit.transport.protocol;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author qiaofeng
 * 
 */
public abstract class AbstractXipResponse extends AbstractXipSignal implements
		XipResponse {
	
	private int		errorCode;
	
	private String	errorMessage;
	
	public final static <T extends AbstractXipResponse> T createRespForError(
			Class<T> clazz, int errorCode, String errorMessage) {
		T resp;
		try {
			resp = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		
		((AbstractXipResponse) resp).setErrorCode(errorCode);
		((AbstractXipResponse) resp).setErrorMessage(errorMessage);
		
		return resp;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
	
}
