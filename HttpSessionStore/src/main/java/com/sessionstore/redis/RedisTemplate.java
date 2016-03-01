package com.sessionstore.redis;

import com.sessionstore.serializer.JavaSerializer;
import com.sessionstore.serializer.Serializer;

import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis 模板，操作redis数据库使用
 * 
 * @author sunyujia@aliyun.com
 * 
 */
public class RedisTemplate {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RedisTemplate.class);
	/**
	 * java对象序列化反序列化实现类
	 */
	private Serializer serializer=new JavaSerializer();
	
	private JedisPool jedisPool;

    public RedisTemplate() {
    }

    public RedisTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Serializer getSerializer() {
		return serializer;
	}
	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
	public JedisPool getJedisPool() {
		return jedisPool;
	}
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	
	@SuppressWarnings("deprecation")
	public Object execute(RedisCallback redisCallback) {
		JedisPool jedisPool = getJedisPool();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return redisCallback.doInRedis(jedis,serializer);
		} catch (JedisConnectionException e) {
			if(jedis!=null){
				jedisPool.returnBrokenResource(jedis);
			}
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if(jedis!=null){
				jedisPool.returnResource(jedis);
			}
		}
	}

    private void closeResource(Jedis jedis, boolean conectionBroken) {
        try {
            if (conectionBroken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
        } catch (Exception e) {
            log.error("return back jedis failed, will fore close the jedis.", e);
            destroyJedis(jedis);
        }
    }

    private void destroyJedis(Jedis jedis) {
        if ((jedis != null) && jedis.isConnected()) {
            try {
                try {
                    jedis.quit();
                } catch (Exception e) {
                }
                jedis.disconnect();
            } catch (Exception e) {
            }
        }
    }

    private boolean handleJedisException(JedisException jedisException) {
        if (jedisException instanceof JedisConnectionException) {
            log.error("Redis connection  lost.", jedisException);
        } else if (jedisException instanceof JedisDataException) {
            if ((jedisException.getMessage() != null) && (jedisException.getMessage().indexOf("READONLY") != -1)) {
                log.error("Redis connection  are read-only slave.", jedisException);
            } else {
                // dataException, isBroken=false
                return false;
            }
        } else {
            log.error("Jedis exception happen.", jedisException);
        }
        return true;
    }

}
