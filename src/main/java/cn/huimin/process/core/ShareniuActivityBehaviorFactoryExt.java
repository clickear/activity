package cn.huimin.process.core;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.IOParameter;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.*;
import org.activiti.engine.impl.bpmn.data.SimpleDataInputAssociation;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.bpmn.webservice.MessageImplicitDataOutputAssociation;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.huimin.process.processengine.ProcessEngineConfigurationEx;

/**
 * @author shareniu
 */
public class ShareniuActivityBehaviorFactoryExt extends
        DefaultActivityBehaviorFactory implements ApplicationContextAware {
    protected ApplicationContext applicationContext;
    private  ProcessEngineConfigurationEx processEngineConfigurationEx;
    // TODO 稍微有问题
    public UserTaskActivityBehavior createUserTaskActivityBehavior(
            UserTask userTask, TaskDefinition taskDefinition) {
		ShareniuUserTaskActivityBehaviorExt shareniuUserTaskActivityBehaviorExt = (ShareniuUserTaskActivityBehaviorExt) applicationContext.getBean("shareniuUserTaskActivityBehaviorExt");
    	shareniuUserTaskActivityBehaviorExt.setTaskDefinition(taskDefinition);
    	shareniuUserTaskActivityBehaviorExt.setUserTaskId(userTask.getId());
    	shareniuUserTaskActivityBehaviorExt.setApplicationContext(applicationContext);
        return shareniuUserTaskActivityBehaviorExt;
    }


    @Override
    public CallActivityBehavior createCallActivityBehavior(CallActivity callActivity) {

        String expressionRegex = "\\$+\\{+.+\\}";

        CallActivityBehavior callActivityBehaviour = null;
        if (StringUtils.isNotEmpty(callActivity.getCalledElement()) && callActivity.getCalledElement().matches(expressionRegex)) {
            callActivityBehaviour = new HmCallActivityBehavior(expressionManager.createExpression(callActivity.getCalledElement()), callActivity.getMapExceptions());
        } else {
            callActivityBehaviour = new HmCallActivityBehavior(callActivity.getCalledElement(), callActivity.getMapExceptions());
        }
        callActivityBehaviour.setInheritVariables(callActivity.isInheritVariables());

        for (IOParameter ioParameter : callActivity.getInParameters()) {
            if (StringUtils.isNotEmpty(ioParameter.getSourceExpression())) {
                Expression expression = expressionManager.createExpression(ioParameter.getSourceExpression().trim());
                callActivityBehaviour.addDataInputAssociation(new SimpleDataInputAssociation(expression, ioParameter.getTarget()));
            } else {
                callActivityBehaviour.addDataInputAssociation(new SimpleDataInputAssociation(ioParameter.getSource(), ioParameter.getTarget()));
            }
        }

        for (IOParameter ioParameter : callActivity.getOutParameters()) {
            if (StringUtils.isNotEmpty(ioParameter.getSourceExpression())) {
                Expression expression = expressionManager.createExpression(ioParameter.getSourceExpression().trim());
                callActivityBehaviour.addDataOutputAssociation(new MessageImplicitDataOutputAssociation(ioParameter.getTarget(), expression));
            } else {
                callActivityBehaviour.addDataOutputAssociation(new MessageImplicitDataOutputAssociation(ioParameter.getTarget(), ioParameter.getSource()));
            }
        }
        return callActivityBehaviour;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
	@Override
	public ParallelMultiInstanceBehavior createParallelMultiInstanceBehavior(ActivityImpl activity,
			AbstractBpmnActivityBehavior innerActivityBehavior) {
		ShareniuParallelMultiInstanceBehavior shareniuParallelMultiInstanceBehavior = new ShareniuParallelMultiInstanceBehavior(activity,innerActivityBehavior);
		shareniuParallelMultiInstanceBehavior.setProcessEngineConfigurationEx(processEngineConfigurationEx);
		return shareniuParallelMultiInstanceBehavior;
	}

    /**
     * 创建自定义的串行多实例
     * @param activity
     * @param innerActivityBehavior
     * @return
     */
    @Override
    public SequentialMultiInstanceBehavior createSequentialMultiInstanceBehavior(ActivityImpl activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
      ShareniuSequentialMultiInstanceBehavior shareniuSequentialMultiInstanceBehavior =  new ShareniuSequentialMultiInstanceBehavior(activity, innerActivityBehavior);
        //return super.createSequentialMultiInstanceBehavior(activity, innerActivityBehavior);
        shareniuSequentialMultiInstanceBehavior.setProcessEngineConfigurationEx(processEngineConfigurationEx);
        return shareniuSequentialMultiInstanceBehavior;
    }


	public void setProcessEngineConfigurationEx(ProcessEngineConfigurationEx processEngineConfigurationEx) {
		this.processEngineConfigurationEx = processEngineConfigurationEx;
	}

	public ProcessEngineConfigurationEx getProcessEngineConfigurationEx() {
		return processEngineConfigurationEx;
	}
    
}
