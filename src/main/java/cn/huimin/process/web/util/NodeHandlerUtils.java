package cn.huimin.process.web.util;

import cn.huimin.process.core.UpdateHiTaskReasonCommand;
import cn.huimin.process.web.dto.HandlerLog;
import com.alibaba.fastjson.JSON;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyp on 2017/3/18.
 */
public class NodeHandlerUtils {

    /**
     * 根据任务节点key查询所有审批人
     * @param
     * @param taskKey
     * @return
     */
    public static List<String> queryUserTaskHandler(ProcessEngine processEngine, String processId,String taskKey){
        //判定当前流程是否结束
        //没有结束
        List<String> user = new ArrayList<>(0);
          //从运行中去找
          TaskService taskService = processEngine.getTaskService();
          List<Task> tasks =  taskService.createTaskQuery().processInstanceId(processId).taskDefinitionKey(taskKey).active().list();
            if(tasks!=null&&tasks.size()>0){
                for(Task task:tasks){
                    //如果有审核人的
                    if(task.getAssignee()!=null){
                        user.add(task.getAssignee());
                        continue;
                    }
                        List<IdentityLink> links =  taskService.getIdentityLinksForTask(task.getId());
                        for (IdentityLink link:links){
                            user.add(link.getUserId());

                        }
                }
                return user;
            }
            //从历史中去找
            HistoryService historyService = processEngine.getHistoryService();
           List<HistoricTaskInstance> historicTaskInstances=  historyService.createHistoricTaskInstanceQuery().taskDefinitionKey(taskKey).processInstanceId(processId).finished().list();
            //如果
            if(historicTaskInstances==null){
                return null;
            }
            for(HistoricTaskInstance historicTaskInstance:historicTaskInstances){
                if(!StringUtils.isEmpty(historicTaskInstance.getAssignee())){
                    user.add(historicTaskInstance.getAssignee());
                }
            }
        return user;
    }


    /**
     * 查询发起人
     * @param processInstanceId
     * @return
     */
    public static String queryStartUserId(ProcessEngine processEngine ,String processInstanceId){
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        return historicProcessInstance.getStartUserId();
    }

    /**
     * 执行完成节点之前的操作
     * @param runtimeService
     * @param taskId
     * @param handlerLog
     */
    public static void taskNodeHandleLog(RuntimeService runtimeService, String taskId, HandlerLog handlerLog){
        RuntimeServiceImpl runtimeServiceImpl = (RuntimeServiceImpl) runtimeService ;
        runtimeServiceImpl.getCommandExecutor().execute(new UpdateHiTaskReasonCommand(taskId, JSON.toJSONString(handlerLog)));
    }










}
