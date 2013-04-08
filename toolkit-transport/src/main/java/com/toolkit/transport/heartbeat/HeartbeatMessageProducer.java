/**
 * 
 */
package com.toolkit.transport.heartbeat;

import java.util.concurrent.Callable;

import com.toolkit.lang.AppInfo;
import com.toolkit.transport.heartbeat.xip.HeartbeatReq;

/**
 * @author qiaofeng
 * 
 */
public class HeartbeatMessageProducer implements Callable<HeartbeatReq> {

    private String  ip;
    private int     port;
    private String  category;

    private AppInfo appInfo;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    protected HeartbeatReq setCommonAttr(HeartbeatReq req) {
        req.setCategory(category);
        req.setIp(ip);
        req.setPort(port);

        //设置版本号
        if (appInfo != null) {
            req.setVersion(appInfo.getAppVersion());
        }

        return req;
    }

    @Override
    public HeartbeatReq call() {
        HeartbeatReq req = new HeartbeatReq();
        this.setCommonAttr(req);
        return req;
    }

}
