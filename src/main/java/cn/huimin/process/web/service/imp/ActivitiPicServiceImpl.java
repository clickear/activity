package cn.huimin.process.web.service.imp;

import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.image.ImaGegenerateUtils;
import cn.huimin.process.web.dto.HistoricTaskInstanceType;
import cn.huimin.process.web.dto.UserTaskNodeInfo;
import cn.huimin.process.web.model.CheckInfo;
import cn.huimin.process.web.model.HistoricTaskInstanceEx;
import cn.huimin.process.web.service.ActivitiPicService;
import cn.huimin.process.web.service.HistoricTaskInstanceExService;
import cn.huimin.process.web.service.LogService;
import com.alibaba.fastjson.JSONObject;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/19. 活动流程图
 */
@Service
public class ActivitiPicServiceImpl implements ActivitiPicService {

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;


	@Autowired
	private HistoryService historyService;

	@Autowired
	private HistoricTaskInstanceExService historicTaskInstanceExService;

	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private LogService logService;

	@Value("${getRoleByUserId}")
	private String getRoleByUserId;

	/**
	 * 查询各个节点信息 已经发起的或者已经完成的
	 * 
	 * @param processId
	 *            根据流程实例id
	 * @return
	 */
	public List<UserTaskNodeInfo> queryActivitis(String processId) {
		HistoricProcessInstance historicProcessInstance = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(processId).singleResult();
		String processDif = historicProcessInstance.getProcessDefinitionId();
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDif);
		List<ActivityImpl> activities = processDefinition.getActivities();
		List<UserTaskNodeInfo> activityInfos = new ArrayList<>();
		for (ActivityImpl activity : activities) {
			UserTaskNodeInfo userTaskNodeInfo = new UserTaskNodeInfo();
			userTaskNodeInfo.setWidth(activity.getWidth());
			userTaskNodeInfo.setHeight(activity.getHeight());
			userTaskNodeInfo.setX( activity.getX());
			userTaskNodeInfo.setY( activity.getY());
			userTaskNodeInfo.setTaskKey(activity.getId());
			userTaskNodeInfo.setName(String.valueOf(activity.getProperty("name")));
			List<CheckInfo> list1 = new ArrayList<>();
			List<JSONObject> list = logService.queryCheckLogByProcessInstanceId(processId, InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO);
			for (int i = 0; i <list.size() ; i++) {
				JSONObject jsonObject = list.get(i);
				CheckInfo checkInfo = JSONObject.parseObject(jsonObject.toJSONString(), CheckInfo.class);
				if(activity.getId().equals(checkInfo.getTaskKey())){
					list1.add(checkInfo);
				}
			}
			userTaskNodeInfo.setCheckInfoList(list1);
			activityInfos.add(userTaskNodeInfo);
		}
		return activityInfos;
	}



	/**
	 * 未发起的流程图节点信息
	 * 
	 * @param processDefKey
	 *            流程定义key
	 * @return
	 */
	public List<Map<String, Object>> queryActivitisByDefKey(String processDefKey) {
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionKey(processDefKey).latestVersion()
				.singleResult();
		List<ActivityImpl> activities = processDefinition.getActivities();
		List<Map<String, Object>> activityInfos = new ArrayList<>();
		if (activityInfos == null || activityInfos.size() == 0) {
			return null;
		}
		for (ActivityImpl activity : activities) {
			Map<String, Object> activityInfo = new HashMap<String, Object>();
			activityInfo.put("width", activity.getWidth());
			activityInfo.put("height", activity.getHeight());
			activityInfo.put("x", activity.getX());
			activityInfo.put("y", activity.getY());
			activityInfo.put("actId", activity.getId());
			activityInfo.put("name", activity.getProperty("name"));
			activityInfo.put("type", activity.getProperty("type"));
			activityInfos.add(activityInfo);
		}
		return activityInfos;
	}

	/**
	 * 根据流程定义key获取流程图 流程未开始的
	 * @param processDefKey
	 * @return
	 */
	public InputStream getActivitiPicByProcessDefKey(String processDefKey){
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefKey).latestVersion().singleResult();
		//bpmnModel文件
		BpmnModel bpmnModel =   repositoryService.getBpmnModel(processDefinitionEntity.getId());
		List<String>  activeActivityIds = new ArrayList<>(0);
		List<String> highLightedFlows=new ArrayList<>(0);
		ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
		InputStream in = processEngineConfiguration.getProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeActivityIds,
				highLightedFlows,"宋体","宋体","宋体",null, 1.0);
		return in;
	}

	/**
	 * 获取节点信息
	 * @param processId
	 * @return
     */
	public List<UserTaskNodeInfo> getUserTaskNodeInfos(String processId){
		HistoricTaskInstanceEx historicTaskInstanceEx = new HistoricTaskInstanceEx();
		historicTaskInstanceEx.setProcessInstanceId(processId);
		HistoricTaskInstanceType historicTaskInstanceType = historicTaskInstanceExService.queryHistoricTaskByProcessInstanceId(historicTaskInstanceEx);
		List<HistoricTaskInstanceEx> list =  historicTaskInstanceType.getHistoricTaskInstanceEx();
		String processDefId;
		Integer state = historicTaskInstanceType.getType();
		if(list!=null && list.size()>0){
			BpmnModel bpmnModel = new BpmnModel();
			Process process = new Process();
			bpmnModel.addProcess(process);
			return ImaGegenerateUtils.gegenerateCore(this.handleData(list),bpmnModel,state);
		}
		return null;

	}

	/**
	 * 创建自定义流程图
	 * @param processId
	 * @return
     */
	public InputStream createCustomeActivitiPicByProcessId(String processId){
		HistoricTaskInstanceEx historicTaskInstanceEx = new HistoricTaskInstanceEx();
		historicTaskInstanceEx.setProcessInstanceId(processId);
		HistoricTaskInstanceType historicTaskInstanceType = historicTaskInstanceExService.queryHistoricTaskByProcessInstanceId(historicTaskInstanceEx);
		List<HistoricTaskInstanceEx> list =  historicTaskInstanceType.getHistoricTaskInstanceEx();
		String currentId =historicTaskInstanceType.getCurrentId();
		String processDefId;
		if(list!=null && list.size()>0){
			 processDefId =list.get(0).getProcessDefinitionId();
			 return ImaGegenerateUtils.gegenerate(this.handleData(list),currentId, historicTaskInstanceType.getHignNodeIds(),null,processEngine,processDefId,historicTaskInstanceType.getType());
		}
		return null;
	}

	/**
	 * 根据流程实例获取流程模板图
	 * @param processId 流程实例
     * @return
     */
	public InputStream getActivitiPicModelByProcessId(String processId){
		List<HistoricProcessInstance> list =	historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).list();
		String processDefId = list.get(0).getProcessDefinitionId();
		//获取模板的信息
			BpmnModel bpmnModel =	repositoryService.getBpmnModel(processDefId);
			List<String>  activeActivityIds =null;
			activeActivityIds = queryHignNodeIds(processId);
			if(activeActivityIds ==null){
				activeActivityIds =new ArrayList<>(0);
			}
			List<String> highLightedFlows=new ArrayList<>(0);
			ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
			InputStream in = processEngineConfiguration.getProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeActivityIds,
					highLightedFlows,"宋体","宋体","宋体",null, 1.0);
			
		return in;
	}












	/**
	 * 处理历史数据为id和name
	 * @param list
	 * @return
     */
	private List<ImaGegenerateUtils.UserTaskInfo> handleData(List<HistoricTaskInstanceEx> list){
		List<ImaGegenerateUtils.UserTaskInfo> userTaskInfoList = new ArrayList<>();
		for(HistoricTaskInstanceEx historicTaskInstanceEx : list){
			ImaGegenerateUtils.UserTaskInfo userTaskNodeInfo =	new ImaGegenerateUtils.UserTaskInfo(historicTaskInstanceEx.getTaskKey(),historicTaskInstanceEx.getName());
			if(!userTaskInfoList.contains(userTaskNodeInfo)){
				userTaskInfoList.add(userTaskNodeInfo);
			}
		}
		return userTaskInfoList;
	}

	private List<String> queryHignNodeIds(String processInstanceId){
		List<Task> list =  taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		List<String> list1 = new ArrayList<>();
		for(Task task : list){
			list1.add(task.getTaskDefinitionKey());
		}
		return list1;
	}

}
