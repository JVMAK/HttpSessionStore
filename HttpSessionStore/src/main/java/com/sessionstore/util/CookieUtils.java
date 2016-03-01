package com.sessionstore.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 操作cookie的工具类
 *
 * @author sunyujia@aliyun.com
 */
public class CookieUtils {
    public static String getCookieValue(Cookie[] cookies, String cookieName,
                                        String defaultValue) {
        if (null == cookies)
            return "";
        for (int i = 0, size = cookies.length; i < size; i++) {
            Cookie cookie = cookies[i];
            if (cookieName.equals(cookie.getName()))
                return cookie.getValue();
        }
        return defaultValue;
    }

    public static String getCookieValue(HttpServletRequest request,
                                        String cookieName, String defaultValue) {
        return getCookieValue(request.getCookies(), cookieName, defaultValue);
    }

    public static void setCookie(HttpCookie cookie,
                                 HttpServletResponse httpResponse) {
        if (httpResponse != null)
            cookie.writeResponse(httpResponse);
    }

    public static void clearCookie(Cookie[] cookies, String cookieName,
                                   HttpServletResponse httpResponse) {
        if (null != cookies) {
            for (int i = 0, size = cookies.length; i < size; i++) {
                Cookie cookie = cookies[i];
                if (cookieName.equals(cookie.getName()))
                    cookie.setMaxAge(0);
                if (httpResponse != null) {
                    httpResponse.addCookie(cookie);
                }
            }
        }
    }

    public static void clearCookie(HttpServletRequest request,
                                   String cookieName, HttpServletResponse httpResponse) {
        clearCookie(request.getCookies(), cookieName, httpResponse);
    }
}
