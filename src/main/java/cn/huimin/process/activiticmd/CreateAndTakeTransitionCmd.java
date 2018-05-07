package cn.huimin.process.activiticmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;

/**
 * 手动操作
 * 创建一个节点并且添加节点名称
 *      
 * wyp
 */
public class CreateAndTakeTransitionCmd implements Command<Void>
{

    private ActivityImpl _activity;

    private String _executionId;

    public CreateAndTakeTransitionCmd(String executionId, ActivityImpl activity)
    {
        _executionId = executionId;
        _activity = activity;
    }

    @Override
    public Void execute(CommandContext commandContext)
    {

        ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(_executionId);
        execution.setActivity(_activity);
        execution.performOperation(AtomicOperation.TRANSITION_CREATE_SCOPE);
        return null;
    }
}
