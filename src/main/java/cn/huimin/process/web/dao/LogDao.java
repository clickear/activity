package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.CheckData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wyp on 2017/4/11.
 */
public interface LogDao {
    /**
     * 查看流程操作日志
     * @param processInstanceId
     * @return
     */
    List<CheckData> queryHandleLogByProcessInstanceId(String processInstanceId);

    /**
     * 查询审核日志 json数据
     * @param processInstanceId
     * @return
     */
    List<String> queryCheckLogByProcessInstanceId(@Param("processInstanceId") String processInstanceId,@Param("type") String type);
}
