package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dto.HistoryProcessData;
import cn.huimin.process.web.service.HistroyProcessQueryService;
import cn.huimin.process.web.util.EhrUserDataHandleUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 所有历史流程
 */
@Service
public class HistroyProcessQueryServiceImpl implements HistroyProcessQueryService{


    @Autowired
    private HistoryService historyService;

    @Value("${getRoleByUserId}")
    private String getRoleByUserId;



    @Autowired
    private RepositoryService repositoryService;
    public HistoryProcessData queryHistoryProcessByProcessId(String processId){
       HistoryProcessData historyProcessData = new HistoryProcessData();
       //HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
       List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).list();
        String buKey =  historicProcessInstances.get(0).getStartUserId();
        //流程name
       String name = repositoryService.createProcessDefinitionQuery().processDefinitionId(historicProcessInstances.get(0).getProcessDefinitionId()).singleResult().getName();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(EhrUserDataHandleUtils.employeeInfo(buKey,getRoleByUserId)).append("  |  ").append(name);
        historyProcessData.setProcessName(stringBuilder.toString());
        return historyProcessData;
    }




}
