/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    IoSessionRepository.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 下午02:28:55
 *******************************************************************************/
package com.toolkit.transport.endpoint;

import java.util.List;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: EndpointRepository.java 460 2011-06-22 04:57:17Z qiaofeng $
 */
public interface EndpointRepository extends EndpointChangeListener {

	List<Endpoint> getEndpoints();

	Endpoint getEndpoint();

	void setMaxSession(int maxSession);

	int getMaxSession();

	boolean isFull();

}
