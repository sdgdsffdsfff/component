package com.hehua.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hehua.framework.web.HehuaRequestContext;
import com.hehua.framework.web.model.AppVer;

public class GobalVariableInterceptor extends HandlerInterceptorAdapter {

    private static final Log logger = LogFactory.getLog(GobalVariableInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        try {
            // os
            // ver
            // ch
            // dm
            String os = request.getParameter("os");
            String appVersion = request.getParameter("ver");
            String channel = request.getParameter("ch");
            String dm = request.getParameter("dm");
            String deviceId = request.getParameter("deviceid");

            try {
                HehuaRequestContext.setAppVer(AppVer.of(appVersion));
            } catch (Exception e) {
                logger.warn("set ver fail, value=" + appVersion, e);
            }

            if (channel != null) {
                HehuaRequestContext.setAppChannel(channel);
            }

            if (os != null) {
                HehuaRequestContext.setOs(os);
            }

            if (dm != null) {
                HehuaRequestContext.setDeviceMode(dm);
            }

            if (deviceId != null) {
                HehuaRequestContext.setDeviceId(deviceId);
            }

        } catch (Exception e) {
            logger.warn("exception", e);
        }
        return true;
    }

}
