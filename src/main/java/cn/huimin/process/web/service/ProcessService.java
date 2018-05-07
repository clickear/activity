package cn.huimin.process.web.service;

/**
 * Created by Administrator on 2016-7-19.
 */
public interface ProcessService {


    /**
     * 流程启动测试版本
     * @param parms
     * @return
     *//*
    public SimpleResult start(Map<String, Object> parms,String startId,String key);


    *//**
     * 完成任务
     * @param systemId
     * @param userId
     * @param taskId
     * @param parameter
     *//*
    void doTask(String systemId, String userId,String taskId,JSONObject parameter);



    *//**
     * 未带变量的发起
     * @param startId
     * @param key
     * @return
     *//*
    @Transactional
    public SimpleResult startProcessByKey(String startId,String key);



    *//**
     * 记录特殊操作
     * @param taskId
     * @param doWhat
     * @param remark
     * @return
     *//*
    SimpleResult specialDo(String taskId,String userId,String doWhat,String remark);

    *//**
     * 完成审批
     * @param params
     * @return
     *//*
    SimpleResult doTask(Map<String,String> params);

    *//**
     * 完成审批任务
     * @param taskId
     *//*
     void doTask(String taskId);


    *//**
     * 完成确认的任务
     * @param user
     * @param processId
     * @return
     *//*
     SimpleResult doTaskConfirm(String user,String processId);


    *//**
     * 流程挂起
     * @param processId
     * @return
     *//*
    SimpleResult processSuppend(String processId);

    *//**
     * 流程启动
     * @param processId
     * @return
     *//*
    SimpleResult processStart(String processId);*/

}
