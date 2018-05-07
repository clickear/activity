package cn.huimin.process.core;

import cn.huimin.process.activiticmd.StartActivityCmd;
import cn.huimin.process.core.rule.RuleManager;
import cn.huimin.process.core.rule.UserRule;
import cn.huimin.process.web.dao.ProcessPriorityDao;
import cn.huimin.process.web.dto.HandlerLog;
import cn.huimin.process.web.dto.NodeHandleType;
import cn.huimin.process.web.model.ProcessPriority;
import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.NodeHandlerUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.DynamicBpmnConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.helper.SkipExpressionUtil;
import org.activiti.engine.impl.calendar.BusinessCalendar;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * @author shareniu
 */	
public class ShareniuUserTaskActivityBehaviorExt extends
        UserTaskActivityBehavior {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ShareniuUserTaskActivityBehaviorExt.class);
    protected ApplicationContext applicationContext;
   
    public ShareniuUserTaskActivityBehaviorExt() {
        super(null, null);
    }

    public ShareniuUserTaskActivityBehaviorExt(String userTaskId,
                                               TaskDefinition taskDefinition) {
        super(userTaskId, taskDefinition);
    }

    public void setUserTaskId(String userTaskId) {
        super.userTaskId = userTaskId;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        super.taskDefinition = taskDefinition;
    }

    public  boolean isSkip(ActivityExecution execution){
        ActivityImpl activity= ProcessDefinitionUtils.getActivity(execution.getEngineServices().
                getRepositoryService(),execution.getProcessDefinitionId(),taskDefinition.getKey());
        ActivityBehavior activityBehavior= activity.getActivityBehavior();
        if (activityBehavior instanceof  UserTaskActivityBehavior){
        return  true;
        }else{
            return  false;
        }
    }
    @Override
    public void execute(ActivityExecution execution) throws Exception {
        //sid-AF89D436-07F2-4D2A-9498-E6CA711DECBA
        //Activity(sid-AF89D436-07F2-4D2A-9498-E6CA711DECBA)
        TaskEntity task = TaskEntity.createAndInsert(execution);
        task.setExecution(execution);
        ExecutionEntity newExecution = (ExecutionEntity) execution;
        if (newExecution != null && newExecution.getTenantId() != null) {
            String processDefinitionId = execution.getProcessDefinitionId();
            RepositoryService repositoryService = execution.getEngineServices().getRepositoryService();
            String category = repositoryService.getProcessDefinition(processDefinitionId).getCategory();
            task.setCategory(category);
        }

        Expression activeNameExpression = null;
        Expression activeDescriptionExpression = null;
        Expression activeDueDateExpression = null;
        Expression activePriorityExpression = null;
        Expression activeCategoryExpression = null;
        Expression activeFormKeyExpression = null;
        Expression activeSkipExpression = null;
        Expression activeAssigneeExpression = null;
        Expression activeOwnerExpression = null;
        Set<Expression> activeCandidateUserExpressions = null;
        Set<Expression> activeCandidateGroupExpressions = null;

        if (Context.getProcessEngineConfiguration()
                .isEnableProcessDefinitionInfoCache()) {
            ObjectNode taskElementProperties = Context
                    .getBpmnOverrideElementProperties(userTaskId,
                            execution.getProcessDefinitionId());
            activeNameExpression = getActiveValue(
                    taskDefinition.getNameExpression(),
                    DynamicBpmnConstants.USER_TASK_NAME, taskElementProperties);
            taskDefinition.setNameExpression(activeNameExpression);
            activeDescriptionExpression = getActiveValue(
                    taskDefinition.getDescriptionExpression(),
                    DynamicBpmnConstants.USER_TASK_DESCRIPTION,
                    taskElementProperties);
            taskDefinition
                    .setDescriptionExpression(activeDescriptionExpression);
            activeDueDateExpression = getActiveValue(
                    taskDefinition.getDueDateExpression(),
                    DynamicBpmnConstants.USER_TASK_DUEDATE,
                    taskElementProperties);
            taskDefinition.setDueDateExpression(activeDueDateExpression);
            activePriorityExpression = getActiveValue(
                    taskDefinition.getPriorityExpression(),
                    DynamicBpmnConstants.USER_TASK_PRIORITY,
                    taskElementProperties);
            taskDefinition.setPriorityExpression(activePriorityExpression);
            activeCategoryExpression = getActiveValue(
                    taskDefinition.getCategoryExpression(),
                    DynamicBpmnConstants.USER_TASK_CATEGORY,
                    taskElementProperties);
            taskDefinition.setCategoryExpression(activeCategoryExpression);
            activeFormKeyExpression = getActiveValue(
                    taskDefinition.getFormKeyExpression(),
                    DynamicBpmnConstants.USER_TASK_FORM_KEY,
                    taskElementProperties);
            taskDefinition.setFormKeyExpression(activeFormKeyExpression);
            activeSkipExpression = getActiveValue(
                    taskDefinition.getSkipExpression(),
                    DynamicBpmnConstants.TASK_SKIP_EXPRESSION,
                    taskElementProperties);
            taskDefinition.setSkipExpression(activeSkipExpression);
            activeAssigneeExpression = getActiveValue(
                    taskDefinition.getAssigneeExpression(),
                    DynamicBpmnConstants.USER_TASK_ASSIGNEE,
                    taskElementProperties);
            taskDefinition.setAssigneeExpression(activeAssigneeExpression);
            activeOwnerExpression = getActiveValue(
                    taskDefinition.getOwnerExpression(),
                    DynamicBpmnConstants.USER_TASK_OWNER, taskElementProperties);
            taskDefinition.setOwnerExpression(activeOwnerExpression);
            activeCandidateUserExpressions = getActiveValueSet(
                    taskDefinition.getCandidateUserIdExpressions(),
                    DynamicBpmnConstants.USER_TASK_CANDIDATE_USERS,
                    taskElementProperties);
            taskDefinition
                    .setCandidateUserIdExpressions(activeCandidateUserExpressions);
            activeCandidateGroupExpressions = getActiveValueSet(
                    taskDefinition.getCandidateGroupIdExpressions(),
                    DynamicBpmnConstants.USER_TASK_CANDIDATE_GROUPS,
                    taskElementProperties);
            taskDefinition
                    .setCandidateGroupIdExpressions(activeCandidateGroupExpressions);

        } else {
            activeNameExpression = taskDefinition.getNameExpression();
            activeDescriptionExpression = taskDefinition
                    .getDescriptionExpression();
            activeDueDateExpression = taskDefinition.getDueDateExpression();
            activePriorityExpression = taskDefinition.getPriorityExpression();
            activeCategoryExpression = taskDefinition.getCategoryExpression();
            activeFormKeyExpression = taskDefinition.getFormKeyExpression();
            activeSkipExpression = taskDefinition.getSkipExpression();
            activeAssigneeExpression = taskDefinition.getAssigneeExpression();
            activeOwnerExpression = taskDefinition.getOwnerExpression();
            activeCandidateUserExpressions = taskDefinition
                    .getCandidateUserIdExpressions();
            activeCandidateGroupExpressions = taskDefinition
                    .getCandidateGroupIdExpressions();
        }

        task.setTaskDefinition(taskDefinition);

        if (activeNameExpression != null) {
            String name = null;
            try {
                name = (String) activeNameExpression.getValue(execution);
            } catch (ActivitiException e) {
                name = activeNameExpression.getExpressionText();
                LOGGER.warn("property not found in task name expression "
                        + e.getMessage());
            }
            task.setName(name);
        }

        if (activeDescriptionExpression != null) {
            String description = null;
            try {
                description = (String) activeDescriptionExpression
                        .getValue(execution);
            } catch (ActivitiException e) {
                description = activeDescriptionExpression.getExpressionText();
                LOGGER.warn("property not found in task description expression "
                        + e.getMessage());
            }
            task.setDescription(description);
        }

        if (activeDueDateExpression != null) {
            Object dueDate = activeDueDateExpression.getValue(execution);
            if (dueDate != null) {
                if (dueDate instanceof Date) {
                    task.setDueDate((Date) dueDate);
                } else if (dueDate instanceof String) {
                    BusinessCalendar businessCalendar = Context
                            .getProcessEngineConfiguration()
                            .getBusinessCalendarManager()
                            .getBusinessCalendar(
                                    taskDefinition
                                            .getBusinessCalendarNameExpression()
                                            .getValue(execution).toString());
                    task.setDueDate(businessCalendar
                            .resolveDuedate((String) dueDate));
                } else {
                    throw new ActivitiIllegalArgumentException(
                            "Due date expression does not resolve to a Date or Date string: "
                                    + activeDueDateExpression
                                    .getExpressionText());
                }
            }
        }

        if (activePriorityExpression != null) {
            final Object priority = activePriorityExpression
                    .getValue(execution);
            if (priority != null) {
                if (priority instanceof String) {
                    try {
                        task.setPriority(Integer.valueOf((String) priority));
                    } catch (NumberFormatException e) {
                        throw new ActivitiIllegalArgumentException(
                                "Priority does not resolve to a number: "
                                        + priority, e);
                    }
                } else if (priority instanceof Number) {
                    task.setPriority(((Number) priority).intValue());
                } else {
                    throw new ActivitiIllegalArgumentException(
                            "Priority expression does not resolve to a number: "
                                    + activePriorityExpression
                                    .getExpressionText());
                }
            }
        }

        if (activeCategoryExpression != null) {
            final Object category = activeCategoryExpression
                    .getValue(execution);
            if (category != null) {
                if (category instanceof String) {
                    task.setCategory((String) category);
                } else {
                    throw new ActivitiIllegalArgumentException(
                            "Category expression does not resolve to a string: "
                                    + activeCategoryExpression
                                    .getExpressionText());
                }
            }
        }

        if (activeFormKeyExpression != null) {
            final Object formKey = activeFormKeyExpression.getValue(execution);
            if (formKey != null) {
                if (formKey instanceof String) {
                    task.setFormKey((String) formKey);
                } else {
                    throw new ActivitiIllegalArgumentException(
                            "FormKey expression does not resolve to a string: "
                                    + activeFormKeyExpression
                                    .getExpressionText());
                }
            }
        }

        boolean skipUserTask = SkipExpressionUtil.isSkipExpressionEnabled(
                execution, activeSkipExpression)
                && SkipExpressionUtil.shouldSkipFlowElement(execution,
                activeSkipExpression);
        ProcessPriorityDao processPriorityDao = applicationContext.getBean(ProcessPriorityDao.class);
        if (processPriorityDao!=null) {
        	 ProcessPriority processPriority = processPriorityDao.selectByProcInstId(task.getProcessInstanceId());
        	 if (processPriority!=null) {
        		 task.setPriority(processPriority.getPriority());
        		 task.setDueDate(DateUtils.calTime(new Date(),String.valueOf(processPriority.getHandTime())));
			}
		}
        if (!skipUserTask) {
           Object object = execution.getVariable(StartActivityCmd.NODE_IS_PICK);
            if(object == null){
                handleAssignments(activeAssigneeExpression, activeOwnerExpression,
                        activeCandidateUserExpressions,
                        activeCandidateGroupExpressions, task, execution);
            }else {
                //回退过来的节点
                PvmActivity activity = execution.getActivity();
                Object property = activity.getProperty("shareniuExt");// 获取shareniuExt属性值
                Map<String, List<ExtensionAttribute>> extensionAttribute = null;
                if (property != null) {
                    extensionAttribute = (Map<String, List<ExtensionAttribute>>) property;
                }
                task.setAssignee(String.valueOf(execution.getVariable(StartActivityCmd.NODE_HANDLE)));
                //获取时间
                String time = this.getValue(extensionAttribute, HmXMLConstants.DUE_DATE_SET);
                if (!StringUtils.isEmpty(time)) {
                    //更新期限时间
                    task.setDueDate(DateUtils.calTime(task.getCreateTime(), time));
                }
            }
        }

        task.fireEvent(TaskListener.EVENTNAME_CREATE);

        // All properties set, now firing 'create' events
        if (Context.getProcessEngineConfiguration().getEventDispatcher()
                .isEnabled()) {
            Context.getProcessEngineConfiguration()
                    .getEventDispatcher()
                    .dispatchEvent(
                            ActivitiEventBuilder.createEntityEvent(
                                    ActivitiEventType.TASK_CREATED, task));
        }

        if (skipUserTask) {
        	changeAutoInfo(task);
            task.complete(null, false);
        }

        Object object = execution.getVariable(StartActivityCmd.NODE_IS_PICK);
        //如果不是erp的才会执行
        if(object ==null && ProcessTypeConstants.ehr.equals(execution.getTenantId())) {
            //获取变量
            Object variable = execution
                    .getVariable(InnerActivitiVarConstants.HM_ACITVITI_PRE_HANDLER_ID);
            if (variable != null) {
                String prefix = (String) variable;
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("ShareniuUserTaskActivityBehaviorExt.execute=======begin=================" + ";" + prefix);
                }
                //如果任务节点
                if (task.getAssignee() != null && !task.getAssignee().equals("")
                        && task.getAssignee().equals(prefix) && !"1".equals(task.getFormKey())&&isSkip(execution)) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("ShareniuUserTaskActivityBehaviorExt.execute========================" + ",task.getAssignee() is not null");
                    }
                    //这里下一个人为执行
                    removeCandidate(task);
                    changeAutoInfo(task);
                    task.complete(null, false);
                    HandlerLog handlerLog = new HandlerLog();
                    handlerLog.setHandleTime(DateUtils.now());
                    handlerLog.setType(NodeHandleType.NODE_REPEART);
                    handlerLog.setHandleUser(prefix);
                    NodeHandlerUtils.taskNodeHandleLog(Context.getProcessEngineConfiguration().getRuntimeService(), task.getId(), handlerLog);
                }
                if ((task.getAssignee() == null || task.getAssignee().equals(""))&&isSkip(execution)) {
                    Set<IdentityLink> candidates = task.getCandidates();
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("ShareniuUserTaskActivityBehaviorExt.execute========================" + ",task.getAssignee() is  null,but candidates is not null is" + candidates);
                    }
                    if((candidates==null || candidates.size()==0)&&(ObjectCheckUtils.isEmptyString(task.getAssignee())&&!"1".equals(task.getFormKey()))){
                        //TODO
                    	 removeCandidate(task);
                         changeAutoInfo(task);
                        task.complete(null, false);
                        HandlerLog handlerLog = new HandlerLog();
                        handlerLog.setHandleTime(DateUtils.now());
                        handlerLog.setType(NodeHandleType.NO_HANDLE_SKILLP);
                        handlerLog.setHandleUser(prefix);
                        NodeHandlerUtils.taskNodeHandleLog(Context.getProcessEngineConfiguration().getRuntimeService(), task.getId(), handlerLog);
                    }
                    if (candidates.size() == 1) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("ShareniuUserTaskActivityBehaviorExt.execute========================" + ",candidates.size() == 1");
                        }
                        Iterator<IdentityLink> iterator = candidates.iterator();
                        while (iterator.hasNext()) {
                            IdentityLink identityLink = (IdentityLink) iterator
                                    .next();
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info("ShareniuUserTaskActivityBehaviorExt.execute========================" + ",identityLink=" + identityLink);
                            }
                            if (identityLink.getUserId().equals(prefix)) {
                                task.setAssignee(identityLink.getUserId());
                                removeCandidate(task);
                                changeAutoInfo(task);
                                task.complete(null, false);
                                HandlerLog handlerLog = new HandlerLog();
                                handlerLog.setHandleTime(DateUtils.now());
                                handlerLog.setType(NodeHandleType.NODE_REPEART);
                                handlerLog.setHandleUser(prefix);
                                NodeHandlerUtils.taskNodeHandleLog(Context.getProcessEngineConfiguration().getRuntimeService(), task.getId(), handlerLog);
                            }

                        }
                    }
                }
            }
        } else {
            execution.removeVariable(StartActivityCmd.NODE_IS_PICK);
            execution.removeVariable(StartActivityCmd.NODE_HANDLE);
        }

    }

    private void changeAutoInfo(TaskEntity task) {
    	task.setVariable("auto", "auto");
	}

	/**
     * 删除候选人自动通过
     * @param task
     */
    private void removeCandidate(TaskEntity task){
    	task.setVariable("auto", "auto");
            List<IdentityLinkEntity> identityLinksFromCache = Context.getCommandContext().getDbSqlSession()
                    .findInCache(IdentityLinkEntity.class);
            for (IdentityLinkEntity identityLinkEntity : identityLinksFromCache) {
                if (task.getId().equals(identityLinkEntity.getTaskId())) {
                    Context.getCommandContext().getDbSqlSession().delete(identityLinkEntity);
                }
            }
        }


    @Override
    protected void handleAssignments(Expression assigneeExpression,
                                     Expression ownerExpression,
                                     Set<Expression> candidateUserExpressions,
                                     Set<Expression> candidateGroupExpressions, TaskEntity task,
                                     ActivityExecution execution) {
        super.handleAssignments(assigneeExpression, ownerExpression,
                candidateUserExpressions, candidateGroupExpressions, task,
                execution);
        PvmActivity activity = execution.getActivity();
        Object property = activity.getProperty("shareniuExt");// 获取shareniuExt属性值
        Map<String, List<ExtensionAttribute>> extensionAttribute = null;
        if (property != null) {
            extensionAttribute = (Map<String, List<ExtensionAttribute>>) property;
        }
        if (extensionAttribute != null) {
            Object object = execution.getVariable(StartActivityCmd.NODE_IS_PICK);

            if(object ==null){
                Map<String,UserRule> ruleMap =RuleManager.produceUserRule();
                List<String> userIds = new ArrayList<>();
                Set<String> keys =ruleMap.keySet();
                //ehr运行所有的
                if(ProcessTypeConstants.ehr.equals(execution.getTenantId())){
                    for(String key:keys){
                        userIds.addAll(ruleMap.get(key).getUsersId(activity,execution));
                    }
                }
                //如果crm 岗位
                if(ProcessTypeConstants.crm.equals(execution.getTenantId())){
                    for(String key:keys){
                        userIds.addAll(ruleMap.get(key).getUsersId(activity,execution));
                    }
                }
                if(ProcessTypeConstants.erp.equals(execution.getTenantId())){
                    userIds.addAll(ruleMap.get(HmXMLConstants.PROPERTY_USERTASK_ASSIGN).getUsersId(activity,execution));
                }
                //添加审核人
                if(userIds!=null && userIds.size()>=1){
                    task.addCandidateUsers(userIds);
                }
            }else {
                //回退来的节点
                task.setAssignee(String.valueOf(execution.getVariable(StartActivityCmd.NODE_HANDLE)));
            }

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
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
}
