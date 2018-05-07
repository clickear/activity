package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.ProcessPriority;
import org.apache.ibatis.annotations.Param;

public interface  ProcessPriorityDao {
	ProcessPriority selectByProcInstId(String procInstId);

	int insertProcessPriority(ProcessPriority processPriority);

	int updateProcessPriorityByProcessInstanceId(ProcessPriority processPriority);
	int deleteProcessPriorityByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}
