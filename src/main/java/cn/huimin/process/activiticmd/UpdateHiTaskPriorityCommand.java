package cn.huimin.process.activiticmd;

import java.util.List;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;

public class UpdateHiTaskPriorityCommand implements Command<Void> {
	protected String processInstanceId;
	protected int priority;
	
	
	public UpdateHiTaskPriorityCommand(String processInstanceId, int priority) {
		this.processInstanceId = processInstanceId;
		this.priority = priority;
	}


	@Override
	public Void execute(CommandContext commandContext) {
		List<HistoricTaskInstance> historicTaskInstances = commandContext.getDbSqlSession().createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
		for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
			HistoricTaskInstanceEntity  h=(HistoricTaskInstanceEntity) historicTaskInstance;
			h.setPriority(priority);
		}
		return null;
	}

}
