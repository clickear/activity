
package cn.huimin.process.createactivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.huimin.process.activiticmd.CreateAndTakeTransitionCmd;
import cn.huimin.process.activiticmd.DeleteRunningTaskCmd;
import cn.huimin.process.activiticmd.StartActivityCmd;
import cn.huimin.process.web.service.ActCreationService;
import cn.huimin.process.web.model.ActCreation;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import org.springframework.util.CollectionUtils;

/**
 * 任务流创建
 */
public class DefaultTaskFlowControlService implements TaskFlowControlService
{
    ActCreationService _activitiesCreationStore;

    ProcessDefinitionEntity _processDefinition;

    ProcessEngine _processEngine;

    private String _processInstanceId;

    public DefaultTaskFlowControlService(ActCreationService activitiesCreationStore,
                                         ProcessEngine processEngine, String processId)
    {
        _activitiesCreationStore = activitiesCreationStore;
        _processEngine = processEngine;
        _processInstanceId = processId;

        String processDefId = _processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(_processInstanceId).singleResult().getProcessDefinitionId();

        _processDefinition = ProcessDefinitionUtils.getProcessDefinition(_processEngine, processDefId);
    }

    private ActivityImpl[] cloneAndMakeChain(String prototypeActivityId, String nextActivityId,String taskId,String doUserId,String[] names, String... assignees)
            throws Exception
    {
        ActCreation info = new ActCreation();
        info.setProcessDefinitionId(_processDefinition.getId());
        info.setProcessInstanceId(_processInstanceId);
        info.setDoUserId(doUserId);
        info.setActId(prototypeActivityId);
        RuntimeActivityDefinitionEntityIntepreter radei = new RuntimeActivityDefinitionEntityIntepreter(info);
        radei.setPrototypeActivityId(prototypeActivityId);
        //添加所有的人
        radei.setAssignees(CollectionUtils.arrayToList(assignees));
        radei.setNextActivityId(nextActivityId);
        radei.setActivityNames(CollectionUtils.arrayToList(names));
        ActivityImpl[] activities = new ChainedActivitiesCreator().createActivities(_processEngine, _processDefinition,
                info);

        moveTo(taskId,activities[0].getId());
        recordActivitiesCreation(info);
        return activities;
    }

    /**
     * 克隆创建并行网关
     * @param prototypeActivityId
     * @param nextActivityId
     * @param doUserId
     * @param names
     * @param assignees
     * @return
     * @throws Exception
     */
    private ActivityImpl[] cloneAndMakePara(String prototypeActivityId, String nextActivityId,String taskId,String doUserId,String[] names, String... assignees)
            throws Exception
    {
        ActCreation info = new ActCreation();
        info.setProcessDefinitionId(_processDefinition.getId());
        info.setProcessInstanceId(_processInstanceId);
        info.setDoUserId(doUserId);
        info.setActId(prototypeActivityId);
        RuntimeActivityDefinitionEntityIntepreter radei = new RuntimeActivityDefinitionEntityIntepreter(info);
        radei.setPrototypeActivityId(prototypeActivityId);
        //添加所有的人
        radei.setAssignees(CollectionUtils.arrayToList(assignees));
        radei.setNextActivityId(nextActivityId);
        radei.setActivityNames(CollectionUtils.arrayToList(names));
        ActivityImpl[] activities = new ParallelActivitiesCreator().createActivities(_processEngine, _processDefinition,
                info);
        moveTo(taskId,activities[0].getId());
        recordActivitiesCreation(info);
        return activities;
    }



    private void executeCommand(Command<java.lang.Void> command)
    {
        ((RuntimeServiceImpl) _processEngine.getRuntimeService()).getCommandExecutor().execute(command);
    }

    private TaskEntity getCurrentTask()
    {
        return (TaskEntity) _processEngine.getTaskService().createTaskQuery().processInstanceId(_processInstanceId)
              .singleResult();
    }

    private TaskEntity getTaskById(String taskId)
    {
        return (TaskEntity) _processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
    }

    /**
     * 后加签
     */
    @Override
    public ActivityImpl[] insertTasksAfter(String targetTaskDefinitionKey,String taskId,String doUserId,String[] names, String... assignees) throws Exception
    {
        List<String> assigneeList = new ArrayList<String>();
        //assigneeList.add(Authentication.getAuthenticatedUserId());
        assigneeList.addAll(CollectionUtils.arrayToList(assignees));
        String[] newAssignees = assigneeList.toArray(new String[0]);
        ActivityImpl prototypeActivity = ProcessDefinitionUtils.getActivity(_processEngine, _processDefinition.getId(),
                targetTaskDefinitionKey);

        return cloneAndMakeChain(targetTaskDefinitionKey, prototypeActivity.getOutgoingTransitions().get(0)
                .getDestination().getId(),taskId,doUserId, names,newAssignees);
    }


