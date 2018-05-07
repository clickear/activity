package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dto.HandlerLog;
import cn.huimin.process.web.dto.NodeHandleType;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.service.HandleProcessService;
import cn.huimin.process.web.util.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * Created by Administrator on 2016/10/28.
 * 流程处理
 */
@Service
public class HandleProcessServiceImpl implements HandleProcessService {
    private static final transient Logger log = Logger.getLogger(HandleProcessServiceImpl.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;


    /**
     * 获取已经完成的活动节点 定义好的
     *
     * @param processId
     */
    @Override
    public Map<String, Object> getCanGoBackActivtiys(String processId) {
        Map<String, Object> map = new HashMap<>();
        //查询当前的任务了
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processId).active().list();
        if (tasks == null || tasks.size() > 1) {
            return map;
        }
        TaskEntity task = (TaskEntity) tasks.get(0);
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).finished().orderByHistoricTaskInstanceEndTime().asc().list();
        //去除重复的和没有人的
        List<HistoricTaskInstance> haveAssign = deleteHistoryAssign(list);
        //需找上一个节点
        //haveAssign为最后为所有的可以回退的节点
        for (int i = 0; i < haveAssign.size(); i++) {
            //找到回退的的节点与当前一致的节点
            if (haveAssign.get(i).getTaskDefinitionKey().equals(task.getTaskDefinitionKey())) {
                break;
            }
            if (!haveAssign.get(i).getExecutionId().equals(haveAssign.get(i).getProcessInstanceId())) {
                continue;
            }
            map.put(haveAssign.get(i).getTaskDefinitionKey(), haveAssign.get(i).getName());
        }
        return map;
    }


    /**
     * 签收功能
     * @param taskId
     * @param userId
     */
    @Transactional
   public void activityClam(String taskId,String userId){
        taskService.claim(taskId,userId);
    }

    /**
     * 管理员执行流程终止
     * @param processId
     */
    @Transactional
    public void delete(String processId){
        String[] strings = processId.split("\\|");
        for(int i=0;i<strings.length;i++){
            runtimeService.deleteProcessInstance(processId,"管理员直接删除");
        }

    }



    /**
     * 流程终止
     *
     * @param processId
     * @param taskId
     * @param userId    谁操作的
     * @param remark    不通过原因
     * @param noProblem
     * @return
     */
    @Transactional
    public SimpleResult processDelete(String processId, String taskId, String userId, String remark, String noProblem) {
        SimpleResult simpleResult = new SimpleResult();
        taskService.setAssignee(taskId, String.valueOf(userId));
        if (remark!=null&&!remark.trim().equals("")){
            taskService.setVariableLocal(taskId, Constants.remark, remark);
        }
        taskService.setVariableLocal(taskId,Constants.result,noProblem);
        //代表是
        if(noProblem==null || noProblem.trim().equals("")){
            taskService.setVariableLocal(taskId,Constants.doWhat,"中止");
        }else {
            taskService.setVariableLocal(taskId,Constants.doWhat,"不通过");
        }
        runtimeService.setVariable(processId,Constants.lastResult,false);
        runtimeService.deleteProcessInstance(processId, remark);
        //通知项目系统
        List<HistoricVariableInstance> historicVariableInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableName(Constants.callbackUrl).list();
        String callBack = "";
        if (historicVariableInstanceList != null && historicVariableInstanceList.size() > 0) {
            callBack = (String) historicVariableInstanceList.get(0).getValue();
        }
        Boolean result = null;
        List<HistoricVariableInstance> resultVariableInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).variableName(Constants.lastResult).list();
        if (resultVariableInstanceList != null && resultVariableInstanceList.size() > 0) {
            result = (Boolean) resultVariableInstanceList.get(resultVariableInstanceList.size()-1).getValue();
        }
       /* HttpClientUtils httpClent = new HttpClientUtils();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.processId, processId);
        jsonObject.put(Constants.result, result);
        //打印处理结果
        log.info(jsonObject.toJSONString());
        String responseJson = httpClent.getHttpParams(callBack, jsonObject);
        JSONObject object = JSONObject.parseObject(responseJson);
        Boolean success = Boolean.parseBoolean(String.valueOf(object.get("success")));
        log.info("回调 处理结果成功是否" + success);*/
        simpleResult.setSuccess(true);
        simpleResult.setMessage("处理成功");
        return  simpleResult;
    }










    /**
     *
     * @param currentTaskId 当前任务id
     * @param backToTaskId  要回退的活动节点
     * @param processId 流程实例id
     *  @param  userId 指定审批人的id
     */
    @Override
    @Transactional
    public SimpleResult dbBackTo(String currentTaskId, List<String> backToTaskId, String doUser, String processId,String userId,String doWhat,String remark) {
        taskService.setVariableLocal(currentTaskId,Constants.remark,remark);
        //不同意
        taskService.setVariableLocal(currentTaskId,Constants.doWhat,doWhat);
        SimpleResult result = new SimpleResult();
        //跳转到另一个节点 节点id;
        TaskEntity task=(TaskEntity)taskService.createTaskQuery().taskId(currentTaskId).singleResult();
        String curNodeId=task.getTaskDefinitionKey();
        String actDefId=task.getProcessDefinitionId();
        Map<String,Object> activityMap= prepare(actDefId, curNodeId, backToTaskId);
        try{
            taskService.setAssignee(currentTaskId,doUser);
            taskService.complete(currentTaskId);
            HandlerLog handlerLog = new HandlerLog();
            handlerLog.setHandleUser(EhrUserDataHandleUtils.employeeInfo(doUser));
            handlerLog.setHandleTime(DateUtils.now());
            handlerLog.setType(NodeHandleType.NODE_BACK);
            handlerLog.setRemark(remark);
            NodeHandlerUtils.taskNodeHandleLog(runtimeService,currentTaskId,handlerLog);
            result.setMessage("回退成功");
            result.setSuccess(true);
            Task task1 = taskService.createTaskQuery().processInstanceId(processId).singleResult();
            //清除原先设置好的关系
            List<IdentityLink> links= taskService.getIdentityLinksForTask(task1.getId());
            for(int j=0;j<links.size();j++){
                IdentityLink identityLink =  links.get(j);
                String groupid = identityLink.getGroupId();
                if(groupid!=null&& !groupid.equals("")){
                    taskService.deleteCandidateGroup(task1.getId(),groupid);
                }
                String olduserId = identityLink.getUserId();
                if(olduserId!=null && !olduserId.equals("")){
                    taskService.deleteCandidateUser(task1.getId(),userId);
                }
            }
            taskService.setAssignee(task1.getId(),userId);
        }
        catch(Exception ex){
            result.setSuccess(false);
            result.setMessage("操作失败，请联系管理员");
            throw new RuntimeException(ex);
        }
        finally{
            //恢复
            restore(activityMap);
            //给当前活动流程设置人
            return  result;
        }
    }



    /**
     * 将临时节点清除掉，加回原来的节点。
     * @param map
     * void
     */
    @SuppressWarnings("unchecked")
    private void restore(Map<String,Object> map){
        ActivityImpl curAct=(ActivityImpl) map.get("activity");
        List<PvmTransition> outTrans=(List<PvmTransition>) map.get("outTrans");
        curAct.getOutgoingTransitions().clear();
        curAct.getOutgoingTransitions().addAll(outTrans);
    }

    /**
     * 将节点之后的节点删除然后指向新的节点。
     * @param actDefId   流程定义ID
     * @param nodeId   流程节点ID
     * @param aryDestination 需要跳转的节点
     * @return Map<String,Object> 返回节点和需要恢复节点的集合。
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object>  prepare(String actDefId,String nodeId,List<String> aryDestination){
        Map<String,Object> map=new HashMap<String, Object>();

        //修改流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(actDefId);

        ActivityImpl curAct= processDefinition.findActivity(nodeId);
        //获取所有的出口
        List<PvmTransition> outTrans= curAct.getOutgoingTransitions();
        List<PvmTransition> cloneOutTrans= new ArrayList<PvmTransition>();
        try{
            for(PvmTransition out : outTrans){
                cloneOutTrans.add(out);
            }
            map.put("outTrans", cloneOutTrans);
        }
        catch(Exception ex){
                throw new RuntimeException();

        }

        /**
         * 解决通过选择自由跳转指向同步节点导致的流程终止的问题。
         * 在目标节点中删除指向自己的流转。
         */
        for(Iterator<PvmTransition> it = outTrans.iterator(); it.hasNext();){
            PvmTransition transition=it.next();
            PvmActivity activity= transition.getDestination();
            List<PvmTransition> inTrans= activity.getIncomingTransitions();
            for(Iterator<PvmTransition> itIn=inTrans.iterator();itIn.hasNext();){
                PvmTransition inTransition=itIn.next();
                if(inTransition.getSource().getId().equals(curAct.getId())){
                    itIn.remove();
                }
            }
        }
        curAct.getOutgoingTransitions().clear();
        if(aryDestination!=null && aryDestination.size()>0){
            for(String dest:aryDestination){
                //创建一个连接
                ActivityImpl destAct= processDefinition.findActivity(dest);
                //给当前活动重新创建一个出口
                TransitionImpl transitionImpl = curAct.createOutgoingTransition();
                //将目的地返回给他
                transitionImpl.setDestination(destAct);
            }
        }

        map.put("activity", curAct);
        return map;
    }


    /**
     * 去除没没有审批的人的
     * @param
     * @return
     */
    private List<HistoricTaskInstance> deleteHistoryAssign(List<HistoricTaskInstance> historicTaskInstanceList){
        for (int i=0;i<historicTaskInstanceList.size();i++){
            if(historicTaskInstanceList.get(i).getAssignee()==null || historicTaskInstanceList.get(i).getAssignee().equals("")){
                historicTaskInstanceList.remove(i);
            }

        }
        return findQueryTaskList(historicTaskInstanceList);
    }

    /**
     * 处理重复的可以返回的历史实例
     * @param historicTaskInstances
     * @return
     */
    private List<HistoricTaskInstance> findQueryTaskList(List<HistoricTaskInstance> historicTaskInstances){
        //去空
        for (int i = 0; i < historicTaskInstances.size() - 1; i++) {
            for (int j = historicTaskInstances.size() - 1; j > i; j--) {                         //这里非常巧妙，这里是倒序的是比较
                if (historicTaskInstances.get(j).getTaskDefinitionKey().equals(historicTaskInstances.get(i).getTaskDefinitionKey())) {
                    historicTaskInstances.remove(j);
                }
            }
        }
        return historicTaskInstances;
    }
}
