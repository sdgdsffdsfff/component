/**
 * 
 */
package com.hehua.framework.web.client;

/**
 * @author zhihua
 *
 */
public class ClientInfo {

    private String traceId;

    private long lastAccessTime;

    /**
     * 
     */
    public ClientInfo() {
        super();
    }

    /**
     * @param traceId
     * @param lastAccessTime
     */
    public ClientInfo(String traceId, long lastAccessTime) {
        super();
        this.traceId = traceId;
        this.lastAccessTime = lastAccessTime;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

}
