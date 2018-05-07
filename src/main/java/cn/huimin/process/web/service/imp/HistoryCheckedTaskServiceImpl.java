package cn.huimin.process.web.service.imp;

import cn.huimin.process.activiticmd.DeleteRunningTaskCmd;
import cn.huimin.process.activiticmd.StartActivityCmd;
import cn.huimin.process.core.pvm.ActivityImplUtils;
import cn.huimin.process.web.dao.HistoryCheckedTaskDao;
import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.HistoryCheckedTask;
import cn.huimin.process.web.service.HistoryCheckedTaskService;
import cn.huimin.process.web.util.EhrUserDataHandleUtils;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 历史当前用户的审批任务
 */
@Service
public class HistoryCheckedTaskServiceImpl implements HistoryCheckedTaskService {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryCheckedTaskDao historyCheckedTaskDao;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;




    @Value("${getRoleByUserId}")
    private String getRoleByUserId;



    /**
     * 分页查询自己审批过的流程审
     * @param authId
     * @param startResult
     * @param maxResult
     * @return
     */

    public Page queryCheckedTaskCompletedByAuthId(String authId, int startResult, int maxResult){
        Page page = new Page();
        HistoryCheckedTask historyCheckedTask = new HistoryCheckedTask();
        historyCheckedTask.setTaskAssigin(authId);
        PageHelper.startPage(startResult,maxResult);
        List<HistoryCheckedTask> historyCheckedTasks = historyCheckedTaskDao.queryPage(historyCheckedTask);
        PageInfo<HistoryCheckedTask> pageInfo = new PageInfo<HistoryCheckedTask>(historyCheckedTasks);
        page.setPage(pageInfo.getPrePage());
        for(HistoryCheckedTask historyCheckedTask1:historyCheckedTasks){
            String adminId = historyCheckedTask1.getApplayName();
            String applayName =EhrUserDataHandleUtils.employeeInfo(adminId,getRoleByUserId);
             historyCheckedTask1.setApplayName(applayName);
                //任务类型
             historyCheckedTask1.setTaskType(applayName+("  |  ")+historyCheckedTask1.getTaskType());
              historyCheckedTask1.setResult(this.isPick(historyCheckedTask1.getProcessId(), historyCheckedTask1.getTaskId()));
            }
        page.setRows(historyCheckedTasks);
        page.setTotal(queryCheckedTaskCompletedCount(authId));
        return page;
    }
    /**
     * 查询审核过的任务
     * @param authId
     * @return
     */
    private  Long queryCheckedTaskCompletedCount(String authId){
        return historyService.createHistoricTaskInstanceQuery().taskAssignee(authId).finished().orderByTaskCreateTime().desc().count();
    }


    /**
     * 查看是否支持取回
     * @param processId
     * @param taskId
     * @return
     */
    public boolean isPick(String processId,String taskId){
        List<ProcessInstance> processInstanceList= runtimeService.createProcessInstanceQuery().processInstanceId(processId).list();
        if(processInstanceList==null || processInstanceList.size()==0){
            return false;
        }
        if(processInstanceList.size()>1){
            return false;
        }
        //查询当前任务节点的个数
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processId).list();
        if(tasks.size()>1){
            return false;
        }

        //历史活动节点
        List<HistoricTaskInstance> historicTaskInstanceList =   historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).finished().orderByHistoricTaskInstanceEndTime().desc().list();
        //这里当前只有一个活动节点
        if(historicTaskInstanceList==null|| historicTaskInstanceList.size()<1){
            return  false;
        }
        //完成的节点任务
        HistoricTaskInstance  completeTaskInstance =  historyService.createHistoricTaskInstanceQuery().taskId(taskId).finished().singleResult();
       //完成任务实例
       ActivityImpl activity = ProcessDefinitionUtils.getActivity(processEngine,completeTaskInstance.getProcessDefinitionId(),completeTaskInstance.getTaskDefinitionKey());
       if(activity!=null){
           if(activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior){
               return false;
           }
       }
        //如果不相等代表不是最后一个不支持流程回退
        if(completeTaskInstance==null){
            return false;
        }

        //历史节点
        ActivityImpl oldActivity = ProcessDefinitionUtils.getActivity(processEngine, completeTaskInstance.getProcessDefinitionId(),
                completeTaskInstance.getTaskDefinitionKey());
        if (oldActivity ==null){
            return false;
        }
        ActivityBehavior activityBehavior = oldActivity.getActivityBehavior();
        if(activityBehavior==null){
            return false;
        }
        if(activityBehavior instanceof UserTaskActivityBehavior){
           // MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) activityBehavior;
           UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
           MultiInstanceActivityBehavior manualTaskActivityBehavior = userTaskActivityBehavior.getMultiInstanceActivityBehavior();
           if(manualTaskActivityBehavior!=null){
               return false;
           }
        }
        boolean flag = true;
        try {
           flag = ActivityImplUtils.isAdjacent(processEngine,completeTaskInstance.getProcessDefinitionId(),completeTaskInstance.getTaskDefinitionKey(),tasks.get(0).getTaskDefinitionKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
     /*   List<PvmTransition> list =oldActivity.getOutgoingTransitions();
        for(PvmTransition pvmTransition : list){
            //获取资源的出口
            PvmActivity pvmActivity = pvmTransition.getDestination();
            if(pvmActivity.getProperty("type").equals(BpmnConstants.parallelGateway)) {
                if (pvmActivity.getOutgoingTransitions().size() != tasks.size()) {
                    return false;
                }
            }
        }*/
        return flag;

    }


    /***
     * 取回操作
     * @param processId 流程实例
     * @param taskId 历史活动节点
     * @return
     */
    @Transactional
    public SimpleResult doPick(String processId, String taskId,String userId) throws Exception {
        //查询当前任务节点的个数
        SimpleResult simpleResult =new SimpleResult();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processId).list();
        //完成的节点任务
        List<HistoricTaskInstance>  completeTaskInstances =  historyService.createHistoricTaskInstanceQuery().taskId(taskId).finished().list();
        HistoricTaskInstance completeTaskInstance = completeTaskInstances.get(0);
        ActivityImpl oldActivity = ProcessDefinitionUtils.getActivity(processEngine, completeTaskInstance.getProcessDefinitionId(),
                completeTaskInstance.getTaskDefinitionKey());
        RuntimeServiceImpl runtimeService = (RuntimeServiceImpl) processEngine.getRuntimeService();
        //执行器
        for(Task task :tasks){
            //删除所有正在运行的任务 这里是删除运行中的任务
            runtimeService.getCommandExecutor().execute(new DeleteRunningTaskCmd((TaskEntity)task));
        }
        runtimeService.getCommandExecutor().execute(new StartActivityCmd(completeTaskInstance.getExecutionId(),1,oldActivity,userId));
        historyService.deleteHistoricTaskInstance(taskId);
        simpleResult.setSuccess(true);
        simpleResult.setProcessId(processId);
        //借用一下作为
        simpleResult.setTaskId(oldActivity.getId());
        return simpleResult;
    }
}
