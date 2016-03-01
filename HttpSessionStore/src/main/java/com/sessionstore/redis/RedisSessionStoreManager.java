package com.sessionstore.redis;

import java.nio.charset.Charset;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import com.sessionstore.RequestEventSubject;
import com.sessionstore.SessionHttpServletRequestWrapper;
import com.sessionstore.SessionStoreManager;
import com.sessionstore.serializer.Serializer;
import com.sessionstore.util.CookieUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于Redis的会话管理器,方法的注释在接口上。
 *
 * @author sunyujia@aliyun.com
 */
public class RedisSessionStoreManager implements SessionStoreManager {
    private Charset UTF8 = Charset.forName("UTF-8");

    private String sidKey = "_SID_";

    private String redisIp;

    private int redisPort = 6379;

    private int redisTimeout = 5000;

    private String cookiePath = "/";

    private String cookieDomain;

    private boolean enableRedisStore = true;

    /**
     * session会话有效期：单位（秒）
     */
    private int sessionTimeout = 60 * 30;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
        this.jedisPoolConfig = jedisPoolConfig;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private JedisPool jedisPool;

    private JedisPoolConfig jedisPoolConfig;

    private RedisTemplate redisTemplate;

    public String getRedisIp() {
        return redisIp;
    }

    public void setRedisIp(String redisIp) {
        this.redisIp = redisIp;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public int getRedisTimeout() {
        return redisTimeout;
    }

    public void setRedisTimeout(int redisTimeout) {
        this.redisTimeout = redisTimeout;
    }

    public String getCookiePath() {
        return cookiePath;
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public String getCookieDomain() {
        return cookieDomain;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public boolean isEnableRedisStore() {
        return enableRedisStore;
    }

    public void setEnableRedisStore(boolean enableRedisStore) {
        this.enableRedisStore = enableRedisStore;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getSidKey() {
        return sidKey;
    }

    public void setSidKey(String sidKey) {
        this.sidKey = sidKey;
    }

    @Override
    public HttpSession getSession(RequestEventSubject requestEventSubject, boolean create) {
        SessionMap sessionMap = getSession(requestEventSubject.getRequest(), create);
        if (sessionMap != null) {
            RedisSessionProxy sessionProxy = new RedisSessionProxy(requestEventSubject.getRequest(), requestEventSubject.getResponse(), sessionMap, this);
            requestEventSubject.addObserver(sessionProxy);
            sessionProxy.writeCookie();
            return sessionProxy;
        }
        return null;
    }

    @Override
    public HttpServletRequestWrapper getRequestWrapper(RequestEventSubject requestEventSubject) {
        SessionHttpServletRequestWrapper requestWrapper = new SessionHttpServletRequestWrapper(requestEventSubject);
        return requestWrapper;
    }

    private RedisTemplate createRedisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setJedisPool(jedisPool);
        return redisTemplate;
    }

    /**
     * 初始化该管理器，主要是完成jedis连接池的创建
     */
    public void init() {
        if (this.jedisPool == null) {
            this.jedisPoolConfig = new JedisPoolConfig();
            this.jedisPool = new JedisPool(jedisPoolConfig, getRedisIp(), getRedisPort(), getRedisTimeout());
        }
        if (this.redisTemplate == null) {
            this.redisTemplate = createRedisTemplate();
        }
    }

    public SessionMap getSession(final HttpServletRequest request, final boolean create) {
        final String sid = getSessionId(request);
        SessionMap sessionMap = (SessionMap) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(Jedis jedis, Serializer serializer) {
                SessionMap sessionMap = null;
                byte[] binaryId = sid != null ? sid.getBytes() : null;
                boolean exists = binaryId == null ? false : jedis.exists(binaryId);// 会话id存在
                if (exists) {// 正常续期的场景
                    long expireRs = jedis.expire(binaryId, getSessionTimeout());
                    if (expireRs != 1) {// 在准备续期的瞬间会话超时了。
                        exists = false;
                    }
                }
                if (exists) {
                    sessionMap = (SessionMap) serializer.deserialize(jedis.get(binaryId));
                    sessionMap.setNew(false);
                } else if (create) {
                    sessionMap = createSessionMap();
                } else {
                    return null;
                }
                updateSessionMap(request, sessionMap);
                return sessionMap;
            }
        });
        return sessionMap;
    }

    /**
     * 创建一个新的session数据值对象
     *
     * @return
     */
    private SessionMap createSessionMap() {
        SessionMap sessionMap = new SessionMap();
        sessionMap.setId("SID_" + UUID.randomUUID().toString());
        sessionMap.setCreationTime(System.currentTimeMillis());
        sessionMap.setNew(true);
        return sessionMap;
    }

    /**
     * 更新session的部分属性，例如末次访问时间
     *
     * @param request
     * @param sessionMap
     * @return
     */
    private void updateSessionMap(HttpServletRequest request, SessionMap sessionMap) {
        sessionMap.setLastAccessedTime(System.currentTimeMillis());
        sessionMap.setLastAccessedUrl(request.getRequestURI());
        sessionMap.setContextPath(request.getContextPath());
    }

    public void invalidate(final String sid) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(Jedis jedis, Serializer serializer) {
                return jedis.del(sid.getBytes(UTF8));
            }
        });
    }

    public boolean isValidate(final String sid) {
        return (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(Jedis jedis, Serializer serializer) {
                return jedis.exists(sid.getBytes(UTF8));
            }
        });
    }

    /**
     * 将session数据保存到redis中。
     *
     * @param smap
     */
    public void saveSessionMap(final SessionMap smap) {
        if (smap == null) return;
        final byte[] idByte = smap.getId().getBytes(UTF8);
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(Jedis jedis, Serializer serializer) {
                jedis.setex(idByte, getSessionTimeout(), serializer.serialize(smap));
                return null;
            }
        });
    }

    /**
     * 获取会话id
     *
     * @param request
     * @return
     */
    public String getSessionId(HttpServletRequest request) {
        String sid = (String) request.getAttribute(getSidKey());
        if (sid == null || "".equals(sid)) {
            sid = CookieUtils.getCookieValue(request.getCookies(), getSidKey(), null);
        }
        if ("".equals(sid)) {
            sid = null;
        }
        return sid;
    }
}
