package cn.huimin.process.web.service.imp;

import cn.huimin.process.activiticmd.DeleteRunningTaskCmd;
import cn.huimin.process.activiticmd.StartActivityCmd;

import cn.huimin.process.web.dao.ActCreationDaoEx;
import cn.huimin.process.createactivity.DefaultTaskFlowControlService;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.ActCreationEx;
import cn.huimin.process.web.model.Employeerole;
import cn.huimin.process.web.service.ActCreationService;
import cn.huimin.process.web.service.TempActivityChangeService;
import cn.huimin.process.web.util.EhrRequestApiUtils;
import cn.huimin.process.web.util.EhrUserDataHandleUtils;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *临时签
 */
@Service
public class TempActivityChangeServiceImpl implements TempActivityChangeService {

    @Autowired
    private ActCreationService actCreationService;


    @Value("${getRoleByUserId}")
    private String getRoleByUserId;



    @Autowired
    private TaskService taskService;
    @Autowired
    private ActCreationDaoEx actCreationDaoEx;
    //串行节点创建的工厂
    private final String factoryName = "cn.huimin.process.createactivity.ChainedActivitiesCreator";
    //并行节点创建的工厂
    private final String factoryName2 = "cn.huimin.process.createactivity.ParallelActivitiesCreator";

    //会签节点创建的工程
    private final String factoryName3 = "cn.huimin.process.createactivity.MultiInstanceActivityCreator";
    @Autowired
    private RepositoryService repositoryService;




