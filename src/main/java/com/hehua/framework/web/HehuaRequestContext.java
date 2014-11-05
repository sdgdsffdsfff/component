/**
 * 
 */
package com.hehua.framework.web;

import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.hehua.commons.model.UserAccessInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hehua.commons.utils.IPUtil;
import com.hehua.framework.web.model.AppVer;
import com.hehua.framework.web.model.Platform;

/**
 * @author zhihua
 *
 */
public abstract class HehuaRequestContext extends RequestContextHolder {

    private static final String USER_ID = "__CURRENT_LOGINED_USERID__";

    private static final String APP_VER = "__APP_VER__";

    private static final String APP_PLATFORM = "__APP_PLATFORM__";

    private static final String APP_CHANNEL = "__APP_CHANNEL__";

    private static final String TRACE_ID = "__TRACE_ID__";

    private static final String DEVICE_ID = "__DEVICE_ID__";

    private static final String CLIENT_ID = "__CLIENT_ID__";

    private static final String ACCESS_LOCATION = "__ACCESS_LOCATION__";

    private static final String USER_AGENT = "__USER_AGENT__";

    private static final String USER_ACCESS_INFO = "__USER_ACCESS_INFO__";

    private static final String DEVICE_MODE = "__DEVICE_MODE__";

    private static final String OS = "__CLIENT_OS__";

    public static long getUserId() {
        try {
            Long userId = (Long) currentRequestAttributes().getAttribute(USER_ID,
                    RequestAttributes.SCOPE_REQUEST);
            if (userId == null) {
                return 0L;
            } else {
                return userId;
            }
        } catch (IllegalStateException e) {
            // 在非www环境使用
            return 0;
        }
    }

