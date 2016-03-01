package com.sessionstore.serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 基于jdk的序列化工具类
 * @author sunyujia@aliyun.com
 *
 */
public class JavaSerializer implements Serializer {

	public byte[] serialize(Object obj){
		if(obj==null)return null;
		byte[] serialized = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(new BufferedOutputStream(bos));
			oos.writeObject(obj);
			oos.flush();
			serialized = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerializerException(e.getMessage(),e);			
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return serialized;
	}

	public Object deserialize(byte[] data){
		if(data==null)return null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new ByteArrayInputStream(data));
			ois = new ObjectInputStream(bis);
			return ois.readObject();
		} catch (IOException|ClassNotFoundException e) {
			e.printStackTrace();
			throw new SerializerException(e.getMessage(),e);
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (oos != null)
					oos.close();
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
