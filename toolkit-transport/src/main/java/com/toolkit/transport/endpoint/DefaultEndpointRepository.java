/*******************************************************************************
 * CopyRight (c) 2005-2011 GLOBE Co, Ltd. All rights reserved.
 * Filename:    DefaultSessionFactory.java
 * Creator:     qiaofeng
 * Create-Date: 2011-6-15 下午02:30:26
 *******************************************************************************/
package com.toolkit.transport.endpoint;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author qiaofeng
 * @version $Id: DefaultSessionRepository.java 390 2011-06-15 08:46:03Z
 *          qiaofeng $
 */
public class DefaultEndpointRepository implements EndpointRepository {

    private Logger         log          = LoggerFactory
                                            .getLogger(DefaultEndpointRepository.class);
    private List<Endpoint> sessionStore = new CopyOnWriteArrayList<Endpoint>();
    private AtomicInteger  sessionIdx   = new AtomicInteger(0);
    private int            maxSessions  = 1;

    @Override
    public void addEndpoint(Endpoint endpoint) {
        sessionStore.add(endpoint);
    }

    @Override
    public void removeEndpoint(Endpoint endpoint) {
        sessionStore.remove(endpoint);
    }

    @Override
    public List<Endpoint> getEndpoints() {
        return sessionStore;
    }

    @Override
    public Endpoint getEndpoint() {
        List<Endpoint> endpoints = getEndpoints();
        while (0 == endpoints.size()) {
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                log.error("send :", e);
            }
            endpoints = getEndpoints();
        }

        if (endpoints.size() == 1) {
            return endpoints.get(0);
        }

        // decide which idx to get session
        int idx = sessionIdx.getAndIncrement();
        if (idx >= endpoints.size()) {
            idx = 0;
            sessionIdx.set(idx);
        }

        return endpoints.get(idx);
    }

    @Override
    public void setMaxSession(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    @Override
    public boolean isFull() {
        return sessionStore.size() >= maxSessions;
    }

    @Override
    public int getMaxSession() {
        return this.maxSessions;
    }

}
