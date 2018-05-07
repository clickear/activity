package cn.huimin.process.web.dto;

import java.io.Serializable;

/**
 * API 返回Bean
 *
 * @author shareniu
 */
public class APISimpleResult implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 6278501798801287960L;
	protected String message = null;
    protected int result = 0;
    protected Object data = null;
    protected String errorMsg;
    protected String errorCode;

    public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
	@Override
	public String toString() {
		return "APISimpleResult [message=" + message + ", result=" + result + ", data=" + data + ", errorMsg="
				+ errorMsg + "]";
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


}
