package cn.huimin.process.web.service;

import cn.huimin.process.web.model.CheckData;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by wyp on 2017/4/11.
 */
public interface LogService {

    /**
     * 查看流程操作日志
     * @param processInstanceId
     * @return
     */
    List<CheckData> queryHandleLogByProcessInstanceId(String processInstanceId);

    /**
     * 查询审核日志
     * @param processInstanceId
     * @return
     */
    List<JSONObject> queryCheckLogByProcessInstanceId(String processInstanceId,String type);
}
