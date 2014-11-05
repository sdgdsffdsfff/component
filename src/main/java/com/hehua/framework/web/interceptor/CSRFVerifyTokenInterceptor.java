package com.hehua.framework.web.interceptor;

import com.hehua.commons.model.CommonMetaCode;
import com.hehua.commons.model.ResultView;
import com.hehua.framework.web.annotation.CSRFVerifyToken;
import com.hehua.framework.web.antispam.csrf.CSRFTokenManager;
import com.hehua.framework.web.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hesheng on 14-10-6.
 */
public class CSRFVerifyTokenInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CSRFVerifyTokenInterceptor.class);

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        CSRFVerifyToken csrfVerifyToken = MethodInterceptorUtils.getAnnotaion(handler,
                CSRFVerifyToken.class);
        boolean isVerify = csrfVerifyToken != null;
        if (isVerify) {
            CSRFTokenManager csrfVerifyTokenManager = new CSRFTokenManager<ModelAndView>() {
                @Override
                public boolean appendCSRFToken(String csrfToken, ModelAndView modelAndView) {
                    return false;
                }

                @Override
                public int getLoginUserId(HttpServletRequest request) {
                    return 1111;
                }
            };

            if (csrfVerifyTokenManager.verifyCSRFToken(request)) {
                return true;
            } else {
                ResponseUtils.output(response, new ResultView<Object>(CommonMetaCode.Unauthorized));
                return false;
            }
        }
        return true;

    }
}
