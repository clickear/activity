package cn.huimin.process.web.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-8-19.
 */
public class ErrorBean  implements Serializable{
    private String error_msg;
    private boolean success;

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
