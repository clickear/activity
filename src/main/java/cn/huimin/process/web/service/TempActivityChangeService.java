package cn.huimin.process.web.service;


import cn.huimin.process.web.dto.SimpleResult;
import com.alibaba.fastjson.JSONObject;

/**
 * 临时签
 */
public interface TempActivityChangeService {

    /**
     * 加签功能 加串行的签
     * @param taskId
     * @param processId
     * @return
     */
    public void addActivity(String taskId, String processId,String user,String activityName,String doUserId) throws Exception;

    /**
     * 剪掉临时加的签
     * @param pricessId
     * @param taskId
     * @param doUserId
     */
    public SimpleResult cutCurrentActivity(String pricessId,String taskId,String doUserId);


    /**
     * 为前往查看临时加签的做数据准备
     * @param id
     * @return
     */
    public JSONObject queryTempActData(Integer id);

    /**
     * 判定是否已经加过签 如果加过了获取临时签的id
     * @param pricessId
     * @param taskId
     * @param doUserId
     * @return
     */
    public SimpleResult isAddActivity(String pricessId,String taskId,String doUserId);

    /**
     * 加签功能 加并行的签
     * @param taskId
     * @param processId
     * @return
     */
    public void addJoinActivity(String taskId, String processId,String user,String activityName,String doUserId) throws Exception;



    /**
     * 会签操作
     *
     * @param taskId    当前任务ID
     * @param userCodes 会签人账号集合
     * @throws Exception
     */
    public void jointProcess(String taskId,String processId, String userCodes) throws Exception;
}
