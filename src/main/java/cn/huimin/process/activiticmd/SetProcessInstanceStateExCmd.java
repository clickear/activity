package cn.huimin.process.activiticmd;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.SuspensionState;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.Execution;

import java.util.List;

/**
 * Created by wyp on 2017/5/5.
 * 设置流程实例状态 命令器
 */
public class SetProcessInstanceStateExCmd implements Command<Void>{

    private String executionId;

    private Integer state;
    //流程状态
    public SetProcessInstanceStateExCmd(String executionId,Integer state){
        this.executionId = executionId;
        this.state = state;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if(executionId == null) {
            throw new ActivitiIllegalArgumentException("ProcessInstanceId cannot be null.");
        }
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager()
                .findExecutionById(executionId);

        if(executionEntity == null) {
            throw new ActivitiObjectNotFoundException("Cannot find processInstance for id '"+executionId+"'.", Execution.class);
        }
        if(!executionEntity.isProcessInstanceType()) {
            throw new ActivitiException("Cannot set suspension state for execution '"+executionId+"': not a process instance.");
        }
        executionEntity.setSuspensionState(state);
        // All child executions are suspended
        List<ExecutionEntity> childExecutions = commandContext.getExecutionEntityManager().findChildExecutionsByProcessInstanceId(executionId);
        for (ExecutionEntity childExecution : childExecutions) {
            if (!childExecution.getId().equals(executionId)) {
                executionEntity.setSuspensionState(state);
            }
        }
        List<TaskEntity> tasks = commandContext.getTaskEntityManager().findTasksByProcessInstanceId(executionId);
        for (TaskEntity taskEntity : tasks) {
           taskEntity.setSuspensionState(state);
        }
        return null;
    }
}
