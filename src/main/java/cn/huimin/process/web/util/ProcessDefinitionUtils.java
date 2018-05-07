package cn.huimin.process.web.util;

import org.activiti.engine.EngineServices;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.VariableInstanceEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * 流程定义相关操作的封装
 *
 *
 */
public abstract class ProcessDefinitionUtils {
	public static ActivityImpl getActivity(ProcessEngine processEngine,
			String processDefId, String activityId) {
		return getActivity(processEngine.getRepositoryService(),
				processDefId,activityId);
	}
	public static ActivityImpl getActivity(RepositoryService repositoryService,
			String processDefId, String activityId) {
		ProcessDefinitionEntity pde = getProcessDefinition(repositoryService,
				processDefId);
		return (ActivityImpl) pde.findActivity(activityId);
	}

	/**
	 * 根据当前的节点信息获取上一个节点的信息
	 *
	 * @param processDefId
	 * @param activityId
	 * @return
	 */
	public static List<ActivityImpl> getPreActivity(
			RepositoryService repositoryService, String processDefId, String activityId) {
		List<ActivityImpl> result = new ArrayList<ActivityImpl>();
		ActivityImpl currentActivity = getActivity(repositoryService, processDefId,
				activityId);
		List<PvmTransition> incomingTransitions = currentActivity
				.getIncomingTransitions();
		for (PvmTransition pvmTransition : incomingTransitions) {
			PvmActivity source = pvmTransition.getSource();
			result.add((ActivityImpl) source);
		}
		return result;
	}

	/**
	 * 获取历史变量
	 * @param engineServices
	 * @param processDefId
	 * @param activityId
	 * @param businessKey
     * @return
     */
	public static String getPreActivityAssignee(EngineServices engineServices,
			String processDefId, String activityId, String businessKey) {
		String assignee = "";
		org.activiti.engine.RepositoryService repositoryService = engineServices.getRepositoryService();
		List<ActivityImpl> currentActivity = ProcessDefinitionUtils
				.getPreActivity(repositoryService, processDefId, activityId);
		HistoryService historyService = engineServices.getHistoryService();
		if (currentActivity == null) {
			// TODO 报错 没找到
		} else if (currentActivity.size() == 1) {
			ActivityImpl activityImpl = currentActivity.get(0);
			if (activityImpl.getActivityBehavior() instanceof NoneStartEventActivityBehavior) {
				// 开始节点
			   
				HistoricProcessInstanceQuery hpi = historyService
						.createHistoricProcessInstanceQuery()
						.processInstanceBusinessKey(businessKey)
						.processDefinitionId(processDefId);
				HistoricProcessInstance singleResult = hpi.singleResult();
				if (singleResult != null) {
					String startUserId = singleResult.getStartUserId();
					return startUserId;
				}else {
					 String authenticatedUserId = Authentication.getAuthenticatedUserId();
					  return authenticatedUserId;
				}

			} else if (activityImpl.getActivityBehavior() instanceof UserTaskActivityBehavior) {
					String taskKey = activityImpl.getId();
					List<HistoricTaskInstance> list = historyService
							.createHistoricTaskInstanceQuery()
							.processDefinitionId(processDefId)
							.processInstanceBusinessKey(businessKey)
							.taskDefinitionKey(taskKey).list();
					if (list != null) {
						if (list.size() > 0) {
							assignee = list.get(0).getAssignee();
						}
				}
			} else {
				// TODO 存在分支
			}
		}

		return assignee;

	}

	/**
	 * 获取运行单一字段
	 * @param engineServices
	 * @param executionId //运行id
	 * @param innerActivitVarName//内置的流程名称
     * @return
     */
	public static Object getRuntimeInnerVar(EngineServices engineServices, final String executionId, final String innerActivitVarName){
		ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) engineServices.getProcessEngineConfiguration();
		String dbVarVal = processEngineConfiguration.getCommandExecutor().execute(new CommandConfig().transactionRequiresNew(),new Command<String>() {
			@Override
			public String execute(CommandContext commandContext) {
				VariableInstanceEntity variableInstanceByExecutionAndName = commandContext.getVariableInstanceEntityManager().findVariableInstanceByExecutionAndName(executionId, innerActivitVarName);
				if(variableInstanceByExecutionAndName!=null){

					String textValue = variableInstanceByExecutionAndName.getTextValue();
					return textValue;
				}
				return null;
			}
		});
		if(dbVarVal==null){
			return engineServices.getRuntimeService().getVariable(executionId, innerActivitVarName);

		}
		return  dbVarVal;
		//return engineServices.getRuntimeService().getVariable(executionId, innerActivitVarName);

	}

	/**
	 * 获取发起人的id
	 * @param engineServices
	 * @param processDefId
	 * @param activityId
	 * @param businessKey
     * @return
     */
	public static String getStartUserId(EngineServices engineServices,
												String processDefId, String activityId, String businessKey) {
		HistoricProcessInstanceQuery hpi = engineServices.getHistoryService()
				.createHistoricProcessInstanceQuery()
				.processInstanceBusinessKey(businessKey)
				.processDefinitionId(processDefId);
		HistoricProcessInstance singleResult = hpi.singleResult();
		return singleResult.getStartUserId();

	}



	public static ProcessDefinitionEntity getProcessDefinition(
			RepositoryService repositoryService, String processDefId) {
		return (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processDefId);
	}
	public static ProcessDefinitionEntity getProcessDefinition(
			ProcessEngine processEngine, String processDefId) {
		return (ProcessDefinitionEntity) ((RepositoryServiceImpl) processEngine
				.getRepositoryService())
				.getDeployedProcessDefinition(processDefId);
	}

	/**
	 * 根据流程key获取最新部署的流程定义
	 * @param repositoryService
     * @return
     */
	public static ProcessDefinitionEntity getProcessDefinitionByKey(
			RepositoryService repositoryService, String key) {
		return (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
	}
}
