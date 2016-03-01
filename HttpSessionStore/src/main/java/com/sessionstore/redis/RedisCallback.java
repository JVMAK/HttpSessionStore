package com.sessionstore.redis;

import com.sessionstore.serializer.Serializer;

import redis.clients.jedis.Jedis;
/**
 * RedisTemplate的回调方法
 * @author sunyujia@aliyun.com
 *
 */
public interface RedisCallback {
	public Object doInRedis(Jedis jedis,Serializer serializer);
}
