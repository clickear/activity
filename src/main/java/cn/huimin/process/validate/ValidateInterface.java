package cn.huimin.process.validate;

import java.util.Map;

/**
 * Created by wyp on 2017/5/4.
 * 校验器
 * 校验请求参数
 */
public interface ValidateInterface {
    /**
     * 设置要校验的参数
     * @param parms
     */
    void setParms(Map<String,String> parms);

    /**
     * 校验参数的结果
     * @return
     */
    boolean validateParms();

    /**
     * 获取错误信息
     * @return
     */
    String getErrorMessage();

    /**
     * 获取所有的
     * @return
     */
    Map<String,Object> getParms();
}
