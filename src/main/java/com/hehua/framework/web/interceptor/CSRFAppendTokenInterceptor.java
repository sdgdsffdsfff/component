package com.hehua.framework.web.interceptor;

import com.hehua.framework.web.annotation.CSRFToken;
import com.hehua.framework.web.annotation.LoginRequired;
import com.hehua.framework.web.antispam.csrf.CSRFTokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * Created by hesheng on 14-10-6.
 */
public class CSRFAppendTokenInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CSRFAppendTokenInterceptor.class);

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        CSRFToken csrfToken = MethodInterceptorUtils.getAnnotaion(handler,
                CSRFToken.class);
        boolean isCrsf = csrfToken != null;
        if (isCrsf) {
            CSRFTokenManager csrfTokenManager = new CSRFTokenManager<ModelAndView>() {
                @Override
                public boolean appendCSRFToken(String csrfToken, ModelAndView modelAndView) {
                    return false;
                }

                @Override
                public int getLoginUserId(HttpServletRequest request) {
                    return 1111;
                }
            };
            String tokenStr = csrfTokenManager.generateCSRFToken(request);
            modelAndView.addObject(CSRFTokenManager.CSRF_TOKEN_NAME, tokenStr);
        }
    }
}
