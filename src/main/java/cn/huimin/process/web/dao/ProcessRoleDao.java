package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.ProcessRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 */
public interface ProcessRoleDao {
    List<String> pageQueryProcessKey(@Param("branchid") Integer branchid, @Param("departmentid") Integer departmentid,@Param("roleid") Integer roleid, @Param("start") Integer start, @Param("max") Integer max);

    void insert(ProcessRole processRole);
    Long processRoleCount(@Param("branchid") Integer branchid, @Param("departmentid") Integer departmentid,@Param("roleid") Integer roleid);



}
