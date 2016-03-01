package com.sessionstore.serializer;

/**
 * 序列化接口
 *
 * @author sunyujia@aliyun.com
 */
public interface Serializer {

    byte[] serialize(Object obj);

    Object deserialize(byte[] data);
}
