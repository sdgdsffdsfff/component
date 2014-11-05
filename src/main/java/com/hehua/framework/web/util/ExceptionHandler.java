package com.hehua.framework.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.hehua.framework.web.HehuaRequestContext;

public class ExceptionHandler extends SimpleMappingExceptionResolver {

    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        if (ex instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
            return;
        }
        super.logException(ex, request);
    }

    @Override
    protected String buildLogMessage(Exception ex, HttpServletRequest request) {
        String url = request.getRequestURL()
                .append((request.getQueryString() == null ? "" : ("?" + request.getQueryString())))
                .toString();
        return "500|" + request.getMethod() + "|" + url + "|" + request.getHeader("user-agent")
                + "|" + HehuaRequestContext.getCurrentIpInString() + "|"
                + HehuaRequestContext.getUserId();
    }
}
