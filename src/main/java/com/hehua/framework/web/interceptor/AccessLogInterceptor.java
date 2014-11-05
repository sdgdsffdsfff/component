package com.hehua.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hehua.framework.web.HehuaRequestContext;

public class AccessLogInterceptor extends HandlerInterceptorAdapter {

    private final Log accessLog = LogFactory.getLog("accessLog");

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {
        long visiterId = HehuaRequestContext.getUserId();
        String url = request.getRequestURL()
                .append((request.getQueryString() == null ? "" : ("?" + request.getQueryString())))
                .toString();
        String log = visiterId + "|" + request.getMethod() + "|" + url + "|"
                + request.getHeader("user-agent") + "|"
                + HehuaRequestContext.getCurrentIpInString() + "|"
                + HehuaRequestContext.getDeviceMode() + "|" + HehuaRequestContext.getOs() + "|"
                + HehuaRequestContext.getAppChannel() + "|" + HehuaRequestContext.getAppVer();
        accessLog.info(log);
    }

}
