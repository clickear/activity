package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.ActCreationEx;

import java.util.List;

/**
 * 流程创建表扩展
 */
public interface ActCreationDaoEx {
    /**
     * 条件查询
     * @param actCreationEx
     * @return
     */
    List<ActCreationEx> query(ActCreationEx actCreationEx);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    ActCreationEx queryById(Integer id);

    /**
     * 删除
     * @param id
     */
    void delete(Integer id);

    void update(String processInstanceId);


}
