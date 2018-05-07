package cn.huimin.process.web.service.imp;

import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.SpecialNodeConstants;
import cn.huimin.process.core.pvm.ProcessExtensionAttributeUtils;
import cn.huimin.process.core.pvm.ProcessNodeSkipUtils;
import cn.huimin.process.web.dao.HistoricTaskInstanceExDao;
import cn.huimin.process.web.dao.InformProcessDao;
import cn.huimin.process.web.dao.TaskDao;
import cn.huimin.process.web.dto.HandlerLog;
import cn.huimin.process.web.dto.NodeHandleType;
import cn.huimin.process.web.dto.UserTaskInfo;
import cn.huimin.process.web.model.CheckInfo;
import cn.huimin.process.web.model.HistoricTaskInstanceEx;
import cn.huimin.process.web.model.InformProcess;
import cn.huimin.process.web.model.TaskAPIData;
import cn.huimin.process.web.service.HistoryCheckedTaskService;
import cn.huimin.process.web.service.TaskServiceEx;
import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.NodeHandlerUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import cn.huimin.process.web.util.ProcessInstanceUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by wyp on 2017/3/10.
 */
@Service
public class TaskServiceExImpl implements TaskServiceEx {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private InformProcessDao informProcessDao;

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ProcessEngine processEngine;

    @Value("${getRoleByUserId}")
    private String getRoleByUserId;

    @Autowired
    private HistoryCheckedTaskService historyCheckedTaskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoricTaskInstanceExDao historicTaskInstanceExDao;


    /**
     * @param systemId
     * @param userId
     * @param taskId
     * @param parameter
     * @return 流程实例id
     */
    @Override
    @Transactional
    public String doTask(String systemId, String userId, String taskId, JSONObject parameter) {
        taskService.setAssignee(taskId, userId);
        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
        Task task = null;
        if (!ObjectCheckUtils.isEmptyCollection(tasks)) {
            task = tasks.get(0);
        }
        //如果为空的话
        if (parameter == null) {
            //执行操作
            parameter = new JSONObject();
        }
        //拼接历史记录信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(parameter);
        jsonObject.put("doTime", DateUtils.now());
        Map<String, Object> map = handleCheckedCompletedData(jsonObject);
        map.put("taskKey", task.getTaskDefinitionKey());
        map.put("taskName", task.getName());
        parameter.put(InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO, JSONObject.toJSONString(map));
        //获取审核人的id
        parameter.put(InnerActivitiVarConstants.HM_ACITVITI_PRE_HANDLER_ID, userId);
        //这里返回结果
        parameter.put(InnerActivitiVarConstants.HM_ACITVITI_PROCESS_RESULT, true);
        taskService.complete(taskId, parameter);
        //删除之后执行保存日志操作
        HandlerLog handlerLog = new HandlerLog();
        handlerLog.setHandleUser(userId);
        handlerLog.setType(NodeHandleType.NODE_PASS);
        handlerLog.setHandleTime(new Date());
        NodeHandlerUtils.taskNodeHandleLog(runtimeService, taskId, handlerLog);
        //完成之后需要设置判定下一个节点是否是知会节点
        String processInstanceId = task.getProcessInstanceId();
        this.completeInformActivity(processInstanceId);
        return processInstanceId;

    }


