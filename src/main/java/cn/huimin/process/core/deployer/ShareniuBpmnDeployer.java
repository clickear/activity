package cn.huimin.process.core.deployer;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.bpmn.deployer.BpmnDeployer;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.cmd.DeploymentSettings;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityManager;
import org.activiti.engine.impl.persistence.entity.ResourceEntity;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.image.ProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareniuBpmnDeployer extends BpmnDeployer {
	private static final Logger log = LoggerFactory.getLogger(ShareniuBpmnDeployer.class);
	@Override
	public void deploy(DeploymentEntity deployment, Map<String, Object> deploymentSettings) {
		log.debug("Processing deployment {}", deployment.getName());
	    List<ProcessDefinitionEntity> processDefinitions = new ArrayList<ProcessDefinitionEntity>();
	    Map<String, ResourceEntity> resources = deployment.getResources();
	    Map<String, BpmnModel> bpmnModelMap = new HashMap<String, BpmnModel>();
	    final ProcessEngineConfigurationImpl processEngineConfiguration = Context.getProcessEngineConfiguration();
	    for (String resourceName : resources.keySet()) {

	      log.info("Processing resource {}", resourceName);
	      if (isBpmnResource(resourceName)) {
	        ResourceEntity resource = resources.get(resourceName);
	        byte[] bytes = resource.getBytes();
	        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
	        
	        BpmnParse bpmnParse = bpmnParser
	          .createParse()
	          .sourceInputStream(inputStream)
	          .setSourceSystemId(resourceName)
	          .deployment(deployment)
	          .name(resourceName);
	        
	        if (deploymentSettings != null) {
	        	if (deploymentSettings.containsKey(DeploymentSettings.IS_BPMN20_XSD_VALIDATION_ENABLED)) {
	        		bpmnParse.setValidateSchema((Boolean) deploymentSettings.get(DeploymentSettings.IS_BPMN20_XSD_VALIDATION_ENABLED));
	        	}
	        	if (deploymentSettings.containsKey(DeploymentSettings.IS_PROCESS_VALIDATION_ENABLED)) {
	        		bpmnParse.setValidateProcess((Boolean) deploymentSettings.get(DeploymentSettings.IS_PROCESS_VALIDATION_ENABLED));
	        	}
	        	
	        } else {
	        	bpmnParse.setValidateSchema(false);
	        	bpmnParse.setValidateProcess(false);
	        }
	        
	        bpmnParse.execute();
	        for (ProcessDefinitionEntity processDefinition: bpmnParse.getProcessDefinitions()) {
	          processDefinition.setResourceName(resourceName);
	          
	          if (deployment.getTenantId() != null) {
	          	processDefinition.setTenantId(deployment.getTenantId()); 
	          }
	          String diagramResourceName = getDiagramResourceForProcess(resourceName, processDefinition.getKey(), resources);
	          if(deployment.isNew()) {
	            if (processEngineConfiguration.isCreateDiagramOnDeploy() &&
	                  diagramResourceName==null && processDefinition.isGraphicalNotationDefined()) {
	              try {
	            	  ProcessDiagramGenerator hmProcessDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
	                  byte[] diagramBytes = IoUtil.readInputStream(hmProcessDiagramGenerator.generateDiagram(bpmnParse.getBpmnModel(), "png", processEngineConfiguration.getActivityFontName(),
	                        processEngineConfiguration.getLabelFontName(),processEngineConfiguration.getAnnotationFontName(), processEngineConfiguration.getClassLoader()), null);
	                  diagramResourceName = getProcessImageResourceName(resourceName, processDefinition.getKey(), "png");
	                  createResource(diagramResourceName, diagramBytes, deployment);
	              } catch (Throwable t) { 
	                log.warn("Error while generating process diagram, image will not be stored in repository", t);
	              }
	            } 
	          }
	          
	          processDefinition.setDiagramResourceName(diagramResourceName);
	          processDefinitions.add(processDefinition);
	          bpmnModelMap.put(processDefinition.getKey(), bpmnParse.getBpmnModel());
	        }
	      }
	    }
	    List<String> keyList = new ArrayList<String>();
	    for (ProcessDefinitionEntity processDefinition : processDefinitions) {
	      if (keyList.contains(processDefinition.getKey())) {
	        throw new ActivitiException("The deployment contains process definitions with the same key '"+ processDefinition.getKey() +"' (process id atrribute), this is not allowed");
	      }
	      keyList.add(processDefinition.getKey());
	    }
	    
	    CommandContext commandContext = Context.getCommandContext();
	    ProcessDefinitionEntityManager processDefinitionManager = commandContext.getProcessDefinitionEntityManager();
	    DbSqlSession dbSqlSession = commandContext.getSession(DbSqlSession.class);
	    for (ProcessDefinitionEntity processDefinition : processDefinitions) {
	      List<TimerEntity> timers = new ArrayList<TimerEntity>();
	      if (deployment.isNew()) {
	        int processDefinitionVersion;

	        ProcessDefinitionEntity latestProcessDefinition = null;
	        if (processDefinition.getTenantId() != null && !ProcessEngineConfiguration.NO_TENANT_ID.equals(processDefinition.getTenantId())) {
	        	latestProcessDefinition = processDefinitionManager
	        			.findLatestProcessDefinitionByKeyAndTenantId(processDefinition.getKey(), processDefinition.getTenantId());
	        } else {
	        	latestProcessDefinition = processDefinitionManager
	        			.findLatestProcessDefinitionByKey(processDefinition.getKey());
	        }
	        		
	        if (latestProcessDefinition != null) {
	          processDefinitionVersion = latestProcessDefinition.getVersion() + 1;
	        } else {
	          processDefinitionVersion = 1;
	        }

	        processDefinition.setVersion(processDefinitionVersion);
	        processDefinition.setDeploymentId(deployment.getId());

	        String nextId = idGenerator.getNextId();
	        String processDefinitionId = processDefinition.getKey() 
	          + ":" + processDefinition.getVersion()
	          + ":" + nextId; // ACT-505
	                   
	        if (processDefinitionId.length() > 64) {          
	          processDefinitionId = nextId; 
	        }
	        processDefinition.setId(processDefinitionId);
	        
	        if(commandContext.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
	        	commandContext.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
	        			ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_CREATED, processDefinition));
	        }

	        removeObsoleteTimers(processDefinition);
	        addTimerDeclarations(processDefinition, timers);
	        
	        removeExistingMessageEventSubscriptions(processDefinition, latestProcessDefinition);
	        addMessageEventSubscriptions(processDefinition);
	        
	        removeExistingSignalEventSubScription(processDefinition, latestProcessDefinition);
	        addSignalEventSubscriptions(processDefinition);

	        dbSqlSession.insert(processDefinition);
	        addAuthorizations(processDefinition);

	        if(commandContext.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
	        	commandContext.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
	        			ActivitiEventBuilder.createEntityEvent(ActivitiEventType.ENTITY_INITIALIZED, processDefinition));
	        }

	        scheduleTimers(timers);

	      } else {
	        String deploymentId = deployment.getId();
	        processDefinition.setDeploymentId(deploymentId);
	        
	        ProcessDefinitionEntity persistedProcessDefinition = null; 
	        if (processDefinition.getTenantId() == null || ProcessEngineConfiguration.NO_TENANT_ID.equals(processDefinition.getTenantId())) {
	        	persistedProcessDefinition = processDefinitionManager.findProcessDefinitionByDeploymentAndKey(deploymentId, processDefinition.getKey());
	        } else {
	        	persistedProcessDefinition = processDefinitionManager.findProcessDefinitionByDeploymentAndKeyAndTenantId(deploymentId, processDefinition.getKey(), processDefinition.getTenantId());
	        }
	        
	        if (persistedProcessDefinition != null) {
	        	processDefinition.setId(persistedProcessDefinition.getId());
	        	processDefinition.setVersion(persistedProcessDefinition.getVersion());
	        	processDefinition.setSuspensionState(persistedProcessDefinition.getSuspensionState());
	        }
	      }
	      DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
	      deploymentManager.getProcessDefinitionCache().add(processDefinition.getId(), processDefinition);
	      addDefinitionInfoToCache(processDefinition, processEngineConfiguration, commandContext);
	      deployment.addDeployedArtifact(processDefinition);
	      
	      createLocalizationValues(processDefinition.getId(), bpmnModelMap.get(processDefinition.getKey()).getProcessById(processDefinition.getKey()));
	    }
	}
}
