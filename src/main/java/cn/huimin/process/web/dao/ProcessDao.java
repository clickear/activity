package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.ProcessDefEntity;

import java.util.List;

/**
 * Created by Administrator on 2017/1/3.
 */
public interface ProcessDao {

    public List<ProcessDefEntity> queryProcessByGroupId(String groupId);

    public List<ProcessDefEntity> queryProcessByNotGroupId(String groupId);

    public List<ProcessDefEntity> queryPageAllProcess(ProcessDefEntity processDefEntity);

    ProcessDefEntity queryProcessByKey(String defKey);



}
