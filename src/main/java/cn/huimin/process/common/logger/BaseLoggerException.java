package cn.huimin.process.common.logger;

import cn.huimin.process.common.BaseException;

public class BaseLoggerException extends BaseException {
	
	
	private static final long serialVersionUID = 5624671892341864006L;
	public BaseLoggerException(String defineCode) {
		super(defineCode);
	}
	public static BaseLoggerException newException(BaseException exception) {
		BaseLoggerException e = new BaseLoggerException(exception.getDefineCode());
		e.id = exception.getId();
		e.message = exception.getMessage();
		e.defineCode = exception.getDefineCode();
		e.setStackTrace(exception.getStackTrace());
		return (BaseLoggerException) BaseException.newException(e, exception.getMessage(), new Object[0]);
	}
}
