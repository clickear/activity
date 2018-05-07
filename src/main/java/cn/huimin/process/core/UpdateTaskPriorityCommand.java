package cn.huimin.process.core;

import java.util.List;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

public class UpdateTaskPriorityCommand implements Command<Void> {
	protected int priority;
	protected String processInstanceId;

	public UpdateTaskPriorityCommand(int priority, String processInstanceId) {
		this.priority = priority;
		this.processInstanceId = processInstanceId;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		List<TaskEntity> findTasksByProcessInstanceId = commandContext
				.getTaskEntityManager().findTasksByProcessInstanceId(
						processInstanceId);
		for (TaskEntity taskEntity : findTasksByProcessInstanceId) {
			commandContext.getTaskEntityManager().findTaskById(
					taskEntity.getId());
			taskEntity.setPriority(priority);
		}
		return null;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
