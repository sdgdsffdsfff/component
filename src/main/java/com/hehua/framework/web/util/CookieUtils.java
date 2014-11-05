/**
 * 
 */
package com.hehua.framework.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        return getCookie(cookies, key);
    }

    public static String getCookie(Cookie[] cookies, String key) {
        if ((cookies == null) || (cookies.length == 0)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public static void saveCookie(HttpServletResponse response, String key, String value,
            int second, String domain, String path, boolean httpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setMaxAge(second);
        cookie.setDomain(domain);
        //        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }

    public static void clearCookie(HttpServletResponse response, String key, String domain,
            String path) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }
}
