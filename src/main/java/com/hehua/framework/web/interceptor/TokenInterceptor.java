package com.hehua.framework.web.interceptor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hehua.commons.model.CommonMetaCode;
import com.hehua.commons.model.ResultView;
import com.hehua.framework.web.HehuaRequestContext;
import com.hehua.framework.web.annotation.LoginRequired;
import com.hehua.framework.web.util.CookieUtils;
import com.hehua.framework.web.util.ResponseUtils;
import com.hehua.user.domain.Token;
import com.hehua.user.service.TokenService;

public class TokenInterceptor extends HandlerInterceptorAdapter {

    private static final Log logger = LogFactory.getLog(TokenInterceptor.class);

    private static final String TICKET_NAME = "token";

    @Inject
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        LoginRequired loginRequired = MethodInterceptorUtils.getAnnotaion(handler,
                LoginRequired.class);
        boolean isLoginRequired = loginRequired != null;
        //        boolean isTicketAware = MethodInterceptorUtils.getAnnotaion(handler, TicketAware.class) != null;
        // 未登录
        Token token = getToken(request);
        if (isLoginRequired && token == null) {
            ResponseUtils.output(response, new ResultView<Object>(CommonMetaCode.Unauthorized));
            return false;
        }
        if (token == null) {
            return true;
        }

        // 过期
        boolean isTokenExpired = token.isExpired();
        Long userId = isTokenExpired ? null : token.getUid();

        if (isLoginRequired && isTokenExpired) {
            tokenService.deleteTokenByToken(token.getToken());
            ResponseUtils.output(response, new ResultView<Object>(
                    CommonMetaCode.AuthenticationExpired));
            return false;
        }
        if (userId != null) {
            HehuaRequestContext.setUserId(userId);
        }
        return true;
    }

    public static String getCurrentToken(HttpServletRequest request) {
        String cookieTicket = request.getParameter(TICKET_NAME);

        if (StringUtils.isBlank(cookieTicket)) {
            cookieTicket = request.getHeader("Authorization");
        }

        if (StringUtils.isBlank(cookieTicket)) {
            cookieTicket = CookieUtils.getCookie(request, TICKET_NAME);
            if (StringUtils.isBlank(cookieTicket)) {
                return null;
            }
        }
        return StringUtils.trimToNull(cookieTicket);
    }

    public Long getLoginUserId(HttpServletRequest request) {
        String ticket = getCurrentToken(request);
        if (ticket == null) {
            return null;
        }
        Long resultUserId = tokenService.verfiyToken(ticket);
        return resultUserId;
    }

    public Token getToken(HttpServletRequest request) {
        String ticket = getCurrentToken(request);
        if (ticket == null) {
            return null;
        }
        return tokenService.getTokenByToken(ticket);
    }

}
