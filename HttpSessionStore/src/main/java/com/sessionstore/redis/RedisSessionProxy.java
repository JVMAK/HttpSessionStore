package com.sessionstore.redis;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionContext;

import com.sessionstore.HttpSessionProxy;
import com.sessionstore.util.CookieUtils;
import com.sessionstore.util.HttpCookie;

/**
 * 
 * HttpSession代理类,该类实现Observer，使用观察者模式，角色为观察者，观察对象为RequestEventSubject，请求结束后保存session对象
 * 观察者模式核心方法public void update(Observable o, Object arg)，被观察对象通知本类的实例执行该方法
 * @author sunyujia@aliyun.com
 *
 */
@SuppressWarnings("deprecation")
public class RedisSessionProxy implements HttpSessionProxy , Observer{
	private SessionMap sessionMap= null;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private RedisSessionStoreManager sessionManager;
	
	public RedisSessionProxy(HttpServletRequest request, 
			HttpServletResponse response,
			SessionMap session, 
			RedisSessionStoreManager sessionManager) {
		super();
		this.sessionMap = session;
		this.request = request;
		this.response = response;
		this.sessionManager = sessionManager;
	}

	public Object getAttribute(String key) {
		if (this.sessionMap == null)
			reBuildSession();
		return sessionMap.get(key);
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		if (this.sessionMap == null)
			reBuildSession();
		return Collections.enumeration(this.sessionMap.keySet());
	}

	public long getCreationTime() {
		if (this.sessionMap == null)
			reBuildSession();
		return this.sessionMap.getCreationTime();
	}

	public String getId() {
		if (this.sessionMap == null)
			reBuildSession();
		return this.sessionMap.getId();
	}

	public long getLastAccessedTime() {
		if (this.sessionMap == null)
			reBuildSession();
		return this.sessionMap.getLastAccessedTime();
	}

	public int getMaxInactiveInterval() {
		if (this.sessionMap == null)
			reBuildSession();
		return sessionManager.getSessionTimeout();
	}

	public Object getValue(String key) {
		if (this.sessionMap == null)
			reBuildSession();
		return this.sessionMap.get(key);
	}

	public String[] getValueNames() {
		if (this.sessionMap == null)
			reBuildSession();
		return (String[]) this.sessionMap.keySet().toArray(new String[0]);
	}

	public void invalidate() {
		if(sessionMap!=null){
			sessionManager.invalidate(sessionMap.getId());
		}
		this.sessionMap = null;
	}

	@Override
	public boolean isValidate() {
		return this.sessionMap != null && sessionManager.isValidate(sessionMap.getId());
	}

	public boolean isNew() {
		return sessionMap.isNew();
	}

	public void putValue(String key, Object val) {
		if (this.sessionMap == null)
			reBuildSession();
		sessionMap.put(key, val);
	}

	public void removeAttribute(String key) {
		if (this.sessionMap == null)
			reBuildSession();
		sessionMap.remove(key);
	}

	public void removeValue(String key) {
		if (this.sessionMap == null)
			reBuildSession();
		sessionMap.remove(key);
	}

	public void setAttribute(String key, Object val) {
		if (this.sessionMap == null)
			reBuildSession();
		sessionMap.put(key, val);
	}

	public ServletContext getServletContext() {
		if (this.sessionMap == null)
			reBuildSession();
		return this.request.getSession().getServletContext();
	}

	public HttpSessionContext getSessionContext() {
		return null;
	}

	public void setMaxInactiveInterval(int maxAge) {
	}

	/**
	 *  重建session
	 */
	private void reBuildSession() {
		this.sessionMap = sessionManager.getSession(request, true);
		this.writeCookie();
	}
	/**
	 * 将session数据保存到redis中。
	 * @param smap
	 */
	public void saveSessionMap(){
		sessionManager.saveSessionMap(this.sessionMap);
	}

	/**
	 * 向浏览器写入cookie
	 * 
	 * @param sid
	 */
	void writeCookie() {
		String sid = getId();
		request.setAttribute(sessionManager.getSidKey(), sid);
		HttpCookie cookie = new HttpCookie(sessionManager.getSidKey(), sid);
		String path = sessionManager.getCookiePath();
		if (path != null && path.trim().length() > 0) {
			cookie.setPath(path);
		}
		String domain = sessionManager.getCookieDomain();
		if (domain != null && domain.trim().length() > 0) {
			cookie.setDomain(domain);
		}
		CookieUtils.setCookie(cookie, response);
	}
	/**
	 * 观察者模式：观察者
	 * 利用观察者模式实现松耦合，redis包下面的类和外面的类隔离。
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.saveSessionMap();
	}
 
}