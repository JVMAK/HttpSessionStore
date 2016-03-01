package com.sessionstore;

public class SessionTimeout extends RuntimeException{ 
 
	private static final long serialVersionUID = 7676612507796162807L;

	public SessionTimeout() {
		super();
	}

	public SessionTimeout(String message, Throwable cause) {
		super(message, cause);
	}

	public SessionTimeout(String message) {
		super(message);
	}

	public SessionTimeout(Throwable cause) {
		super(cause);
	}

}
