package cn.huimin.process.createactivity;

import java.util.List;

import cn.huimin.process.web.util.BpmnConstants;
import cn.huimin.process.web.util.CloneUtils;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.bpmn.behavior.ParallelGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.springframework.beans.BeanUtils;

public abstract class RuntimeActivityCreatorSupport
{
    private static int SEQUNCE_NUMBER = 0;

    protected ActivityImpl cloneActivity(ProcessDefinitionEntity processDefinition, ActivityImpl prototypeActivity,
                                         String newActivityId, String... fieldNames)
    {
        ActivityImpl clone = processDefinition.createActivity(newActivityId);

        CloneUtils.copyFields(prototypeActivity, clone, fieldNames);
        return clone;
    }

    protected TaskDefinition cloneTaskDefinition(TaskDefinition taskDefinition)
    {
        TaskDefinition newTaskDefinition = new TaskDefinition(taskDefinition.getTaskFormHandler());
        BeanUtils.copyProperties(taskDefinition, newTaskDefinition);
        return newTaskDefinition;
    }

    protected ActivityImpl createActivity(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
                                          ActivityImpl prototypeActivity, String cloneActivityId, String assignee,String activityName,Integer newX)
    {
        ActivityImpl c = cloneActivity(processDefinition, prototypeActivity, cloneActivityId, "executionListeners",
                "properties","x","y","width","height");
        //设置assignee
        //clone.setProperty("name",activityName);
        c.setProperty("create", "true");
        c.setX(c.getX()+newX);
        UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior) (prototypeActivity.getActivityBehavior());
        TaskDefinition taskDefinition = cloneTaskDefinition(activityBehavior.getTaskDefinition());
        taskDefinition.setKey(cloneActivityId);
        taskDefinition.setAssigneeExpression(null);
        taskDefinition.setCandidateGroupIdExpressions(null);
        taskDefinition.setCandidateUserIdExpressions(null);
        if (assignee != null)
        {
            taskDefinition.setAssigneeExpression(new FixedValue(assignee));
        }
        if(activityName !=null){
            taskDefinition.setNameExpression(new FixedValue(activityName));
        }
        UserTaskActivityBehavior cloneActivityBehavior = new UserTaskActivityBehavior(cloneActivityId,taskDefinition);
        c.setActivityBehavior(cloneActivityBehavior);

        return c;
    }


    /**
     * 创建网关
     * @param processDefinition
     * @param prototypeActivity
     * @param cloneActivityId
     * @return
     */
    protected ActivityImpl createParalActivity( ProcessDefinitionEntity processDefinition,
                                          ActivityImpl prototypeActivity, String cloneActivityId)
    {
        //复制
        ActivityImpl clone = cloneActivity(processDefinition, prototypeActivity, cloneActivityId, "executionListeners");

        clone.setProperty("type", BpmnConstants.parallelGateway);
        clone.setExclusive(true);
        ParallelGatewayActivityBehavior parallelGatewayActivityBehavior = new ParallelGatewayActivityBehavior();
        clone.setActivityBehavior(parallelGatewayActivityBehavior);

        return clone;
    }



    protected ActivityImpl createActivity(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
                                          String prototypeActivityId, String cloneActivityId, String assignee,String activityName)
    {
        ActivityImpl prototypeActivity = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
                prototypeActivityId);
        return createActivity(processEngine, processDefinition, prototypeActivity, cloneActivityId, assignee,activityName,null);
    }

    protected void createActivityChain(List<ActivityImpl> activities, ActivityImpl nextActivity)
    {
        for (int i = 0; i < activities.size(); i++)
        {
            //设置各活动的下线
            activities.get(i).getOutgoingTransitions().clear();
            //设置出口的方法
            activities.get(i).createOutgoingTransition("flow" + (i + 1))
                    .setDestination(i == activities.size() - 1 ? nextActivity : activities.get(i + 1));
        }
    }

    /**
     * 创建并行活动
     * @param activities
     * @param activitie2
     * @param nextActivity
     */
    protected void createActivityParal(List<ActivityImpl> activities,List<ActivityImpl> activitie2, ActivityImpl nextActivity)
    {
        activitie2.get(0).getOutgoingTransitions().clear();
        for (int i = 0; i < activities.size(); i++)
        {

            //设置各活动的下线
            activities.get(i).getOutgoingTransitions().clear();
            //设置出口的方法
            if(i==0){
                activities.get(i).createOutgoingTransition("flow"+100).setDestination(activitie2.get(0));
            }else {
                //所有的都加上出口
                activitie2.get(0).createOutgoingTransition().setDestination(activities.get(i));
                activities.get(i).createOutgoingTransition("flow"+1000+i).setDestination(activitie2.get(1));
            }

        }
        activitie2.get(1).getOutgoingTransitions().clear();
        activitie2.get(1).createOutgoingTransition("flow"+10000).setDestination(nextActivity);
    }


    protected String createUniqueActivityId(String processInstanceId, String prototypeActivityId)
    {
        return processInstanceId + ":" + prototypeActivityId + ":" + System.currentTimeMillis() + "-"
                + (SEQUNCE_NUMBER++);
    }
}
