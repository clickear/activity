package cn.huimin.process.web.service.imp;

import cn.huimin.process.activiticmd.ExecuteCustomeCmd;
import cn.huimin.process.activiticmd.SetProcessInstanceStateExCmd;
import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.InnerBusinessVarConstants;
import cn.huimin.process.core.SpecialNodeConstants;
import cn.huimin.process.core.pvm.ProcessExtensionAttributeUtils;
import cn.huimin.process.web.contorller.process.APIConstants;
import cn.huimin.process.web.dao.ProcessDataDao;
import cn.huimin.process.web.dao.ProcessPriorityDao;
import cn.huimin.process.web.dto.*;
import cn.huimin.process.web.model.ProcessData;
import cn.huimin.process.web.model.ProcessPriority;
import cn.huimin.process.web.service.ProcessServiceEx;
import cn.huimin.process.web.service.TaskServiceEx;
import cn.huimin.process.web.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.runtime.ProcessInstanceBuilderImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by wyp on 2017/3/10.
 */
@Service
public class ProcessServiceExImpl implements ProcessServiceEx {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;
    @Autowired
    private ProcessDataDao processDataDao;

    @Value("${urgeTime}")
    private Integer urgeTime;

    @Value("${urgeBase}")
    private Integer urgeBase;

    @Autowired
    private ProcessPriorityDao processPriorityDao;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskServiceEx taskServiceEx;




    /**
     * 启动必须参数
     * @param parms
     * @param startId
     * @param key
     */
    @Transactional
    @Override
     public APISimpleResult start(Map<String, Object> parms, String startId, String key, String systemId,APISimpleResult apiSimpleResult){
        //流程发起
        Authentication.setAuthenticatedUserId(startId);
        //构建流程实例
        ProcessInstanceBuilderImpl processInstanceBuilder = (ProcessInstanceBuilderImpl) runtimeService.createProcessInstanceBuilder();
        processInstanceBuilder.processDefinitionKey(key);
        processInstanceBuilder.tenantId(systemId);
        String bussinessKey =UUIDutils.createUUID();
        if(parms.get(InnerBusinessVarConstants.BUSINESS_KEY)!= null){
            bussinessKey =String.valueOf(parms.get(InnerBusinessVarConstants.BUSINESS_KEY));
            processInstanceBuilder.businessKey(bussinessKey);
        }
        processInstanceBuilder.businessKey(bussinessKey);
        if(parms.get(InnerBusinessVarConstants.PROCESS_INSTANCE_NAME)!=null){
            processInstanceBuilder.processInstanceName(String.valueOf(parms.get(InnerBusinessVarConstants.PROCESS_INSTANCE_NAME)));
        }
        Object branchId =  parms.get(InnerBusinessVarConstants.BRANCH_ID);
        if(branchId!=null){
            parms.put(InnerActivitiVarConstants.HM_ACTIVITI_START_BRANCH_ID,branchId);
        }
        Object departmentId =parms.get(InnerBusinessVarConstants.DEPARTMENT_ID);
        if(departmentId!=null){
            parms.put(InnerActivitiVarConstants.HM_ACTIVITI_START_DEPARTMENT_ID,departmentId);
        }
        processInstanceBuilder.getVariables().putAll(parms);
       /* for(String paramKey:parms.keySet()){
            processInstanceBuilder.addVariable(paramKey,parms.get(paramKey));
        }*/
        ProcessInstance processInstance =processInstanceBuilder.start();
        Authentication.setAuthenticatedUserId(null);
        //新增状态
        if("1".equals(systemId)){
            this.insertProcessPriority(processInstance.getProcessInstanceId());
        }
        //自动跳过表单提交
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();
        //如果1的代表设置的发起人节点
        if(tasks!=null && tasks.size()>0){
            Task task = tasks.get(0);
            if(SpecialNodeConstants.START_NODE.equals(task.getFormKey())){
                taskService.setAssignee(task.getId(),startId);
                taskService.complete(task.getId());
                HandlerLog handlerLog = new HandlerLog();
                handlerLog.setType(NodeHandleType.NODE_SUBMIT);
                handlerLog.setHandleTime(DateUtils.now());
                handlerLog.setHandleUser(startId);
                NodeHandlerUtils.taskNodeHandleLog(runtimeService,task.getId(),handlerLog);
            }
        }
        //流转中
        //初始化流转
        ProcessData processData = processDataDao.getProcessInstanceInfoByProcessInstanceId(processInstance.getProcessInstanceId());
        List<UserTaskInfo> list = taskServiceEx.queryTaskInfoByProcessInstanceId(processInstance.getProcessInstanceId());
        if(list!=null&&list.size()>0){
            ExecuteCustomeCmd.execute(runtimeService,new SetProcessInstanceStateExCmd(processInstance.getId(),0));
        }

        //查询信息用的
        StartProcessInfo startProcessInfo = new StartProcessInfo(processData,list);
        apiSimpleResult.setData(startProcessInfo);
        //查询流程实例信息
        return apiSimpleResult;
    }

