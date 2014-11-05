/**
 * 
 */
package com.hehua.framework.web.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author zhihua
 *
 */
public class RequestLoggingFilter extends GenericFilterBean {

    private static final Log logger = LogFactory.getLog(RequestLoggingFilter.class);

    private final AtomicLong requestIdGenerator = new AtomicLong();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (logger.isTraceEnabled()) {

            long id = requestIdGenerator.incrementAndGet();
            LogableRequest logableRequest = new LogableRequest(id, (HttpServletRequest) request);
            LogableResponse logableResponse = new LogableResponse(id,
                    (HttpServletResponse) response);
            try {
                chain.doFilter(logableRequest, logableResponse);
            } finally {
                logger.trace("===============================" + id + "\r\n" + logableRequest
                        + "\r\n" + logableResponse);
            }
        } else {
            chain.doFilter(request, response);
        }

    }

}
