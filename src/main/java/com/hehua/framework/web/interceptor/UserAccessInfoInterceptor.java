package com.hehua.framework.web.interceptor;

import com.hehua.commons.model.UserAccessInfo;
import com.hehua.commons.time.DateUtils;
import com.hehua.framework.web.HehuaRequestContext;
import com.hehua.user.domain.Baby;
import com.hehua.user.service.BabyService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by liuweiwei on 14-9-4.
 */
public class UserAccessInfoInterceptor extends HandlerInterceptorAdapter {

    @Inject
    private BabyService babyService;
    /**
     * This implementation always returns <code>true</code>.
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        UserAccessInfo userAccessInfo = new UserAccessInfo();
        userAccessInfo.setTm(DateUtils.formatDateTime(new Date()));
        if (HehuaRequestContext.getAppVer() != null) {
            userAccessInfo.setAppid(HehuaRequestContext.getAppVer().toString());
        }
        if (HehuaRequestContext.getAppChannel() != null) {
            userAccessInfo.setChannelid(HehuaRequestContext.getAppChannel().toString());
        }
        userAccessInfo.setTraceid(HehuaRequestContext.getTraceId());
        if (HehuaRequestContext.getUserId() != 0) {
            Baby baby = babyService.getBabyByUidxUid(HehuaRequestContext.getUserId());
            userAccessInfo.setPreganancy(baby.getStatus());
            userAccessInfo.setBabyGender(baby.getGender());
            if (baby.getBirthday() != null) {
                userAccessInfo.setBabyBirthday(DateUtils.formatDate(baby.getBirthday()));
            }
            if (baby.getEdc() != null ) {
                userAccessInfo.setEdc(DateUtils.formatDate(baby.getEdc()));
            }
        }
        userAccessInfo.setUid(HehuaRequestContext.getUserId());
        userAccessInfo.setClientid(HehuaRequestContext.getClientId());
        userAccessInfo.setOs(HehuaRequestContext.getOs());
        userAccessInfo.setDm(HehuaRequestContext.getDeviceMode());
        userAccessInfo.setIp(this.getRemoteIp(request));

        HehuaRequestContext.setUserAccessInfo(userAccessInfo);
        return true;
    }

    /**
     * This implementation is empty.
     */
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {

    }

    public String getRemoteIp(HttpServletRequest request) {
        if (request.getHeader("X-FORWARDED-FOR") != null) {
            String[] ips = request.getHeader("X-FORWARDED-FOR").split(",");
            return ips[0];
        } else if (request.getHeader("X-Real-IP") != null) {
            return request.getHeader("X-Real-IP");
        } else if (request.getHeader("Proxy-Client-IP") != null) {
            return request.getHeader("Proxy-Client-IP");
        } else if (request.getHeader("WL-Proxy-Client-IP") != null) {
            return request.getHeader("WL-Proxy-Client-IP");
        } else {
            return request.getRemoteAddr();
        }
    }
}