    /**
     * 新增流程处理时间状态
     * @param processInstanceId
     */
    private void insertProcessPriority(String processInstanceId){
        ProcessPriority processPriority = new ProcessPriority();
        processPriority.setProcInstId(processInstanceId);
        processPriority.setHandTime(urgeTime);
        processPriority.setPriority(urgeBase);
        processPriorityDao.insertProcessPriority(processPriority);

    }

    /**
     * 通过流程实例获取流程表单详情
     * @param processInstanceId
     * @return
     */
    public  Map<String, Object> getFormInfoByProcessInstanceId(String processInstanceId){
        Map<String, Object> map = runtimeService.getVariables(processInstanceId);

        if(map == null){
            return null;
        }
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> stringObjectEntry = iterator.next();
            Field[] fields = InnerBusinessVarConstants.class.getDeclaredFields();
            for(int i=0;i<fields.length;i++){
                try {
                    String key = String.valueOf(fields[i].get(stringObjectEntry.getKey()));
                    if (stringObjectEntry.getKey().equals(key)){
                        iterator.remove();
                        break;
                    }
                } catch (IllegalAccessException e) {

                    e.printStackTrace();
                }
            }
            Field[] fis = InnerActivitiVarConstants.class.getDeclaredFields();
            for(int j=0;j<fis.length;j++){
                try {
                    String key = String.valueOf(fis[j].get(stringObjectEntry.getKey()));
                    if (stringObjectEntry.getKey().equals(key)){
                        iterator.remove();
                        break;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return map;
    }

    /**
     * 对人员信息进行校验
     * @param key
     * @param userId
     * @param roleId
     * @return
     */
    public void processCheck(String key, String userId, String roleId,APISimpleResult simpleResult) throws Exception{
        ProcessDefinition processDefinition = ProcessDefinitionUtils.getProcessDefinitionByKey(repositoryService,key);
        boolean flag = false;
        if(processDefinition!=null){
            List<String> list = ProcessExtensionAttributeUtils.getProcessDefExtensionAttribute(repositoryService, processDefinition.getId(), HmXMLConstants.CANDIDATE_STARTER_USERS);
            List<String> list2 = ProcessExtensionAttributeUtils.getProcessDefExtensionAttribute(repositoryService, processDefinition.getId(), HmXMLConstants.CANDIDATE_STARTER_GROUP);
           if(ObjectCheckUtils.isEmptyCollection(list)&&ObjectCheckUtils.isEmptyCollection(list2)){
               simpleResult.setResult(1);
               simpleResult.setErrorMsg("未给流程配置相应的岗位或人");
               return;
           }
            //如果
            if(!ObjectCheckUtils.isEmptyString(userId)){
                if(list.contains(userId)){
                    flag = true;
                }
            }
            if(!ObjectCheckUtils.isEmptyString(roleId)){
                if(list2.contains(roleId)){
                    flag = true;
                }
            }
            if(!ObjectCheckUtils.isEmptyString(userId)&&!ObjectCheckUtils.isEmptyString(roleId)){
                if(list.contains(userId)&&list2.contains(roleId)){
                    flag = true;
                }else {
                    flag = false;
                }
            }
        }
        simpleResult.setResult(0);
        simpleResult.setMessage("校验成功");
        simpleResult.setData(flag);
    }

    /**
     * 系统删除操作
     * @param systemId
     * @param userId
     */
    @Override
    @Transactional
    public  void processInstanceDeleteBySystemId(String systemId, String userId, String processInstanceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("systemId",systemId);
        jsonObject.put("userId",userId);
        jsonObject.put(APIConstants.BUSINESS_DELETE_RESON_KEY,APIConstants.BUSINESS_DELETE_RESON_VAlUE);
        runtimeService.deleteProcessInstance(processInstanceId,jsonObject.toString());
    }


    /**
     * 流程被驳回
     * @param processInstanceId
     * @param jsonObject 驳回信息
     */
    @Override
    @Transactional
    public  void processInstanceDeleteByReject(String processInstanceId,String taskId,String userId,JSONObject jsonObject) {
        taskService.setAssignee(taskId,userId);
        runtimeService.deleteProcessInstance(processInstanceId,jsonObject.toString());
        HandlerLog handlerLog = new HandlerLog();
        handlerLog.setHandleUser(userId);
        handlerLog.setType(NodeHandleType.NODE_NO_PASS);
        handlerLog.setHandleTime(DateUtils.now());
        //多实例有问题
        NodeHandlerUtils.taskNodeHandleLog(runtimeService,taskId,handlerLog);
    }

    @Override
    public boolean isCanProcessInstanceDeleteBySystemId(String processInstanceId, String systemId) {
       List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskTenantId(systemId).finished().list();
       if(historicTaskInstanceList!=null&&historicTaskInstanceList.size()==0){
            return  true;
       }
        if(historicTaskInstanceList==null){
            return  true;
        }
       //如果长度等于
        if(historicTaskInstanceList!=null&&historicTaskInstanceList.size()==1){
            //formKey等于1代表是发起人的节点是可以撤回的
           if("1".equals(historicTaskInstanceList.get(0).getFormKey())){
                return true;
            }
        }
        return false;
    }


    /**
     * 流程实例终止，这个是处理当前任务节点处理的方式
     * @param userId
     * @param taskId
     */
    @Override
    @Transactional
    public void processInstanceDelete(String userId, String taskId,String remark,String type) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
       /* taskService.setAssignee(taskId,userId);
        taskService.complete(taskId);*/
        HandlerLog handlerLog = new HandlerLog();
        handlerLog.setHandleTime(DateUtils.now());
        handlerLog.setHandleUser(EhrUserDataHandleUtils.employeeInfo(userId));
        //如果1 代表是不通过结束流程
        if("1".equals(type)){
            handlerLog.setType(NodeHandleType.NODE_NO_PASS);
        }
        //2代表终止流程
        if("2".equals(type)){
            handlerLog.setType(NodeHandleType.PROCESS_DELETE);
        }
        taskService.setVariable(taskId,InnerActivitiVarConstants.HM_ACITVITI_PROCESS_RESULT,false);
        runtimeService.deleteProcessInstance(processInstanceId,JSON.toJSONString(handlerLog));

    }

    /**
     * 查询已发起流程
     * @param processData
     * @param start
     * @param size
     * @return
     */
    @Override
    public PageInfo<ProcessData> queryProcessInstancesByStarterId(ProcessData processData, Integer start, Integer size) {
        PageHelper.startPage(start,size);
        List<ProcessData> list = processDataDao.queryStartedProcess(processData);
        PageInfo<ProcessData> pageInfo = new PageInfo<ProcessData>(list);
        return pageInfo;
    }

    /**
     * 流程挂起
     * @param processInstanceId
     * @return
     */
    @Override
    @Transactional
    public void processSuppend(String processInstanceId){
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 流程挂起
     * @param processInstanceId
     */
    @Override
    @Transactional
    public void processActive(String processInstanceId) {
        runtimeService.activateProcessInstanceById(processInstanceId);
    }


}
