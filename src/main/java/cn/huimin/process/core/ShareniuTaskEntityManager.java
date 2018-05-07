package cn.huimin.process.core;

import java.util.List;

import cn.huimin.process.web.dto.HandlerLog;
import cn.huimin.process.web.dto.NodeHandleType;
import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import com.alibaba.fastjson.JSON;
import org.activiti.engine.impl.TaskQueryImpl;
import org.activiti.engine.impl.juel.Node;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.task.Task;

public class ShareniuTaskEntityManager  extends TaskEntityManager{
	@Override
	public List<Task> findTasksByQueryCriteria(TaskQueryImpl taskQuery) {
		return super.findTasksByQueryCriteria(taskQuery);
	}
	@Override
	public void deleteTask(String taskId, String deleteReason, boolean cascade) {
		super.deleteTask(taskId, deleteReason, cascade);
	}

	@Override
	public void deleteTasksByProcessInstanceId(String processInstanceId, String deleteReason, boolean cascade) {
		super.deleteTasksByProcessInstanceId(processInstanceId, deleteReason, cascade);
	}

	@Override
	public void deleteTask(TaskEntity task, String deleteReason, boolean cascade) {
		ExecutionEntity executionEntity =task.getExecution();
		if(isPaExecution(executionEntity) && task.DELETE_REASON_DELETED.equals(deleteReason)&& !ObjectCheckUtils.isEmptyString(task.getAssignee())){
			deleteReason = makeDeleteReson(task);
		}
		super.deleteTask(task, deleteReason, cascade);
	}


	/**
	 * 是否是多实例的删除其他多实例的结果
	 * @param executionEntity
	 * @return
     */
	private boolean isPaExecution(ExecutionEntity executionEntity){
		while (executionEntity !=null){
			executionEntity =executionEntity.getParent();
		}
		if(executionEntity ==null){
			return true;
		}
		return false;
	}

	/**
	 * 执行
	 * @param taskEntity
	 * @return
     */
	private String makeDeleteReson(TaskEntity taskEntity){
		HandlerLog handlerLog = new HandlerLog();
		handlerLog.setHandleUser(taskEntity.getAssignee());
		handlerLog.setHandleTime(DateUtils.now());
		handlerLog.setType(NodeHandleType.NODE_PARALLEL_DELETE);
		return JSON.toJSONString(handlerLog);
	}


}
