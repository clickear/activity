package cn.huimin.process.createactivity;

import cn.huimin.process.core.pvm.ActivityImplUtils;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import cn.huimin.process.web.model.RuntimeActivityCreateInterface;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建串行节点
 */
public class ChainedActivitiesCreator extends RuntimeActivityCreatorSupport implements RuntimeActivityCreator
{
	public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
										   RuntimeActivityCreateInterface info)
	{
		info.setFactoryName(ChainedActivitiesCreator.class.getName());
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
		for (int i = 0; i < assignees.size(); i++)
		{
			if (activityIds.get(i) == null)
			{
				String activityId = createUniqueActivityId(processInstanceId, prototypeActivityId);
				activityIds.set(i, activityId);
			}

			ActivityImpl clone = createActivity(processEngine, processDefinition, prototypeActivity,
				activityIds.get(i), assignees.get(i),activityNames.get(i),(i+1)*50);
			activities.add(clone);
		}

		ActivityImpl nextActivity = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
			nextActivityId);
		
		createActivityChain(activities, nextActivity);
		ActivityImpl addLast = activities.get(activities.size()-1);
		List<String> lists = ActivityImplUtils.getNextActivityImpl(activities.get(activities.size()-1));
		for (int i = 0; i < lists.size(); i++) {
			ActivityImpl activity = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
					lists.get(i));
			activity.setX(addLast.getX()+(i+1)*200);
		}
		
		return activities.toArray(new ActivityImpl[0]);
	}
}
