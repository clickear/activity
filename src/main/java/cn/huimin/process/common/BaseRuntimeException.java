package cn.huimin.process.common;

import java.text.MessageFormat;
import java.util.UUID;

public class BaseRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2142702415424932715L;
	private String id;
	private String message;

	public BaseRuntimeException() {
		initId();
	}

	public BaseRuntimeException(Throwable throwable) {
		setMessage(throwable.getMessage());
		setStackTrace(throwable.getStackTrace());
		initId();
	}

	public BaseRuntimeException(String message, Throwable throwable, Object... args) {
		setMessageFormat(message, args);
		setStackTrace(throwable.getStackTrace());
		initId();
	}

	public BaseRuntimeException(String message, Object... args) {
		setMessageFormat(message, args);
		initId();
	}

	private void initId() {
		this.id = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
	}

	private void setMessageFormat(String message, Object... args) {
		if ((message != null) && (args != null)) {
			setMessage(MessageFormat.format(message, args));
		} else {
			setMessage(message);
		}
	}

	public String getId() {
		return this.id;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("exception id : ").append(this.id).append(",").append(this.message);
		return sb.toString();
	}

}
