package com.sessionstore;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * request请求装饰类，核心逻辑是实现getSession方法。
 * 该装饰类没有100%遵循servlet规范
 *
 * @author sunyujia@aliyun.com
 */
public class SessionHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private HttpSession session;
    private RequestEventSubject requestEventSubject;

    public SessionHttpServletRequestWrapper(RequestEventSubject requestEventSubject) {
        super(requestEventSubject.getRequest());
        this.requestEventSubject = requestEventSubject;
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
        this.session = requestEventSubject.getSessionStoreManager().getSession(this.requestEventSubject, create);
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