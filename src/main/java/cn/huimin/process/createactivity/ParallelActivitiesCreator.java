package cn.huimin.process.createactivity;

import cn.huimin.process.web.model.RuntimeActivityCreateInterface;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import cn.huimin.process.web.util.UUIDutils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 并行活动节点
 */
public class ParallelActivitiesCreator extends RuntimeActivityCreatorSupport implements RuntimeActivityCreator
{
	public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
										   RuntimeActivityCreateInterface info)
	{
		info.setFactoryName(ParallelActivitiesCreator.class.getName());
		RuntimeActivityDefinitionEntityIntepreter radei = new RuntimeActivityDefinitionEntityIntepreter(info);

		if (radei.getCloneActivityIds() == null)
		{

			radei.setCloneActivityIds(CollectionUtils.arrayToList(new String[radei.getAssignees().size()]));
		}

		return createActivities(processEngine, processDefinition, info.getProcessInstanceId(),
			radei.getPrototypeActivityId(), radei.getNextActivityId(), radei.getAssignees(),
			radei.getCloneActivityIds(),radei.getActivityNames());
	}

	private ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
											String processInstanceId, String prototypeActivityId, String nextActivityId, List<String> assignees,
											List<String> activityIds,List<String> activityNames)
	{
		ActivityImpl prototypeActivity = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
			prototypeActivityId);

		List<ActivityImpl> activities = new ArrayList<ActivityImpl>();
		//这里代表所有创建的节点
		for (int i = 0; i < assignees.size(); i++) {
			if (activityIds.get(i) == null)
			{
				String activityId = createUniqueActivityId(processInstanceId, prototypeActivityId);
				activityIds.set(i, activityId);
			}
			ActivityImpl clone = createActivity(processEngine, processDefinition, prototypeActivity,
				activityIds.get(i), assignees.get(i),activityNames.get(i),null);
			activities.add(clone);
		}

		//获取下一个节点
		ActivityImpl nextActivity = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
			nextActivityId);
		List<ActivityImpl> list = new ArrayList<>();
		  ActivityImpl paralActivity1 = createParalActivity(processDefinition,
				prototypeActivity, UUIDutils.createUUID());
		ActivityImpl paralActivity2 = createParalActivity(processDefinition,
				prototypeActivity, UUIDutils.createUUID());
		
		list.add(paralActivity1);
		list.add(paralActivity2);
		createActivityParal(activities,list,nextActivity);
		return activities.toArray(new ActivityImpl[0]);
	}

}
