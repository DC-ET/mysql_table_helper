package com.yjy.mysql.exception;

public class DaoException extends BaseException {
	
	private static final long serialVersionUID = -9006104640618533135L;

	public DaoException(String messageKey) {
		super.setMessageKey(messageKey);
	}

	public DaoException(String messageKey, Throwable t) {
		super.setMessageKey(messageKey);
		super.initCause(t);
	}

	public DaoException(Throwable t) {
		super.setMessageKey(t.getClass().getSimpleName());
		super.initCause(t);
	}

	public Throwable getOrignalException() {
		Throwable t = getCause();
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t;
	}

	public String getOrignalMessageKey() {
		return getOrignalException().getClass().getSimpleName();
	}
}