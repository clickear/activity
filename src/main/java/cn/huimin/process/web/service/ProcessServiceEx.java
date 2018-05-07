package cn.huimin.process.web.service;


import cn.huimin.process.web.dto.APISimpleResult;
import cn.huimin.process.web.model.ProcessData;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * Created by wyp on 2017/3/10.
 * 流程相关service扩展
 */
public interface ProcessServiceEx {


    /**
     * 流程启动测试版本
     * @param parms
     * @return
     */
  APISimpleResult start(Map<String, Object> parms, String startId, String key, String systemId,APISimpleResult apiSimpleResult);

    /**
     * 流程删除
     * @param userId
     * @param taskId
     */
    void processInstanceDelete(String userId, String taskId,String remark,String type);

  /**
   * 查询已发事项 这里有局限性后期修改
   * @param processData
   * @param start
   * @param size
     * @return
     */
   PageInfo<ProcessData> queryProcessInstancesByStarterId(ProcessData processData,Integer start, Integer size);


    /**
     * 业务系统直接调接口删除该流程
     * @param systemId
     * @param userId
     * @param processInstanceId
     */
    public  void processInstanceDeleteBySystemId(String systemId, String userId, String processInstanceId);


    /**
     * 流程被驳回
     * @param processInstanceId
     * @param jsonObject 驳回信息
     */

    public  void processInstanceDeleteByReject(String processInstanceId,String taskId,String userId,JSONObject jsonObject);
  /**
   * 业务系统是否可以删除该流程
   * @param processInstanceId
   * @param systemId
   * @return
     */
  boolean isCanProcessInstanceDeleteBySystemId(String processInstanceId,String systemId);

  /**
   * 流程挂体
   * @param processInstanceId
     */
   void processSuppend(String processInstanceId);

  /**
   *
   * @param processInstanceId
     */
  void  processActive(String processInstanceId);

    /**
     * 根据流程id获取表单数据
     * @param processInstanceId
     * @return
     */
    Map<String, Object> getFormInfoByProcessInstanceId(String processInstanceId);

    /**
     *
     * @param key
     * @param userId
     * @param roleId
     * @return
     */
    public void processCheck(String key,String userId,String roleId,APISimpleResult simpleResult) throws Exception;

    }