    /**
     * 后加签并行签
     */
    public ActivityImpl[] insertTasksAfterPara(String targetTaskDefinitionKey,String taskId,String doUserId,String[] names, String... assignees) throws Exception
    {
        List<String> assigneeList = new ArrayList<String>();
        //assigneeList.add(Authentication.getAuthenticatedUserId());
        assigneeList.addAll(CollectionUtils.arrayToList(assignees));
        String[] newAssignees = assigneeList.toArray(new String[0]);
        ActivityImpl prototypeActivity = ProcessDefinitionUtils.getActivity(_processEngine, _processDefinition.getId(),
                targetTaskDefinitionKey);
        return cloneAndMakePara(targetTaskDefinitionKey, prototypeActivity.getOutgoingTransitions().get(0)
                .getDestination().getId(),taskId,doUserId, names,newAssignees);
    }



    /**
     * 前加签
     */
    @Override
    public ActivityImpl[] insertTasksBefore(String targetTaskDefinitionKey, String... assignees) throws Exception
    {
        return cloneAndMakeChain(targetTaskDefinitionKey, targetTaskDefinitionKey, "","",assignees);
    }

    @Override
    public void moveBack() throws Exception
    {
        moveBack(getCurrentTask());
    }

    @Override
    public void moveBack(TaskEntity currentTaskEntity) throws Exception
    {
        ActivityImpl activity = (ActivityImpl) ProcessDefinitionUtils
                .getActivity(_processEngine, currentTaskEntity.getProcessDefinitionId(),
                        currentTaskEntity.getTaskDefinitionKey()).getIncomingTransitions().get(0).getSource();

        moveTo(currentTaskEntity, activity);
    }



    @Override
    public void moveForward() throws Exception
    {
        moveForward(getCurrentTask());
    }

    @Override
    public void moveForward(TaskEntity currentTaskEntity) throws Exception
    {
        ActivityImpl activity = (ActivityImpl) ProcessDefinitionUtils
                .getActivity(_processEngine, currentTaskEntity.getProcessDefinitionId(),
                        currentTaskEntity.getTaskDefinitionKey()).getOutgoingTransitions().get(0).getDestination();

        moveTo(currentTaskEntity, activity);
    }

    /**
     * 跳转（包括回退和向前）至指定活动节点
     *
     * @param targetTaskDefinitionKey
     * @throws Exception
     */
    @Override
    public void moveTo(String targetTaskDefinitionKey) throws Exception
    {
        moveTo(getCurrentTask(), targetTaskDefinitionKey);
    }

    @Override
    public void moveTo(String currentTaskId, String targetTaskDefinitionKey) throws Exception
    {
        moveTo(getTaskById(currentTaskId), targetTaskDefinitionKey);
    }

    private void moveTo(TaskEntity currentTaskEntity, ActivityImpl activity)
    {
        executeCommand(new StartActivityCmd(currentTaskEntity.getExecutionId(), activity));
        executeCommand(new DeleteRunningTaskCmd(currentTaskEntity));
    }

    /**
     *
     * @param currentTaskEntity
     *            当前任务节点
     * @param targetTaskDefinitionKey
     *            目标任务节点（在模型定义里面的节点名称）
     * @throws Exception
     */
    @Override
    public void moveTo(TaskEntity currentTaskEntity, String targetTaskDefinitionKey) throws Exception
    {
        ActivityImpl activity = ProcessDefinitionUtils.getActivity(_processEngine,
                currentTaskEntity.getProcessDefinitionId(), targetTaskDefinitionKey);

        moveTo(currentTaskEntity, activity);
    }

    private void recordActivitiesCreation(ActCreation info) throws Exception
    {
        info.serializeProperties();
        _activitiesCreationStore.save(info);
    }

    /**
     * 分裂某节点为多实例节点
     *
     * @param targetTaskDefinitionKey
     * @param
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Override
    public ActivityImpl split(String targetTaskDefinitionKey, boolean isSequential, String doUser,String... assignees)
            throws Exception
    {
        ActCreation info = new ActCreation();
        info.setProcessDefinitionId(_processDefinition.getId());
        info.setProcessInstanceId(_processInstanceId);
        info.setDoUserId(doUser);
        info.setActId(targetTaskDefinitionKey);
        RuntimeActivityDefinitionEntityIntepreter radei = new RuntimeActivityDefinitionEntityIntepreter(info);

        radei.setPrototypeActivityId(targetTaskDefinitionKey);
        radei.setAssignees(CollectionUtils.arrayToList(assignees));
        radei.setSequential(isSequential);

        ActivityImpl clone = new MultiInstanceActivityCreator().createActivities(_processEngine, _processDefinition,
                info)[0];
        TaskEntity currentTaskEntity = getCurrentTask();
        executeCommand(new CreateAndTakeTransitionCmd(currentTaskEntity.getExecutionId(), clone));
        executeCommand(new DeleteRunningTaskCmd(currentTaskEntity));
        recordActivitiesCreation(info);
        return clone;
    }

    @Override
    public ActivityImpl split(String targetTaskDefinitionKey, String doUser, String... assignee) throws Exception
    {
        return split(targetTaskDefinitionKey, true,doUser, assignee);
    }
}
