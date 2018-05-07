package cn.huimin.process.core;

import java.util.*;

import cn.huimin.process.core.rule.RuleManager;
import cn.huimin.process.core.rule.UserRule;
import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.EhrRequestApiUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import cn.huimin.process.web.util.ReadFileUtils;
import com.alibaba.fastjson.JSONArray;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;

import cn.huimin.process.processengine.ProcessEngineConfigurationEx;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class ShareniuParallelMultiInstanceBehavior extends ParallelMultiInstanceBehavior {
    /**
	 * 
	 */
	private static final long serialVersionUID = -98934989753532936L;
	private ProcessEngineConfigurationEx processEngineConfigurationEx;

    public ShareniuParallelMultiInstanceBehavior(ActivityImpl activity,
                                                 AbstractBpmnActivityBehavior originalActivityBehavior) {
        super(activity, originalActivityBehavior);
    }

    @Override
    protected int resolveNrOfInstances(ActivityExecution execution) {
        //获取岗位id
        List<String> userIds = getUserIds(execution);
        if (userIds.size() > 0) {
            return userIds.size();
        }
        return -1;
        //return super.resolveNrOfInstances(execution);
    }

    @Override
    protected void createInstances(ActivityExecution execution) throws Exception {
        //调用
        int nrOfInstances = this.resolveNrOfInstances(execution);
        if (nrOfInstances < 0) {
            throw new ActivitiIllegalArgumentException("Invalid number of instances: must be non-negative integer value"
                    + ", but was " + nrOfInstances);
        }
        setLoopVariable(execution, NUMBER_OF_INSTANCES, nrOfInstances);
        setLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES, 0);
        setLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES, nrOfInstances);
        List<ActivityExecution> concurrentExecutions = new ArrayList<ActivityExecution>();
        for (int loopCounter = 0; loopCounter < nrOfInstances; loopCounter++) {
            ActivityExecution concurrentExecution = execution.createExecution();
            concurrentExecution.setActive(true);
            concurrentExecution.setConcurrent(true);
            concurrentExecution.setScope(false);

            // In case of an embedded subprocess, and extra child execution is required
            // Otherwise, all child executions would end up under the same parent,
            // without any differentiation to which embedded subprocess they belong
            if (isExtraScopeNeeded()) {
                ActivityExecution extraScopedExecution = concurrentExecution.createExecution();
                extraScopedExecution.setActive(true);
                extraScopedExecution.setConcurrent(false);
                extraScopedExecution.setScope(true);
                concurrentExecution = extraScopedExecution;
            }

            concurrentExecutions.add(concurrentExecution);
            logLoopDetails(concurrentExecution, "initialized", loopCounter, 0, nrOfInstances, nrOfInstances);
        }

        // Before the activities are executed, all executions MUST be created up front
        // Do not try to merge this loop with the previous one, as it will lead to bugs,
        // due to possible child execution pruning.
        for (int loopCounter = 0; loopCounter < nrOfInstances; loopCounter++) {
            ActivityExecution concurrentExecution = concurrentExecutions.get(loopCounter);
            // executions can be inactive, if instances are all automatics (no-waitstate)
            // and completionCondition has been met in the meantime
            if (concurrentExecution.isActive() && !concurrentExecution.isEnded()
                    && concurrentExecution.getParent().isActive()
                    && !concurrentExecution.getParent().isEnded()) {
                setLoopVariable(concurrentExecution, getCollectionElementIndexVariable(), loopCounter);
                executeOriginalBehavior(concurrentExecution, loopCounter);
            }
        }

        // See ACT-1586: ExecutionQuery returns wrong results when using multi instance on a receive task
        // The parent execution must be set to false, so it wouldn't show up in the execution query
        // when using .activityId(something). Do not we cannot nullify the activityId (that would
        // have been a better solution), as it would break boundary event behavior.
        if (!concurrentExecutions.isEmpty()) {
            ExecutionEntity executionEntity = (ExecutionEntity) execution;
            executionEntity.setActive(false);
        }
    }

    @SuppressWarnings("unused")
    protected void executeOriginalBehavior(ActivityExecution execution, int loopCounter) throws Exception {
        if (usesCollection() && collectionElementVariable != null) {
            Collection collection = null;
            if (collectionExpression != null) {
                collection = (Collection) collectionExpression.getValue(execution);
            } else if (collectionVariable != null) {
                collection = (Collection) execution.getVariable(collectionVariable);
            }
            Object value = null;
            int index = 0;
            Iterator it = collection.iterator();
            while (index <= loopCounter) {
                value = it.next();
                index++;
            }
            setLoopVariable(execution, collectionElementVariable, value);
        }
        Object property = activity.getProperty("shareniuExt");// 获取shareniuExt属性值
        Map<String, List<ExtensionAttribute>> extensionAttribute = null;
        TaskDefinition taskDefinition = (TaskDefinition) activity.getProperty("taskDefinition");
        ExpressionManager expressionManager = processEngineConfigurationEx.getExpressionManager();

        if (property != null) {
            extensionAttribute = (Map<String, List<ExtensionAttribute>>) property;
        }
        List<String> id = null;
        if (extensionAttribute != null) {
            id = getUserIds(execution);
            String dateTime = this.getValue(extensionAttribute, HmXMLConstants.DUE_DATE_SET);
            if (!ObjectCheckUtils.isEmptyString(dateTime)) {
                DateTime dateTime1 = new DateTime(DateUtils.calHour(dateTime));
                Expression expression = expressionManager.createExpression(String.valueOf(dateTime1));
                taskDefinition.setDueDateExpression(expression);
            }
        }

        String assignee = id.get(loopCounter);
        taskDefinition.setAssigneeExpression(expressionManager.createExpression(assignee));
        if (loopCounter == 0) {
            callCustomActivityStartListeners(execution);
            innerActivityBehavior.execute(execution);
        } else {
            execution.executeActivity(activity);
        }
    }

    private List<Expression> getRealAssigneeLists(TaskDefinition taskDefinition) {
        Set<Expression> candidateUserIdExpressions = taskDefinition.getCandidateUserIdExpressions();
        List<Expression> list = new ArrayList<>();
        Iterator<Expression> iterator = candidateUserIdExpressions.iterator();
        while (iterator.hasNext()) {
            Expression expression = (Expression) iterator.next();
            list.add(expression);
        }
        return list;
    }

    public static String getRealAssignee(String assigneeExpressionText, int index) {
        String[] split = assigneeExpressionText.split(",");
        return split[index];
    }

    public void setProcessEngineConfigurationEx(ProcessEngineConfigurationEx processEngineConfigurationEx) {
        this.processEngineConfigurationEx = processEngineConfigurationEx;
    }

    /**
     * 覆盖执行器
     *
     * @param execution
     */
    @Override
    public void leave(ActivityExecution execution) {
        callActivityEndListeners(execution);

        int nrOfInstances = getLoopVariable(execution, NUMBER_OF_INSTANCES);
        if (nrOfInstances == 0) {
            // Empty collection, just leave.
            super.leave(execution);
            return;
        }

        int loopCounter = getLoopVariable(execution, getCollectionElementIndexVariable());
        int nrOfCompletedInstances = getLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES) + 1;
        int nrOfActiveInstances = getLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES) - 1;

        if (isExtraScopeNeeded()) {
            // In case an extra scope was created, it must be destroyed first before going further
            ExecutionEntity extraScope = (ExecutionEntity) execution;
            execution = execution.getParent();
            extraScope.remove();
        }

        if (execution.getParent() != null) { // will be null in case of empty collection
            setLoopVariable(execution.getParent(), NUMBER_OF_COMPLETED_INSTANCES, nrOfCompletedInstances);
            setLoopVariable(execution.getParent(), NUMBER_OF_ACTIVE_INSTANCES, nrOfActiveInstances);
        }
        logLoopDetails(execution, "instance completed", loopCounter, nrOfCompletedInstances, nrOfActiveInstances, nrOfInstances);

        ExecutionEntity executionEntity = (ExecutionEntity) execution;

        if (executionEntity.getParent() != null) {

            executionEntity.inactivate();
            executionEntity.getParent().forceUpdate();

            List<ActivityExecution> joinedExecutions = executionEntity.findInactiveConcurrentExecutions(execution.getActivity());
            if (joinedExecutions.size() >= nrOfInstances || completionConditionSatisfied(execution)) {

                // Removing all active child executions (ie because completionCondition is true)
                List<ExecutionEntity> executionsToRemove = new ArrayList<ExecutionEntity>();
                for (ActivityExecution childExecution : executionEntity.getParent().getExecutions()) {
                    if (childExecution.isActive()) {
                        executionsToRemove.add((ExecutionEntity) childExecution);
                    }
                }
                for (ExecutionEntity executionToRemove : executionsToRemove) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Execution {} still active, but multi-instance is completed. Removing this execution.", executionToRemove);
                    }
                    executionToRemove.inactivate();
                    executionToRemove.deleteCascade("multi-instance completed");
                }
                executionEntity.takeAll(executionEntity.getActivity().getOutgoingTransitions(), joinedExecutions);
            }

        } else {
            super.leave(executionEntity);
        }
    }

    /**
     * 获取的唯一一个属性
     *
     * @param map
     * @param ruleKey
     * @return
     */
    private String getValue(Map<String, List<ExtensionAttribute>> map, String ruleKey) {
        List<ExtensionAttribute> list = map.get(ruleKey);
        if (list != null && list.size() > 0) {
            for (ExtensionAttribute extensionAttribute2 : list) {
                if (!StringUtils.isEmpty(extensionAttribute2.getValue())) {
                    return extensionAttribute2.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 获取规则设置对应的人
     *
     * @param execution
     * @return
     */
    private List<String> getUserIds(ActivityExecution execution) {
        Map<String, UserRule> ruleMap = RuleManager.produceUserRule();
        List<String> userIds = new ArrayList<>();
        Set<String> keys = ruleMap.keySet();
        for (String key : keys) {
            List<String> list = ruleMap.get(key).getUsersId(activity, execution);
            for (String id : list) {
                if (!userIds.contains(id)) {
                    userIds.add(id);
                }
            }
        }
        return userIds;
    }
}
