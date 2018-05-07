package cn.huimin.process.web.service;

import cn.huimin.process.web.model.UserTaskVarPool;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by wyp on 2017/3/18.
 */
public interface UserTaskVarPoolService {
    /**
     * 添加或更改流程常量池
     * @param userTaskVarPool
     */
    void insertOrUpdate(UserTaskVarPool userTaskVarPool);

    /**
     * 根据流程key获取流程变量池
     * @param userTaskVarPool
     * @return
     */
    UserTaskVarPool getByProcessKey(UserTaskVarPool userTaskVarPool);

    /**
     * 通过流程key和任务节点key
     * @param processDefId
     * @param taskKey
     * @return
     */
     JSONArray getUserTaskVarByProcessKeyAndTaskKey(String processDefId,String taskKey);

    /**
     * 通过流程key和任务节点key获取对应的表单信息
     * @param processDefId
     * @param taskKey
     * @return
     */
    JSONArray getFormPropertiesVarByProcessKeyAndTaskKey(String processDefId, String taskKey);


    }
