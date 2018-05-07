package cn.huimin.process.web.service;

import cn.huimin.process.web.model.ActCreationEx;

import java.util.List;

/**
 * Created by wyp on 2017/4/12.
 */
public interface ActCreationServiceEx {

    /**
     * 查询
     * @param actCreationEx
     * @return
     */
    List<ActCreationEx> query(ActCreationEx actCreationEx);

    /**
     * 根据流程实例id更新完成的状态
     * @param processInstanceId
     */
    void updateByProcessInstanceId(String processInstanceId);
}
