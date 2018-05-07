package cn.huimin.process.core;

import cn.huimin.process.web.util.UUIDutils;
import org.activiti.bpmn.model.MapExceptionEntry;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.CallActivityBehavior;
import org.activiti.engine.impl.bpmn.data.AbstractDataAssociation;
import org.activiti.engine.impl.bpmn.helper.ErrorPropagation;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntityManager;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmProcessInstance;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wyp on 2017/10/18.
 */
public class HmCallActivityBehavior extends CallActivityBehavior {

    private List<AbstractDataAssociation> dataInputAssociations = new ArrayList<AbstractDataAssociation>();
    private List<AbstractDataAssociation> dataOutputAssociations = new ArrayList<AbstractDataAssociation>();
    private Expression processDefinitionExpression;

    public HmCallActivityBehavior(String processDefinitionKey, List<MapExceptionEntry> mapExceptions) {
        super(processDefinitionKey, mapExceptions);
    }

    public HmCallActivityBehavior(Expression processDefinitionExpression, List<MapExceptionEntry> mapExceptions) {
        super(processDefinitionExpression, mapExceptions);
    }

    @Override
    public void addDataInputAssociation(AbstractDataAssociation dataInputAssociation) {
        this.dataInputAssociations.add(dataInputAssociation);
    }

    @Override
    public void addDataOutputAssociation(AbstractDataAssociation dataOutputAssociation) {
        this.addDataOutputAssociation(dataOutputAssociation);
    }

    @Override
    public void execute(ActivityExecution execution) throws Exception {
        String processDefinitonKey = this.processDefinitonKey;
        if (processDefinitionExpression != null) {
            processDefinitonKey = (String) processDefinitionExpression.getValue(execution);
        }

        DeploymentManager deploymentManager = Context.getProcessEngineConfiguration().getDeploymentManager();

        ProcessDefinitionEntity processDefinition = null;
        if (execution.getTenantId() == null || ProcessEngineConfiguration.NO_TENANT_ID.equals(execution.getTenantId())) {
            processDefinition = deploymentManager.findDeployedLatestProcessDefinitionByKey(processDefinitonKey);
        } else {
            processDefinition = deploymentManager.findDeployedLatestProcessDefinitionByKeyAndTenantId(processDefinitonKey, execution.getTenantId());
        }

        // Do not start a process instance if the process definition is suspended
        if (deploymentManager.isProcessDefinitionSuspended(processDefinition.getId())) {
            throw new ActivitiException("Cannot start process instance. Process definition "
                    + processDefinition.getName() + " (id = " + processDefinition.getId() + ") is suspended");
        }
        PvmProcessInstance subProcessInstance = execution.createSubProcessInstance(processDefinition);
        String subBussinessKey = null;
        //给子流程添加业务id 开始
        ExecutionEntity subProcessInstanceNew = null;
        if (subProcessInstance instanceof ExecutionEntity) {
            subProcessInstanceNew = (ExecutionEntity) subProcessInstance;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(execution.getProcessBusinessKey()).append(",").append(UUIDutils.createShortUUID());
            subBussinessKey = stringBuilder.toString();
            subProcessInstanceNew.setBusinessKey(subBussinessKey);
        }
        if (subProcessInstanceNew != null) {
            subProcessInstance = subProcessInstanceNew;
        }

        // 给子流程拼接业务id结束
        if (inheritVariables) {
            Map<String, Object> variables = execution.getVariables();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                subProcessInstance.setVariable(entry.getKey(), entry.getValue());
            }
        }

        // copy process variables
        for (AbstractDataAssociation dataInputAssociation : dataInputAssociations) {
            Object value = null;
            if (dataInputAssociation.getSourceExpression() != null) {
                value = dataInputAssociation.getSourceExpression().getValue(execution);
            } else {
                value = execution.getVariable(dataInputAssociation.getSource());
            }
            subProcessInstance.setVariable(dataInputAssociation.getTarget(), value);
        }

        try {
            //改变历史记录
            HistoricProcessInstanceEntityManager historicProcessInstanceEntityManager =
                    Context.getCommandContext().getHistoricProcessInstanceEntityManager();
            HistoricProcessInstanceEntity historicProcessInstance =
                    historicProcessInstanceEntityManager.findHistoricProcessInstance(subProcessInstanceNew.getId());
            historicProcessInstance.setBusinessKey(subBussinessKey);
            subProcessInstance.start();
        } catch (Exception e) {
            if (!ErrorPropagation.mapException(e, execution, mapExceptions, true))
                throw e;

        }
    }


}
