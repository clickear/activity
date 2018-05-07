package cn.huimin.process.web.dao;


import cn.huimin.process.web.model.ProcessData;

import java.util.List;

/**
 * 查询流程相关信息
 */
public interface ProcessDataDao {
    /**
     * 获取待发事项
     */
    List<ProcessData>  queryRestartProcess(ProcessData processData);

    /**
     * 查询启动的流程
     * @param processData
     * @return
     */
    List<ProcessData>  queryStartedProcess(ProcessData processData);


    /**
     * 通过流程实例id获取流程信息
     * @param processInstanceId
     * @return
     */
    ProcessData  getProcessInstanceInfoByProcessInstanceId(String processInstanceId);




}
