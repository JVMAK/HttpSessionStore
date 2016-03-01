package com.sessionstore;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * 会话管理器接口，实现该接口即可切换会话管理的底层实现
 * @author sunyujia@aliyun.com
 *
 */
public interface SessionStoreManager {
	
	
	/**
	 * 核心方法获取一个session对象
	 * @param requestEvent
	 * @param create
	 * @return
	 */
	public HttpSession getSession(RequestEvent requestEvent,boolean create);
	
	/**
	 * 返回一个request装饰类
	 * @param requestEvent
	 * @return
	 */
	public HttpServletRequestWrapper getRequestWrapper(RequestEvent requestEvent);
	
	
	/**
	 * 是否启用session管理器
	 * @return
	 */
	public boolean isEnableRedisStore();
	
	/**
	 * 销毁session
	 * @param sid
	 */
	public void invalidate(String sid);
	
	/**
	 * 判断session是否失效
	 * @param sid
	 * @return
	 */
	public boolean isValidate(String sid);
	
	/**
	 * 获取session 超时时间
	 * @return
	 */
	public int getSessionTimeout();
	
	/**
	 * 获取存储sessionid的cookie的域名
	 * @return
	 */
	public String getCookieDomain();
	
	/**
	 * 获取存储sessionid的路径
	 * @return
	 */
	public String getCookiePath();
	
	/**
	 * 获取在cookie中存储sid的key的名字
	 * @return
	 */
	public String getSidKey();
	
	/**
	 * 初始化逻辑
	 */
	public void init();
}