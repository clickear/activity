package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.UserTaskVarPool;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wyp on 2017/3/18.
 */
public interface UserTaskVarPoolDao {


    void insert(UserTaskVarPool userTaskVarPool);

    void update(UserTaskVarPool userTaskVarPool);

    /**
     * 根据流程key
     * @param userTaskVarPool
     * @return
     */
    UserTaskVarPool getByProcessKey(UserTaskVarPool userTaskVarPool);

    /**
     * 根据流程定义id
     * @param userTaskVarPool
     * @return
     */
    UserTaskVarPool getByProcessDefId(UserTaskVarPool userTaskVarPool);

    /**
     * 根据流程key获取未发布的任务相关变量
     * @param processKey
     * @return
     */
    List<UserTaskVarPool>  getListByProcessKey(String processKey);

}
