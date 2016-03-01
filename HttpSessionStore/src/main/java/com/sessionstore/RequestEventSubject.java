package com.sessionstore;

import java.util.Observable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 观察者模式:被观察目标
 * 请求事件，该类有两个功能:1是作为请求过程观察者模式中的被观察者，2是作为参数容器将请求上下文参数存入其中，例如request，response等，缩减核心方法的入参个数。
 *
 * @author sunyujia@aliyun.com
 */
public class RequestEventSubject extends Observable {
    protected SessionStoreManager sessionStoreManager;
    private HttpServletResponse response;
    private HttpServletRequest request;

    public RequestEventSubject(HttpServletRequest request, HttpServletResponse response,
                               SessionStoreManager sessionStoreManager) {
        super();
        this.sessionStoreManager = sessionStoreManager;
        this.response = response;
        this.request = request;
    }

    public SessionStoreManager getSessionStoreManager() {
        return sessionStoreManager;
    }

    public void setSessionStoreManager(SessionStoreManager sessionStoreManager) {
        this.sessionStoreManager = sessionStoreManager;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 请求结束后通知所有的观察者
     *
     * @param servletRequest
     * @param response
     */
    public void completed(HttpServletRequest servletRequest, HttpServletResponse response) {
        this.setChanged();
        this.notifyObservers();
    }

}