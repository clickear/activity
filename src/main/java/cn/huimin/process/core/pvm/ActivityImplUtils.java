package cn.huimin.process.core.pvm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.log4j.Logger;

public class ActivityImplUtils {
	private static final transient Logger log = Logger
			.getLogger(ActivityImplUtils.class);
	/**
	 * 是否相邻，暂时不考虑加签
	 * @param processEngine
	 * @param processDefinitionId
	 * @param preTaskKey
	 * @param nextTaskKey
	 * @return
	 * @throws Exception 
	 */
	public static boolean isAdjacent(ProcessEngine processEngine, String processDefinitionId, String preTaskKey,
			String nextTaskKey) throws Exception {
		BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(processDefinitionId);
		List<String> outgoingFlows = new ArrayList<String>();
		List<String> ingoingFlows = new ArrayList<String>();
		if (bpmnModel != null) {
			// 因为我们这里只定义了一个Process 所以获取集合中的第一个即可
			Process process = bpmnModel.getProcesses().get(0);
			// 获取所有的FlowElement信息
			FlowElement pre = process.getFlowElement(preTaskKey);
			if (pre==null ) {
				throw new Exception();
			}else {
				if (pre instanceof UserTask) {
					UserTask preUserTask = (UserTask) pre;
					List<SequenceFlow> outgoingSequenceFlows = preUserTask.getOutgoingFlows();
					for (SequenceFlow sequenceFlow : outgoingSequenceFlows) {
						if (sequenceFlow.getTargetRef() != null) {
							FlowElement flowElement = process.getFlowElement(sequenceFlow.getTargetRef());
							if (flowElement instanceof Gateway) {
								Gateway gateway = (Gateway) flowElement;
								List<SequenceFlow> outgoingFlows2 = gateway.getOutgoingFlows();
								for (SequenceFlow s : outgoingFlows2) {
									outgoingFlows.add(s.getId());
								}
							}
						}
						outgoingFlows.add(sequenceFlow.getId());
					}
				}
			}
			FlowElement next = process.getFlowElement(nextTaskKey);
			if (next==null ) {
				throw new Exception();
			}else{
				if (pre instanceof UserTask) {
					UserTask preUserTask = (UserTask) next;
					List<SequenceFlow> outgoingSequenceFlows = preUserTask.getIncomingFlows();
					for (SequenceFlow sequenceFlow : outgoingSequenceFlows) {
						ingoingFlows.add(sequenceFlow.getId());
					}
				}
			}
		
			outgoingFlows.retainAll(ingoingFlows);
			return outgoingFlows.size()>0;

		}
		return false;

	}
	public static synchronized void removeTemporaryActivityImpl(List<String> tempIds,
												   String processDefinitionId, RepositoryService repositoryService,ProcessEngineConfiguration processEngineConfigurationI) {
		if (log.isInfoEnabled()) {
			log.info("ActivityImplUtils.removeTemporaryActivityImpl================begin");
		}
		ProcessDefinition processDefinition = repositoryService
				.getProcessDefinition(processDefinitionId);
		if (processDefinition != null) {
			ProcessDefinitionEntity pdf = (ProcessDefinitionEntity) processDefinition;
			Iterator<ActivityImpl> activities = pdf.getActivities().iterator();
			if (log.isInfoEnabled()) {
				log.info("ActivityImplUtils.removeTemporaryActivityImpl================1.1 activities:"
						+ activities);
			}
			while (activities.hasNext()) {
				ActivityImpl activityImpl = activities.next();
				if (tempIds.contains(activityImpl.getId())) {
					activities.remove();
				}
			}
			if (log.isInfoEnabled()) {
				log.info("ActivityImplUtils.removeTemporaryActivityImpl================1.2 finally activities:"
						+ activities);
			}
			Class<?> cls = pdf.getClass().getSuperclass().getSuperclass();
			try {
				Field declaredField = cls.getDeclaredField("namedActivities");
				declaredField.setAccessible(true);
				Map<String, ActivityImpl> namedActivities =(Map<String, ActivityImpl>) declaredField.get(pdf);
				for (String tempId : tempIds) {
					namedActivities.remove(tempId);
				}
				declaredField.setAccessible(true);
				declaredField.set(pdf, namedActivities);
				if (log.isInfoEnabled()) {
					log.info("ActivityImplUtils.removeTemporaryActivityImpl================1.3 namedActivities:"
							+ activities);
				}
				ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngineConfigurationI;
				processEngineConfiguration.getDeploymentManager().getProcessDefinitionCache()
				.add(processDefinitionId, pdf);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}


	public static synchronized List<String> getNextActivityImpl(ActivityImpl activityImpl) {
		List<String> needWrite = new ArrayList<>();
		if (activityImpl != null) {
			List<PvmTransition> outgoingTransitions = activityImpl
					.getOutgoingTransitions();

			if (outgoingTransitions != null && outgoingTransitions.size() > 0) {
				PvmTransition pvmTransition = outgoingTransitions.get(0);
				PvmActivity destination = pvmTransition.getDestination();
				while (destination != null) {
					needWrite.add(destination.getId());
					outgoingTransitions = destination.getOutgoingTransitions();
					if (outgoingTransitions != null
							&& outgoingTransitions.size() > 0) {
						destination = outgoingTransitions.get(0)
								.getDestination();
					} else {
						destination = null;
					}

				}
			}

		}
		return needWrite;

	}
}
