package cn.huimin.process.core.session;

import java.util.Date;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.history.DefaultHistoryManager;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareniuHistoryManager extends DefaultHistoryManager {
	  private static Logger log = LoggerFactory.getLogger(ShareniuHistoryManager.class.getName());
	public void recordActivityEnd(ExecutionEntity executionEntity) {
		if (isHistoryLevelAtLeast(HistoryLevel.ACTIVITY)) {
			HistoricActivityInstanceEntity historicActivityInstance = findActivityInstance(executionEntity);
			log.info("ShareniuHistoryManager:recordActivityEnd executionEntity.getVariable auto "+executionEntity.getVariable("auto"));
			
			if (executionEntity.getVariable("auto") != null) {
				endHistoricActivityInstance(historicActivityInstance, executionEntity);
			}else{
				if (historicActivityInstance != null) {
					endHistoricActivityInstance(historicActivityInstance);
				}
			}
		
		}
	}
	protected void endHistoricActivityInstance(HistoricActivityInstanceEntity historicActivityInstance,ExecutionEntity executionEntity) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Date endTime = new Date(System.currentTimeMillis());
		historicActivityInstance.setDeleteReason(null);
		historicActivityInstance.setEndTime(endTime);
		historicActivityInstance.setDurationInMillis(endTime.getTime()-historicActivityInstance.getStartTime().getTime());
		executionEntity.removeVariable("auto");
		ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
		if (config != null && config.getEventDispatcher().isEnabled()) {
			config.getEventDispatcher().dispatchEvent(ActivitiEventBuilder
					.createEntityEvent(ActivitiEventType.HISTORIC_ACTIVITY_INSTANCE_ENDED, historicActivityInstance));
		}
	}
	protected void endHistoricActivityInstance(HistoricActivityInstanceEntity historicActivityInstance) {
		Date endTime = new Date(System.currentTimeMillis());
		historicActivityInstance.setDeleteReason(null);
		historicActivityInstance.setEndTime(endTime);
		historicActivityInstance.setDurationInMillis(endTime.getTime()-historicActivityInstance.getStartTime().getTime());
		ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
		if (config != null && config.getEventDispatcher().isEnabled()) {
			config.getEventDispatcher().dispatchEvent(ActivitiEventBuilder
					.createEntityEvent(ActivitiEventType.HISTORIC_ACTIVITY_INSTANCE_ENDED, historicActivityInstance));
		}
	}
}
