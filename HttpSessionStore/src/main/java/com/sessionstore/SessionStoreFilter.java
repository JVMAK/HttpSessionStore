package com.sessionstore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.sessionstore.redis.RedisSessionStoreManager;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 利用装饰模式，实现对应用系统的透明化处理。
 *
 * @author sunyujia@aliyun.com
 */
public class SessionStoreFilter implements Filter {
    private static final HashSet<String> IGNORE_SUFFIX = new HashSet<String>();
    static {
        IGNORE_SUFFIX.addAll(Arrays.asList("gif,jpg,jpeg,png,bmp,swf,js,css,html,htm".split(",")));
    }

    protected SessionStoreManager sessionStoreManager;

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain fc)
            throws IOException, ServletException {
        boolean enable = sessionStoreManager.isEnableRedisStore();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if ((!enable) || (!shouldFilter(request) || (request instanceof HttpRequestWrapper))) {
            fc.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        RequestEvent requestEvent = new RequestEvent(request, response, sessionStoreManager);
        HttpServletRequestWrapper requestWrapper = sessionStoreManager.getRequestWrapper(requestEvent);
        try {
            fc.doFilter(requestWrapper, servletResponse);
        } finally {
            requestEvent.completed(request, response);
        }
    }

    public SessionStoreManager getSessionStoreManager() {
        return sessionStoreManager;
    }

    public void setSessionStoreManager(SessionStoreManager sessionStoreManager) {
        this.sessionStoreManager = sessionStoreManager;
    }

    /**
     * 该方法仅在没有使用spring的org.springframework.web.filter.DelegatingFilterProxy时使用，即直接在web.xml中配置SessionStoreFilter时使用。
     */
    public void init(FilterConfig fc) throws ServletException {
        if(this.sessionStoreManager!=null){
            return;
        }
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        String redisIp=fc.getInitParameter("redisIp");
        Integer redisPort=Integer.parseInt(fc.getInitParameter("redisPort"));
        Integer redisTimeout=Integer.parseInt(fc.getInitParameter("redisTimeout"));
        Integer sessionTimeout=Integer.parseInt(fc.getInitParameter("sessionTimeout"));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisIp, redisPort, redisTimeout);
        RedisSessionStoreManager redisSessionStoreManager = new RedisSessionStoreManager();
        redisSessionStoreManager.setJedisPool(jedisPool);
        redisSessionStoreManager.setSessionTimeout(sessionTimeout);
        String cookieDomain = fc.getInitParameter("cookieDomain");
        if (cookieDomain != null&&cookieDomain.length()>0) {
            redisSessionStoreManager.setCookieDomain(cookieDomain.trim());
        }
        String cookiePath = fc.getInitParameter("cookiePath");
        if (cookiePath != null) {
            redisSessionStoreManager.setCookiePath(cookiePath);
        }
        redisSessionStoreManager.init();
        this.sessionStoreManager = redisSessionStoreManager;
    }

    private boolean shouldFilter(HttpServletRequest request) {
        String uri = request.getRequestURI().toLowerCase();
        int idx = uri.lastIndexOf(".");
        if (idx > 0) {
            String suffix = uri.substring(idx);
            if (suffix.length() < 8 && IGNORE_SUFFIX.contains(suffix)) {
                return false;
            }
        }
        return true;
    }
}