    /**
     * 加签功能
     * @param taskId
     * @param processId
     * @param userCodes
     * @throws Exception
     */
    @Transactional
    public void addActivity(String taskId,String processId, String userCodes,String activityName,String doUserId) throws Exception{
        DefaultTaskFlowControlService defaultTaskFlowControlService = new DefaultTaskFlowControlService(actCreationService,
                processEngine, processId);
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).active().processInstanceId(processId).singleResult();
        taskService.setAssignee(taskId,doUserId);
        activityName = task.getName()+","+activityName;
        String[] assos = userCodes.split(",");
        String[] activityNames = activityName.split(",");
        //添加功能
        defaultTaskFlowControlService.insertTasksAfter(task.getTaskDefinitionKey(),taskId,doUserId,activityNames,assos);


    }



    /**
     * 为前往查看临时加签的做数据准备
     * @param id
     * @return
     */
    public JSONObject queryTempActData(Integer id){
        JSONObject jsonObject = new JSONObject();
        ActCreationEx actCreationEx = actCreationDaoEx.queryById(id);
        //串行
        if(actCreationEx.getFactoryName().equals(factoryName)){
            jsonObject.put("type",1);
        }
        //并行
        if(actCreationEx.getFactoryName().equals(factoryName2)){
            jsonObject.put("type",2);
        }
        String properties = actCreationEx.getProcessText();

        JSONObject jsonProperties =JSON.parseObject(properties);
        //员工信息
        JSONArray jsonAssignees =jsonProperties.getJSONArray("assignees");
        //会签名称
        JSONArray jsonActivityNames =jsonProperties.getJSONArray("activityNames");
        JSONArray jsonArray= new JSONArray();
        for(int i=1;i<jsonAssignees.size();i++){
            String userId = jsonAssignees.getString(i);
            JSONArray jsonArray1 = EhrRequestApiUtils.getUserInfoByUserId(userId,getRoleByUserId);
            if(jsonArray1.size()!=0){
               Employeerole employee= EhrUserDataHandleUtils.handleOnlyUserData(jsonArray1.getJSONObject(0));
                employee.setRemark(jsonActivityNames.getString(i));
                jsonArray.add(employee);
            }

        }
        jsonObject.put("employees",jsonArray);
        return jsonObject;
    }




    /**
     * 剪掉临时加的签
     * @param processId
     * @param taskId
     * @param doUserId
     */

    @Transactional
    public SimpleResult cutCurrentActivity(String processId, String taskId, String doUserId){
        SimpleResult simpleResult = new SimpleResult();
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).processInstanceId(processId).singleResult();
        ActCreationEx actCreation = new ActCreationEx();
        actCreation.setDoUserId(doUserId);
        actCreation.setProcessInstanceId(processId);
        actCreation.setFactoryName(factoryName);
        //查询是并签还是串行签
        List<ActCreationEx> list = actCreationDaoEx.query(actCreation);
        //第一次查询
        if(list==null || list.size()==0){
            actCreation.setFactoryName(factoryName2);
            list.addAll(actCreationDaoEx.query(actCreation));
        }else {
            actCreation.setFactoryName(factoryName2);
            list.addAll(actCreationDaoEx.query(actCreation));
        }
        //这里就保证只有加并签和串行的签可以收回
        if(list==null || list.size()==0){
            simpleResult.setMessage("不支持减签");
            simpleResult.setSuccess(false);
            return simpleResult;
        }
        for(ActCreationEx actCreation1:list){
           String properties = actCreation1.getProcessText();
             JSONObject jsonObject =JSON.parseObject(properties);
           JSONArray jsonArray = jsonObject.getJSONArray("cloneActivityIds");
             if(jsonArray.getString(0).equals(task.getTaskDefinitionKey())){
                ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionId(actCreation1.getProcessDefinitionId()).singleResult();
                //变动前的活动节点
                 ActivityImpl prototypeActivity = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
                         actCreation1.getActId());

                 ActivityImpl prototypeActivity2 = ProcessDefinitionUtils.getActivity(processEngine, processDefinition.getId(),
                         task.getTaskDefinitionKey());
                 List<PvmTransition> list1 =prototypeActivity2.getOutgoingTransitions();
                 for(PvmTransition pvmTransition:list1){
                        pvmTransition.getDestination();
                 }
                 prototypeActivity2.getOutgoingTransitions().clear();
                 prototypeActivity2.createOutgoingTransition().setDestination(prototypeActivity);
                 RuntimeServiceImpl runtimeService = (RuntimeServiceImpl) processEngine.getRuntimeService();
                 runtimeService.getCommandExecutor().execute(new DeleteRunningTaskCmd(task));
                 runtimeService.getCommandExecutor().execute(new StartActivityCmd(task.getExecutionId(), prototypeActivity));
                 //List<ProcessInstance> list2=  runtimeService.createProcessInstanceQuery().processInstanceId(processId).active().list();
                 //这里代表找到对应的节点删除掉
                  actCreationDaoEx.delete(actCreation1.getId());
                 Task task1 =   taskService.createTaskQuery().processInstanceId(processId).active().singleResult();
                 if(task1!=null){
                     //临时借用的任务id
                     simpleResult.setTaskId(task1.getId());
                 }
                 simpleResult.setSuccess(true);
                 simpleResult.setMessage("减签成功");
             }else {
                 simpleResult.setSuccess(false);
                 simpleResult.setMessage("没有可减的签");
             }
        }
     return  simpleResult;
    }

    /**
     * 判定是否已经加过签 并且获取该临时签的id
     * @param pricessId
     * @param taskId
     * @param doUserId
     * @return
     */
    public SimpleResult isAddActivity(String pricessId,String taskId,String doUserId){
        SimpleResult simpleResult = new SimpleResult();
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).processInstanceId(pricessId).singleResult();
        ActCreationEx actCreation = new ActCreationEx();
        actCreation.setDoUserId(doUserId);
        actCreation.setProcessInstanceId(pricessId);
       List<ActCreationEx> actCreations = actCreationDaoEx.query(actCreation);
        //可以加签
        if(actCreations==null || actCreations.size()==0){
            simpleResult.setSuccess(true);
            return simpleResult;
        }
        for(ActCreationEx actCreationEx :actCreations){
            if(factoryName3.equals(actCreationEx.getFactoryName())){
                continue;
            }
            String properties = actCreationEx.getProcessText();
            JSONObject jsonObject =JSON.parseObject(properties);
            JSONArray jsonArray = jsonObject.getJSONArray("cloneActivityIds");
            //如果有相同的话就
            if(jsonArray.getString(0).equals(task.getTaskDefinitionKey())){
                //临时充当新加的id
                simpleResult.setProcessId(String.valueOf(actCreationEx.getId()));
                simpleResult.setSuccess(false);
                return simpleResult;
            }
        }
        simpleResult.setSuccess(true);
        return simpleResult;
    }




    /**
     * 加签会签
     * @param taskId
     * @param processId
     * @param user
     * @throws Exception
     */
    @Transactional
    public void addJoinActivity(String taskId, String processId,String user,String activityName,String doUserId) throws Exception{
        DefaultTaskFlowControlService defaultTaskFlowControlService = new DefaultTaskFlowControlService(actCreationService,
                processEngine, processId);
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).taskId(taskId).active().processInstanceId(processId).singleResult();
        activityName = task.getName()+","+activityName;
        String[] assos = user.split(",");
        String[] activityNames = activityName.split(",");
        //添加功能
        defaultTaskFlowControlService.insertTasksAfterPara(task.getTaskDefinitionKey(),taskId,doUserId,activityNames,assos);

    }


    @Autowired
    private  ProcessEngine processEngine;
    /**
     * 当前节点变为会签
     *
     * @param taskId    当前任务ID
     * @param userCodes 会签人集合
     * @throws Exception
     */
    @Transactional
    public void jointProcess(String taskId,String processId, String userCodes) throws Exception{
        DefaultTaskFlowControlService defaultTaskFlowControlService = new DefaultTaskFlowControlService( actCreationService,
                processEngine, processId);
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).processInstanceId(processId).singleResult();
       // taskService.setAssignee(taskId,null);
        String[] assos = userCodes.split(",");
        defaultTaskFlowControlService.split(task.getTaskDefinitionKey(),false,assos[assos.length-1],assos);
        List<Task> tasks =taskService.createTaskQuery().processInstanceId(processId).list();
        for(int i=0;i<tasks.size();i++){
            //最后面的为当前用户
             List<IdentityLink> links= taskService.getIdentityLinksForTask(tasks.get(i).getId());
            for(int j=0;j<links.size();j++){
               IdentityLink identityLink =  links.get(j);
               String groupid = identityLink.getGroupId();
                if(groupid!=null&& !groupid.equals("")){
                    taskService.deleteCandidateGroup(tasks.get(i).getId(),groupid);
                }
                String userId = identityLink.getUserId();
                if(userId!=null && !userId.equals("")){
                    taskService.deleteCandidateUser(tasks.get(i).getId(),userId);
                }
            }
            //taskService.addCandidateUser(tasks.get(i).getId(),assos[i]);
            taskService.setAssignee(tasks.get(i).getId(),assos[i]);
            //设置为拥有着
            if(i == assos.length-1){
                //执行当前任务为所属操作
                taskService.setOwner(tasks.get(i).getId(),assos[i]);
            }
        }
    }
}
