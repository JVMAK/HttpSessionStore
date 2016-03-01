package com.sessionstore.serializer;

/**
 * 序列化异常
 * @author sunyujia@aliyun.com
 */
public class SerializerException extends RuntimeException {
    private static final long serialVersionUID = 5109639464780576657L;

    public SerializerException() {
        super();
    }

    public SerializerException(String message, Throwable cause,
                               boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializerException(String message) {
        super(message);
    }

    public SerializerException(Throwable cause) {
        super(cause);
    }

}
