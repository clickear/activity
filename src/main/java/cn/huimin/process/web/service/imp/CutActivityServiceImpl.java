package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.service.CutActivityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 */
@Service
public class CutActivityServiceImpl implements CutActivityService{


    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;
    /**
     * 实现减签功能
     * @param taskId
     * @param processId
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public SimpleResult cutActivity(String taskId, String processId, String user) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).taskId(taskId).active().processInstanceId(processId).singleResult();
        //当前活动焦点
        ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createExecutionQuery().processInstanceId(processId).singleResult();
        //修改流程定义
        //实际定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
       //当前节点
        ActivityImpl activity = processDefinition.findActivity(task.getTaskDefinitionKey());
        SimpleResult simpleResult = new SimpleResult();
       List <PvmTransition>  list =activity.getOutgoingTransitions();
        if(list==null || list.size()>1){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("不支持加签");
            return simpleResult;
        }
        ActivityImpl  nextActivity;
        //循环只有一个
        for (PvmTransition pvmTransition:list){
            //获取目的地
            PvmActivity pvmActivity = pvmTransition.getDestination();
            if(pvmActivity instanceof  ActivityImpl){
                 nextActivity = (ActivityImpl) pvmActivity;

            }
        }
        return null;
    }
}
