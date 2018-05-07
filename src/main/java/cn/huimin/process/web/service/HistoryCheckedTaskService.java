package cn.huimin.process.web.service;


import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.dto.SimpleResult;

/**
 * 取回已审批的节点
 */
public interface HistoryCheckedTaskService {



    /**
     * 分页查询自己审批过的流程审批的结果
     * @param authId
     * @param startResult
     * @param maxResult
     * @return
     */
    public Page queryCheckedTaskCompletedByAuthId(String authId, int startResult, int maxResult);



    /***
     * 取回操作
     * @param processId 流程实例
     * @param taskId 历史活动节点
     * @return
     */
    SimpleResult doPick(String processId, String taskId,String userId) throws Exception;



    /**
     * 查看是否支持取回
     * @param processId
     * @param taskId
     * @return
     */
    boolean isPick(String processId,String taskId);
}
