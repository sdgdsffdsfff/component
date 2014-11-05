/**
 * 
 */
package com.hehua.framework.web.client;

import java.util.concurrent.TimeUnit;

import com.hehua.framework.cache.AbstractRedisCache;
import com.hehua.framework.jedis.PoolableJedisManager;

/**
 * @author zhihua
 *
 */
public class DeviceInfoStorage extends AbstractRedisCache<String, String> {

    private static final DeviceInfoStorage INSTANCE = new DeviceInfoStorage();

    public static DeviceInfoStorage getInstance() {
        return INSTANCE;
    }

    /**
     * @param jedis
     */
    private DeviceInfoStorage() {
        super(PoolableJedisManager.getJedis("sms"));
    }

    @Override
    public String encode(String object) {
        return object;
    }

    @Override
    public String decode(String object) {
        return object;
    }

    @Override
    public String buildKey(String key) {
        return "device:" + key;
    }

    @Override
    public int getExpire() {
        return (int) TimeUnit.MINUTES.toSeconds(1);
    }

}
