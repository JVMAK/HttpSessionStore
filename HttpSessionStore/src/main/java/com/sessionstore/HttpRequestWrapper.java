package com.sessionstore;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * request请求装饰类，核心逻辑是实现getSession方法。
 * 该装饰类没有100%遵循servlet规范
 *
 * @author sunyujia@aliyun.com
 */
public class HttpRequestWrapper extends HttpServletRequestWrapper {

    private HttpSession session;
    private RequestEvent requestEvent;

    public HttpRequestWrapper(RequestEvent requestEvent) {
        super(requestEvent.getRequest());
        this.requestEvent = requestEvent;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (session != null) {
            return session;
        }
        this.session = requestEvent.getSessionStoreManager().getSession(this.requestEvent, create);
        return session;
    }

    @Override
    public String getRequestedSessionId() {
        if (session == null) {
            return null;
        }
        return session.getId();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        HttpSessionProxy sessionProxy = (HttpSessionProxy) this.getSession(false);
        if (sessionProxy == null) {
            return false;
        } else {
            return sessionProxy.isValidate();
        }
    }
}