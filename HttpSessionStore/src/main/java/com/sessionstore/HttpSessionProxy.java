package com.sessionstore;

import javax.servlet.http.HttpSession;

/**
 * 该类无特别意义，仅仅是为了扩展HttpSession的isValidate方法
 *
 * @author sunyujia@aliyun.com
 */
public interface HttpSessionProxy extends HttpSession {
    public boolean isValidate();
}