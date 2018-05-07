package cn.huimin.process.core;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;

public class UpdateHiTaskReasonCommand implements Command<Void> {
	protected String taskId;
	protected String deleteReason;

	public UpdateHiTaskReasonCommand(String taskId, String deleteReason) {
		this.taskId = taskId;
		this.deleteReason = deleteReason;
	}
	@Override
	public Void execute(CommandContext commandContext) {
		HistoricTaskInstanceEntity historicTaskInstance = commandContext
				.getDbSqlSession().selectById(HistoricTaskInstanceEntity.class,taskId);
		if (historicTaskInstance != null) {
			historicTaskInstance.markEnded(deleteReason);
		}
		return null;
	}

}
