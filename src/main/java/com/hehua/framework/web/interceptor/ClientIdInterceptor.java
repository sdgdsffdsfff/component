package com.hehua.framework.web.interceptor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hehua.framework.jedis.PoolableJedis;
import com.hehua.framework.jedis.PoolableJedisManager;
import com.hehua.framework.uuid.UuidUtils;
import com.hehua.framework.web.HehuaRequestContext;
import com.hehua.framework.web.constants.CookieKeys;
import com.hehua.framework.web.constants.Domains;
import com.hehua.framework.web.model.AppVer;
import com.hehua.framework.web.util.CookieUtils;
import com.hehua.user.dao.ClientDAO;
import com.hehua.user.model.Client;

/**
 * @author zhihua
 *
 *         TODO 优化性能，每次1次redis读，+ redis写
 */
public class ClientIdInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ClientDAO clientDAO;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        IgnoreIntereptor ignoreIntereptor = MethodInterceptorUtils.getAnnotaion(handler,
                IgnoreIntereptor.class);

        if (ignoreIntereptor != null
                && ArrayUtils.contains(ignoreIntereptor.value(), ClientIdInterceptor.class)) {
            return true;
        }

        // 获取clientId
        boolean newClient = false;
        String clientId = CookieUtils.getCookie(request, CookieKeys.CLIENT_ID);
        if (!UuidUtils.isValidClientId(clientId)) {
            String deviceId = HehuaRequestContext.getDeviceId();
            Pair<String, Boolean> result = getClientIdDeviceId(deviceId);
            clientId = result.getKey();

            newClient = !UuidUtils.isValidClientId(clientId);
            if (newClient) {
                clientId = UuidUtils.generateClientId();
            }

            if (newClient || result.getRight()) {
                AppVer appVer = HehuaRequestContext.getAppVer();
                String appChannel = HehuaRequestContext.getAppChannel();
                String os = HehuaRequestContext.getOs();
                String deviceMode = HehuaRequestContext.getDeviceMode();

                Client client = new Client();
                client.setClientid(clientId);
                client.setAppversion(appVer == null ? "" : appVer.toString());
                client.setOs(StringUtils.defaultString(os));
                client.setChannel(StringUtils.defaultString(appChannel));
                client.setDm(StringUtils.defaultString(deviceMode));
                client.setDeviceid(StringUtils.defaultString(deviceId));
                client.setCts(new Date());

                if (logger.isDebugEnabled()) {
                    logger.debug("new client: url=" + request.getRequestURI() + ", handler="
                            + handler + ", client=" + client);
                }

                clientDAO.create(client);
            }

            CookieUtils.saveCookie(response, CookieKeys.CLIENT_ID, clientId,
                    (int) TimeUnit.DAYS.toSeconds(365 * 50), Domains.DOMAIN_API, "/", true);
        }

        // 获取或者创建traceId
        String traceId = getOrCreateTraceId(clientId, newClient);

        // 刷新过期时间
        if (!newClient) {
            updateTraceIdExpiredTime(clientId, traceId);
        }

        // 设置上下午
        HehuaRequestContext.setClientId(clientId);
        HehuaRequestContext.setTraceId(traceId);

        return true;
    }

    public Pair<String, Boolean> getClientIdDeviceId(String deviceId) {
        String clientId = null;
        if (StringUtils.isBlank(deviceId)) {
            return Pair.of(null, true);
        }

        // 如果最近1分钟deviceId关联着一个clientId
        PoolableJedis jedis = PoolableJedisManager.getDefaultCacheJedis();
        String key = getClientIdKey(deviceId);
        clientId = jedis.get(key);
        if (clientId != null) {
            return Pair.of(clientId, false);
        }

        clientId = UuidUtils.generateClientId();
        String result = jedis.set(key, clientId, "NX", "EX", TimeUnit.MINUTES.toSeconds(1));
        if (StringUtils.equalsIgnoreCase("OK", result)) {
            return Pair.of(clientId, true);
            //            return clientId;
        }
        return Pair.of(jedis.get(key), false);
    }

    public String getOrCreateTraceId(String clientId, boolean newClient) {
        PoolableJedis jedis = PoolableJedisManager.getDefaultCacheJedis();

        String traceidKey = getTraceIdKey(clientId);

        String traceId = null;
        if (!newClient) {
            traceId = jedis.get(traceidKey);
            if (StringUtils.isNotBlank(traceId)) {
                return traceId;
            }
        }

        traceId = UuidUtils.generateTraceId();
        String result = jedis.set(traceidKey, traceId, "NX", "EX", TimeUnit.MINUTES.toSeconds(30));
        if (StringUtils.equalsIgnoreCase("OK", result)) {
            return traceId;
        }
        return jedis.get(traceidKey);
    }

    public void updateTraceIdExpiredTime(String clientId, String traceId) {
        PoolableJedis jedis = PoolableJedisManager.getDefaultCacheJedis();
        String traceIdKey = getTraceIdKey(clientId);
        jedis.expire(traceIdKey, (int) TimeUnit.MINUTES.toSeconds(30));
    }

    /**
     * @param clientId
     * @return
     */
    private String getTraceIdKey(String clientId) {
        return "trace_by_client:" + clientId;
    }

    /**
     * @param deviceId
     * @return
     */
    public String getClientIdKey(String deviceId) {
        return "client_by_device:" + deviceId;
    }

}
