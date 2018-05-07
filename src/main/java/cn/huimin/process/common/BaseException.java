package cn.huimin.process.common;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.UUID;

public class BaseException extends RuntimeException {
	private static final long serialVersionUID = 811520235166861591L;
	protected String id;
	protected String message;
	protected String defineCode;

	protected BaseException(String defineCode) {
		this.defineCode = defineCode;
		initId();
	}

	private void initId() {
		this.id = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
	}

	public String getId() {
		return this.id;
	}

	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("exception id : ").append(this.id).append(",").append(this.message);
		return sb.toString();
	}

	public void setMessage(String message, Object... args) {
		this.message = MessageFormat.format(message, args);
	}

	public String getDefineCode() {
		return this.defineCode;
	}

	public static <T extends BaseException> T newException(T exception, String message, Object... args) {
		if (exception == null) {
			throw new RuntimeException("no exception instance specified");
		}
		try {
			Constructor constructor = exception.getClass().getDeclaredConstructor(new Class[] { String.class });
			constructor.setAccessible(true);
			T newException = (T) constructor.newInstance(new Object[] { exception.getDefineCode() });
			newException.setMessage(message, args);
			return newException;
		} catch (Throwable e) {
			throw new RuntimeException("create exception instance fail : " + e.getMessage(), e);
		}
	}

	public boolean codeEquals(BaseException e) {
		if (e == null) {
			return false;
		}
		if (!e.getClass().equals(getClass())) {
			return false;
		}
		if (!e.getDefineCode().equals(getDefineCode())) {
			return false;
		}
		return true;
	}
}
