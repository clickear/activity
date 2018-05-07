package cn.huimin.process.web.service;

/**
 * Created by wyp on 2017/3/9.
 */
public interface APIParameterCheck {

    /**
     *
     * @param param 校验的参数
     * @return
     */
    boolean commonParamCheck(Object param);

    /**
     * 对是否是json格式的字符串校验
     * @param jsonStringParam
     * @return
     */
     boolean jsonStringParamCheck(String jsonStringParam);

    /**
     * 判定该流程实例id是否存在
     * @param processInstanceId
     * @return
     */
    boolean checkProcessInstanceExist(String processInstanceId);


}
