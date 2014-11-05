/**
 * 
 */
package com.hehua.framework.web.filter;

import com.hehua.framework.web.antispam.StripXssRequestWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StripXssFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (request.getCharacterEncoding() == null)
            request.setCharacterEncoding("UTF-8");
        filterChain.doFilter(new StripXssRequestWrapper((HttpServletRequest) request), response);
    }

}
