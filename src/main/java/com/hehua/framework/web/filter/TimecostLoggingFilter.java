/**
 * 
 */
package com.hehua.framework.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class TimecostLoggingFilter extends OncePerRequestFilter {

    private static final Log logger = LogFactory.getLog("timecost");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long s = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (logger.isDebugEnabled()) {
                String uri = request.getRequestURI();
                String method = request.getMethod();
                long cost = System.currentTimeMillis() - s;
                // GET[/flash/list] cost 50
                logger.debug(StringUtils.upperCase(method) + "[" + uri + "]" + " cost " + cost);
            }
        }
    }

}
