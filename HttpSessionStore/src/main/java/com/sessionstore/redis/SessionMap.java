package com.sessionstore.redis;

import java.util.HashMap;

/**
 * 存储session属性的值对象, 该类被设计为值对象，没有逻辑，逻辑在RedisSessionManager中。
 * @author sunyujia@aliyun.com
 *
 */
public class SessionMap extends HashMap<String, Object>{
	private static final long serialVersionUID = 2875861337661254462L;

	private String id;
	
	private long creationTime;

	private long lastAccessedTime;

	private String lastAccessedUrl;

	private String contextPath;
	
	private boolean isNew;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	public String getLastAccessedUrl() {
		return lastAccessedUrl;
	}

	public void setLastAccessedUrl(String lastAccessedUrl) {
		this.lastAccessedUrl = lastAccessedUrl;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
}