    public static void setUserId(long userId) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
    }

    public static UserAccessInfo getUserAccessInfo() {
        UserAccessInfo accessInfo = (UserAccessInfo) currentRequestAttributes().getAttribute(
                USER_ACCESS_INFO, RequestAttributes.SCOPE_REQUEST);
        if (accessInfo == null) {
            accessInfo = new UserAccessInfo();
        }
        return accessInfo;
    }

    public static void setUserAccessInfo(UserAccessInfo accessInfo) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(USER_ACCESS_INFO, accessInfo, RequestAttributes.SCOPE_REQUEST);
    }

    public static String getOs() {
        try {
            String channel = (String) currentRequestAttributes().getAttribute(OS,
                    RequestAttributes.SCOPE_REQUEST);
            return channel;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setOs(String os) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(OS, os, RequestAttributes.SCOPE_REQUEST);
    }

    public static String getDeviceMode() {
        try {
            String deviceMode = (String) currentRequestAttributes().getAttribute(DEVICE_MODE,
                    RequestAttributes.SCOPE_REQUEST);
            return deviceMode;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setDeviceMode(String deviceMode) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(DEVICE_MODE, deviceMode, RequestAttributes.SCOPE_REQUEST);
    }

    public static AppVer getAppVer() {
        try {
            AppVer appVer = (AppVer) currentRequestAttributes().getAttribute(APP_VER,
                    RequestAttributes.SCOPE_REQUEST);
            if (appVer == null) {
                return null;
            } else {
                return appVer;
            }
        } catch (IllegalStateException e) {
            // 在非www环境使用
            return null;
        }
    }

    public static String getAppChannel() {
        try {
            String channel = (String) currentRequestAttributes().getAttribute(APP_CHANNEL,
                    RequestAttributes.SCOPE_REQUEST);
            return channel;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setAppChannel(String channel) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(APP_CHANNEL, channel, RequestAttributes.SCOPE_REQUEST);
    }

    public static void setAppVer(AppVer appVer) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(APP_VER, appVer, RequestAttributes.SCOPE_REQUEST);
    }

    public static Platform getAppPlatform() {
        try {
            Platform appPlatform = (Platform) currentRequestAttributes().getAttribute(APP_PLATFORM,
                    RequestAttributes.SCOPE_REQUEST);
            if (appPlatform == null) {
                return null;
            } else {
                return appPlatform;
            }
        } catch (IllegalStateException e) {
            // 在非www环境使用
            return null;
        }
    }

    public static void setPlatform(Platform platform) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(APP_PLATFORM, platform, RequestAttributes.SCOPE_REQUEST);
    }

    @SuppressWarnings("unchecked")
    public static Entry<Double, Double> getAccessLocation() {
        try {
            Entry<Double, Double> location = (Entry<Double, Double>) currentRequestAttributes()
                    .getAttribute(ACCESS_LOCATION, RequestAttributes.SCOPE_REQUEST);
            return location;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setAccessLocation(Entry<Double, Double> location) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(ACCESS_LOCATION, location, RequestAttributes.SCOPE_REQUEST);
    }

    public static long getCurrentIpInLong() {
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) currentRequestAttributes();
            long ip = 0;
            if (servletRequestAttributes != null) {
                HttpServletRequest request = servletRequestAttributes.getRequest();
                if (request != null) {
                    ip = ipv4ToLong(getIpFromXForwardedFor(request));
                }
            }
            return ip;
        } catch (Throwable e) {
            return 0L;
        }
    }

    public static String longToIpv4(long ip) {
        return (ip >> 24) + "." + //
                ((ip & 0x00FFFFFF) >> 16) + "." + //
                ((ip & 0x0000FFFF) >> 8) + "." + //
                (ip & 0x000000FF);
    }

    public static String getIpFromXForwardedFor(HttpServletRequest request) {
        String ip = null;

        Enumeration<String> xffe = request.getHeaders("X-Forwarded-For");
        if (xffe == null) {
            return request.getRemoteAddr();
        }
        while (xffe.hasMoreElements()) {
            String xffIp = xffe.nextElement();
            if (isValidIP(xffIp) && !xffIp.startsWith("10.")) { // 过滤掉10.开头的IP
                ip = xffIp;
            }
            //对opera mini 4.2.1 的支持
            if (xffIp.indexOf(',') != -1) {
                String[] ips = StringUtils.split(xffIp, ",\n\r ");
                if (ips.length > 1) {
                    ip = ips[0];
                    if (isValidIP(ip) && !ip.startsWith("10.")) {
                        return ip;
                    }
                }
            }
        }

        return ip;
    }

    private static final Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");

    /**
     * 判断是否合法ip格式
     * 
     * @param ip
     * @return
     */
    public static boolean isValidIP(String ip) {
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }
        return IP_PATTERN.matcher(ip).matches();
    }

    public static long ipv4ToLong(String ipStr) {
        return IPUtil.ip2long(ipStr);
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) currentRequestAttributes();
        if (servletRequestAttributes != null) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    public static final String getUserAgent() {
        try {
            String r = (String) currentRequestAttributes().getAttribute(USER_AGENT,
                    RequestAttributes.SCOPE_REQUEST);
            return r;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setUserAgent(String param) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(USER_AGENT, param, RequestAttributes.SCOPE_REQUEST);
    }

    public static final String getDeviceId() {
        try {
            String r = (String) currentRequestAttributes().getAttribute(DEVICE_ID,
                    RequestAttributes.SCOPE_REQUEST);
            return r;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setDeviceId(String deviceId) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(DEVICE_ID, deviceId, RequestAttributes.SCOPE_REQUEST);
    }

    public static final String getClientId() {
        try {
            String r = (String) currentRequestAttributes().getAttribute(CLIENT_ID,
                    RequestAttributes.SCOPE_REQUEST);
            return r;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setClientId(String clientId) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(CLIENT_ID, clientId, RequestAttributes.SCOPE_REQUEST);
    }

    public static final String getTraceId() {
        try {
            String r = (String) currentRequestAttributes().getAttribute(TRACE_ID,
                    RequestAttributes.SCOPE_REQUEST);
            return r;
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setTraceId(String traceId) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(TRACE_ID, traceId, RequestAttributes.SCOPE_REQUEST);
    }

    public static final String getCurrentIpInString() {
        return longToIpv4(getCurrentIpInLong());
    }
}
