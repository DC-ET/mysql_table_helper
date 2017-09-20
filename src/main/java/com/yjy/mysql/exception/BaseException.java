package com.yjy.mysql.exception;

public class BaseException extends RuntimeException {

	private static final long serialVersionUID = -3653870580234213024L;
	protected String messageKey;

	public BaseException() {
		
	}

	public BaseException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public BaseException(Throwable throwable) {
		super(throwable);
	}

	public BaseException(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return this.messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
}