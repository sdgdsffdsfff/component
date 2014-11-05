/**
 * 
 */
package com.hehua.framework.web.exception;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hehua.commons.exception.BusinessException;
import com.hehua.commons.time.DateUtils;
import com.hehua.framework.log.LogCategory;
import com.hehua.framework.log.LogService;
import com.hehua.framework.web.render.ResponseRender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;

public class ExceptionResolver implements HandlerExceptionResolver {

    private static final Log logger = LogFactory.getLog(ExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception exception) {

        logException(request, response, handler, exception);

        response.setContentType("application/json;charset=utf-8");
        response.setStatus(200);
        try {

            JSONObject result = ResponseRender.renderError(exception);
            PrintWriter pw = response.getWriter();
            pw.print(JSON.toJSON(result));

            pw.flush();
            pw.close();
            return new ModelAndView();
        } catch (Exception e) {
            logger.warn("Ops.", e);
        }
        return null;
    }

    private void logException(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception exception) {

        if (exception instanceof BusinessException) {
            if (logger.isTraceEnabled()) {
                logger.trace("ops", exception);
            }
        } else {
            logExceptionToFlume(request, exception);
            logger.error("ops", exception);
        }
    }

    private void logExceptionToFlume(HttpServletRequest request, Exception exception) {
        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return;
        }
        if (exception instanceof HttpMediaTypeNotAcceptableException) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(DateUtils.formatDateTime(new Date()));
        sb.append("|");
        sb.append(exception.toString());
        sb.append("|");
        sb.append(request.getMethod() + ":");
        sb.append(request.getRequestURL());
        if (request.getQueryString() != null) {
            sb.append("?" + request.getQueryString());
        }
        sb.append("|");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            sb.append("cookie:");
            for (Cookie cookie : cookies) {
                sb.append(cookie.getName());
                sb.append("=");
                sb.append(cookie.getValue());
            }
            sb.append("|");
        }
        int count = 0;
        sb.append("Trace:");
        for (StackTraceElement traceElement : exception.getStackTrace()) {
            sb.append("at ");
            sb.append(traceElement);
            sb.append("#");
            if (++count == 3) {
                sb.append("...");
                break;
            }
        }
        LogService.getInstance().log(LogCategory.ERRORLOG, sb.toString());
    }

}
