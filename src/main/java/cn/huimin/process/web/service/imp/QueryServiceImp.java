package cn.huimin.process.web.service.imp;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryServiceImp {


    //历史服务
    @Autowired
    private HistoryService historyService;
    //仓库id


    @Value("${getRoleByUserId}")
    private String getRoleByUserId;








    /**
     * 获取历史流程变量
     * @param processId
     * @param valName
     * @return
     */
    public Object getHistoryVar(String processId,String valName){
        if(processId==null||processId.equals("")){
            return null;
        }
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableName(valName).list();
        if(variableInstances==null || variableInstances.size()==0){
            return null;
        }
        return variableInstances.get(variableInstances.size()-1).getValue();
    }


}

