/**
 * 
 */
package com.hehua.framework.uuid;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhihua
 *
 */
public class UuidUtils {

    public boolean isValidUuid(String str) {
        return false;
    }

    public static String generateTraceId() {
        //TODO 加密，签名
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString();
    }

    public static boolean isValidTraceId(String traceId) {
        if (StringUtils.isBlank(traceId)) {
            return false;
        }

        if (StringUtils.length(traceId) < 36) {
            return false;
        }

        return true;
    }

    public static String generateClientId() {
        //TODO
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString();
    }

    public static boolean isValidClientId(String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return false;
        }

        if (StringUtils.length(clientId) < 36) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        String uuid = UUID.randomUUID().toString();

        System.out.println(uuid.length());
        DES des = new DES("xxadiadf".getBytes());
        for (int i = 0; i < 100; i++) {
            String encodeString = des.encodeBase64URLSafeString(uuid);
            System.out.println(encodeString);
            System.out.println(des.decodeBase64String(encodeString));
        }

        System.out.println(des.encodeBase64URLSafeStringLong(10000000000L));
    }
}
