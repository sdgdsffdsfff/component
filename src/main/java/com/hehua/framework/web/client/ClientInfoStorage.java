/**
 * 
 */
package com.hehua.framework.web.client;

import com.alibaba.fastjson.JSON;
import com.hehua.framework.cache.AbstractRedisCache;
import com.hehua.framework.jedis.PoolableJedisManager;

/**
 * @author zhihua
 *
 */
public class ClientInfoStorage extends AbstractRedisCache<String, ClientInfo> {

    private static final ClientInfoStorage INSTANCE = new ClientInfoStorage();

    public static ClientInfoStorage getInstance() {
        return INSTANCE;
    }

    /**
     * @param jedis
     */
    private ClientInfoStorage() {
        super(PoolableJedisManager.getJedis("sms"));
    }

    @Override
    public String encode(ClientInfo object) {
        return JSON.toJSONString(object);
    }

    @Override
    public ClientInfo decode(String object) {
        return JSON.parseObject(object, ClientInfo.class);
    }

    @Override
    public String buildKey(String key) {
        return "client:" + key;
    }

    @Override
    public int getExpire() {
        return 0;
    }

}