    /**
     * 对请求的数据进行处理获取处理表单审核日志
     *
     * @return
     */
    public Map<String, Object> handleCheckedCompletedData(Map<String, Object> completeData) {
        Map<String, Object> map = new HashMap<>();
        Iterator<Map.Entry<String, Object>> iterator = completeData.entrySet().iterator();
        CheckInfo checkInfo = new CheckInfo();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> stringStringEntry = iterator.next();
            Field[] fields = checkInfo.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals(stringStringEntry.getKey())) {
                    map.put(stringStringEntry.getKey(), stringStringEntry.getValue());
                    iterator.remove();
                    break;
                }
            }
        }
        return map;
    }

    /**
     * 执行任意跳转
     *
     * @param systemId
     * @param userId
     * @param taskId
     * @param type
     * @return
     */
    @Override
    @Transactional
    public List<UserTaskInfo> handleSkipNode(String systemId, String userId, String taskId, CheckInfo checkInfo, String type) {
        List<UserTaskInfo> userTaskInfos = null;
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null){
            throw new NullPointerException("Cannot find task with id "+taskId);
        }
        switch (type) {
            case "1":
              userTaskInfos = backStartNode(taskId, userId,checkInfo,task);
                break;
            default:
                break;
        }
        return userTaskInfos;
    }

    /**
     * 去掉重复的审批节点
     * @param taskAPIDataParam
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<TaskAPIData> taskDistinctCompletedList(TaskAPIData taskAPIDataParam, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TaskAPIData> list = taskDao.queryDistinctProcessByUserIdAndSystemIdChecked(taskAPIDataParam);
        PageInfo pageInfo = new PageInfo(list);
        list = pageInfo.getList();
        for (TaskAPIData taskAPIData : list) {
            //设置为可取回
            taskAPIData.setCanPick(historyCheckedTaskService.isPick(taskAPIData.getProcessInstanceId(), taskAPIData.getTaskId()));
        }
        return pageInfo;
    }

    /**
     * 回退到发起的节点
     *
     * @param taskId
     * @param userId
     * @return 返回回退后的处理人id
     */
    private List<UserTaskInfo> backStartNode(String taskId, String userId,CheckInfo checkInfo,Task task) {

        if (task==null){
            throw new NullPointerException("taskId is null");
        }
        HistoricTaskInstanceEx historicTaskInstanceEx = new HistoricTaskInstanceEx();
        historicTaskInstanceEx.setTaskId(taskId);
        historicTaskInstanceEx.setFormKey(SpecialNodeConstants.START_NODE);
        List<HistoricTaskInstanceEx> historicTaskInstanceices = historicTaskInstanceExDao.queryHistoryTasksByTaskId(historicTaskInstanceEx);
        if (ObjectCheckUtils.isEmptyCollection(historicTaskInstanceices)) {
            return null;
        }

        checkInfo.setTaskKey(task.getTaskDefinitionKey());
        checkInfo.setTaskName(task.getName());
        HandlerLog handlerLog = new HandlerLog();
        handlerLog.setHandleUser(userId);
        handlerLog.setHandleTime(DateUtils.now());
        handlerLog.setType(NodeHandleType.NODE_BACK_DELETE);
        HistoricTaskInstanceEx historicTaskInstanceEx1 = historicTaskInstanceices.get(0);
        ProcessNodeSkipUtils.handleNodeSkip(processEngine, historicTaskInstanceEx1.getTaskKey(), historicTaskInstanceEx1.getProcessDefinitionId(), historicTaskInstanceEx1.getProcessInstanceId(), JSON.toJSONString(handlerLog),JSON.toJSONString(checkInfo));
        List<UserTaskInfo> list = queryTaskInfoByProcessInstanceId(historicTaskInstanceEx1.getProcessInstanceId());
        for (UserTaskInfo userTaskInfo : list) {
            ArrayList<String> strings = new ArrayList<>();
            strings.add(historicTaskInstanceEx1.getAssignee());
            taskService.setAssignee(userTaskInfo.getTaskId(),historicTaskInstanceEx1.getAssignee());
            userTaskInfo.setHandlers(strings);
        }
        return list;
    }


    /**
     * 通过流程实例id获取当前节点的详情
     *
     * @param processInstanceId
     * @return
     */
    public List<UserTaskInfo> queryTaskInfoByProcessInstanceId(String processInstanceId) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        List<UserTaskInfo> taskInfos = new ArrayList<>(0);
        for (Task task : list) {
            UserTaskInfo taskInfo = new UserTaskInfo();
            List<String> ids = new ArrayList<>();
            if (!ObjectCheckUtils.isEmptyString(task.getAssignee())) {
                ids.add(task.getAssignee());
            } else {
                List<IdentityLink> links = taskService.getIdentityLinksForTask(task.getId());
                for (IdentityLink identityLink : links) {
                    ids.add(identityLink.getUserId());
                }
            }
            taskInfo.setProcessInstancId(processInstanceId);
            taskInfo.setHandlers(ids);
            taskInfo.setTaskId(task.getId());
            taskInfo.setHandleTime(task.getDueDate());
            taskInfo.setTaskName(task.getName());
            taskInfo.setPriority(task.getPriority());
            taskInfo.setTaskType(task.getFormKey());
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
            taskInfo.setProcessKey(processDefinition.getKey());
            taskInfos.add(taskInfo);
            String roleRule = ProcessExtensionAttributeUtils.getExtensionAttributeByTaskKey(repositoryService, processDefinition.getId(), task.getTaskDefinitionKey(), HmXMLConstants.PROPERTY_ROLE_SET);
            if (!ObjectCheckUtils.isEmptyString(roleRule)) {
                String[] strings = StringUtils.split(roleRule, ",");
                List<String> roles = Arrays.asList(strings);
                taskInfo.setRoleIds(roles);
            }
            String departmentId = ProcessExtensionAttributeUtils.getExtensionAttributeByTaskKey(repositoryService, processDefinition.getId(), task.getTaskDefinitionKey(), HmXMLConstants.PROPERTY_DEPARTMENT_SET);
            if (!ObjectCheckUtils.isEmptyString(departmentId)) {
                String[] strings = StringUtils.split(departmentId, ",");
                List<String> departments = Arrays.asList(strings);
                taskInfo.setDepartmentId(departments);
            }
            String specific = ProcessExtensionAttributeUtils.getExtensionAttributeByTaskKey(repositoryService, processDefinition.getId(), task.getTaskDefinitionKey(), HmXMLConstants.PROPERTY_USERTASK_DEPNAME);
            if (!ObjectCheckUtils.isEmptyString(specific)) {
                String[] specificsStrings = StringUtils.split(specific, ",");
                List<String> specifics = Arrays.asList(specificsStrings);
                taskInfo.setSpecificRoleIds(specifics);
            }
            String nodeBelong = ProcessExtensionAttributeUtils.getExtensionAttributeByTaskKey(repositoryService, processDefinition.getId(), task.getTaskDefinitionKey(), HmXMLConstants.NODE_BELONG);
           if(!ObjectCheckUtils.isEmptyString(nodeBelong)){
               taskInfo.setNodeBelong(Integer.parseInt(nodeBelong));
           }
            String bussiness = ProcessExtensionAttributeUtils.getExtensionAttributeByTaskKey(repositoryService, processDefinition.getId(), task.getTaskDefinitionKey(), HmXMLConstants.BUSINESS_STATE);
            taskInfo.setBusinessState(bussiness);
        }
        return taskInfos;
    }


    /**
     * 根据流程实例id获取流程下一个节点是否是知会节点
     *
     * @param processInstanceId
     */
    private void completeInformActivity(String processInstanceId) {
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (list != null && list.size() > 0) {
            //循环处理对应的历史人员
            for (Task task : list) {
                //知会节点
                if (SpecialNodeConstants.INFORM_NODE.equals(task.getFormKey())) {
                    if (task.getAssignee() != null) {
                        InformProcess informProcess = new InformProcess();
                        informProcess.setTaskId(task.getId());
                        informProcess.setProcessInstanceId(processInstanceId);
                        informProcess.setCreateTime(new Date());
                        informProcess.setInformPersonId(task.getAssignee());
                        informProcessDao.insert(informProcess);
                    }
                    taskService.complete(task.getId());
                    HandlerLog handlerLog = new HandlerLog();
                    handlerLog.setHandleUser(task.getAssignee());
                    handlerLog.setType(NodeHandleType.INFORM_NODE_COMPLETE);
                    handlerLog.setHandleTime(new Date());
                    NodeHandlerUtils.taskNodeHandleLog(runtimeService, task.getId(), handlerLog);
                }
            }
        }
    }


    /**
     * 判定流程是否结束
     *
     * @param taskId
     * @return
     */
    public Boolean isProcessStop(String taskId) {
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().taskId(taskId).list();
        if (!ObjectCheckUtils.isEmptyCollection(historicTaskInstances)) {
            List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processInstanceId(historicTaskInstances.get(0).getProcessInstanceId()).list();
            if (ObjectCheckUtils.isEmptyCollection(list)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 分页查询待办事项
     *
     * @param taskAPIData
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<TaskAPIData> TaskList(TaskAPIData taskAPIData, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TaskAPIData> list = taskDao.queryByUserIdAndSystemIdList(taskAPIData);
        PageInfo pageInfo = new PageInfo(list);
        if (list != null && list.size() > 0) {
            for (TaskAPIData taskData : list) {
                String processId = taskData.getProcessInstanceId();
                HistoricProcessInstance instance2 = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
                taskData.setProcessDefName(instance2.getProcessDefinitionName());
                String applayId = instance2.getStartUserId();
                taskData.setStartUserId(applayId);
            }
        }
        return pageInfo;
    }


    /**
     * 已完成
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<TaskAPIData> TaskCompletedList(TaskAPIData taskAPIDataParam, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        //TaskAPIData taskAPIData1 = new TaskAPIData();
        List<TaskAPIData> list = taskDao.queryByUserIdAndSystemIdChecked(taskAPIDataParam);
        PageInfo pageInfo = new PageInfo(list);
        list = pageInfo.getList();
        for (TaskAPIData taskAPIData : list) {
            //设置为可取回
            taskAPIData.setCanPick(historyCheckedTaskService.isPick(taskAPIData.getProcessInstanceId(), taskAPIData.getTaskId()));
            //设置结果
            if (taskAPIData.getStopTime() != null) {
                Object object = ProcessInstanceUtils.getHistoryVar(historyService, taskAPIData.getProcessInstanceId(), InnerActivitiVarConstants.HM_ACITVITI_PROCESS_RESULT);
                if (object instanceof Boolean) {
                    Boolean b = (Boolean) object;
                    taskAPIData.setResult(b);

                }
            }
        }
        return pageInfo;
    }


}
