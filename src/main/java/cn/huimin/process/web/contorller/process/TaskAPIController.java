package cn.huimin.process.web.contorller.process;

import cn.huimin.process.validate.TaskCompletedValidate;
import cn.huimin.process.validate.ValidateInterface;
import cn.huimin.process.web.dto.APIQuerySimpleResult;
import cn.huimin.process.web.dto.APISimpleResult;
import cn.huimin.process.web.dto.TaskCompleteAfterApi;
import cn.huimin.process.web.dto.UserTaskInfo;
import cn.huimin.process.web.model.CheckInfo;
import cn.huimin.process.web.model.TaskAPIData;
import cn.huimin.process.web.service.*;
import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.ExpectionUtils;
import cn.huimin.process.web.util.ExpectionUtils.ErrorInfo;
import cn.huimin.process.web.util.ObjectCheckUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wyp on 2017/3/9.
 */
@Controller
@RequestMapping("/openapi/task")
public class TaskAPIController {

    private static final transient Logger logger = LoggerFactory.getLogger(TaskAPIController.class);

    @Autowired
    private APIParameterCheck apiParameterCheck;

    @Autowired
    private TaskServiceEx taskServiceEx;
    @Autowired
    private ProcessServiceEx processServiceEx;
    @Autowired
    private UserTaskVarPoolService userTaskVarPoolService;
    @Autowired
    private TaskUrgeService taskUrgeService;
    @Autowired
    private HistoryCheckedTaskService historyCheckedTaskService;

    private static final String CTIME = "ctime";

    private static final String NONCE = "nonce";
    private static final String SIGN = "sign";





    /**
     * 查询流程待办事项
     *
     * @param taskAPIData
     * @param pageSize
     * @param pageNo
     * @return
     */
    @RequestMapping(value = "queryChecking", method = RequestMethod.POST)
    @ResponseBody
    public APIQuerySimpleResult queryChecking(TaskAPIData taskAPIData, @RequestParam(defaultValue = "40") Integer pageSize, @RequestParam(defaultValue = "1") Integer pageNo) {
        logger.warn("TaskAPIController.queryChecking.param {},{},{},", JSON.toJSONString(taskAPIData), pageNo, pageSize);
        APIQuerySimpleResult apiSimpleResult = new APIQuerySimpleResult();
        if(apiParameterCheck.commonParamCheck(taskAPIData.getSystemId())){
            apiSimpleResult.setResult(1);
            apiSimpleResult.setMessage("系统id为必需参数");
            return apiSimpleResult;
        }
        if(apiParameterCheck.commonParamCheck(taskAPIData.getHanderId())){
            apiSimpleResult.setResult(1);
            apiSimpleResult.setMessage("处理人id为必须参数");
            return apiSimpleResult;
        }
        PageInfo<TaskAPIData> pageInfo = taskServiceEx.TaskList(taskAPIData, pageNo, pageSize);
        apiSimpleResult.setData(pageInfo.getList());
        apiSimpleResult.setResult(0);
        apiSimpleResult.setTotalPage(pageInfo.getPages());
        apiSimpleResult.setTotalCount(pageInfo.getTotal());
        apiSimpleResult.setMessage("请求待办事项成功");
        logger.info("TaskAPIController.queryChecking.result {}", apiSimpleResult);
        return apiSimpleResult;
    }


    /**
     * 查询已办
     * @param taskAPIData
     * @param sTime 流程开始时间
     * @param eTime 流程结束时间
     * @param pageSize
     * @param pageNo
     * @param request
     * @return
     */
    @RequestMapping(value = "queryChecked", method = RequestMethod.POST)
    @ResponseBody
    public APIQuerySimpleResult queryChecked(TaskAPIData taskAPIData,Long sTime,Long eTime, @RequestParam(defaultValue = "40") Integer pageSize, @RequestParam(defaultValue = "1") Integer pageNo, String type,HttpServletRequest request) {
        logger.warn("TaskAPIController.queryChecked.param {},{},{}", taskAPIData.toString(), pageNo, pageSize);
        APIQuerySimpleResult apiSimpleResult = new APIQuerySimpleResult();
        if(sTime!=null){
            taskAPIData.setApplyTime(DateUtils.timeToDate(sTime));
        }
        if(eTime!=null){
            taskAPIData.setStopTime(DateUtils.timeToDate(eTime));
        }
        if(apiParameterCheck.commonParamCheck(taskAPIData.getSystemId())){
            apiSimpleResult.setResult(1);
            apiSimpleResult.setMessage("系统id为必需参数");
            return apiSimpleResult;
        }
        if(apiParameterCheck.commonParamCheck(taskAPIData.getHanderId())){
            apiSimpleResult.setResult(1);
            apiSimpleResult.setMessage("处理人id为必须参数");
            return apiSimpleResult;
        }
        PageInfo<TaskAPIData> pageInfo = null;
        //不去掉重复的审批
        if("1".equals(type)){
            pageInfo = taskServiceEx.TaskCompletedList(taskAPIData, pageNo, pageSize);
        }
        //去掉重复审批的
        if("2".equals(type)){
            pageInfo = taskServiceEx.taskDistinctCompletedList(taskAPIData, pageNo, pageSize);
        }
        apiSimpleResult.setData(pageInfo.getList());
        apiSimpleResult.setResult(0);
        apiSimpleResult.setTotalPage(pageInfo.getPages());
        apiSimpleResult.setTotalCount(pageInfo.getTotal());
        apiSimpleResult.setMessage("请求已办事项成功");
        logger.info("TaskAPIController.queryChecked.result {}", apiSimpleResult.getData());
        return apiSimpleResult;
    }

    /**
     * 根据流程实例获取流程详情
     *
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "queryTaskInfoByProcessInstanceId", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult queryTaskInfoByProcessInstanceId(String processInstanceId) {
        logger.warn("TaskAPIController.queryTaskInfoByProcessInstanceId  param {}");
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (ObjectCheckUtils.isEmptyString(processInstanceId)) {
            return this.simpleResult(apiSimpleResult, "processInstanceId");
        }
        List<UserTaskInfo> list = taskServiceEx.queryTaskInfoByProcessInstanceId(processInstanceId);
        apiSimpleResult.setData(list);
        apiSimpleResult.setResult(0);
        apiSimpleResult.setMessage("查看当前任务节点详情完成");
        return apiSimpleResult;
    }


    /**
     * 完成任务
     *
     * @param systemId
     * @param userId
     * @param parameter
     * @return
     */
    @RequestMapping(value = "complete", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult complete(String systemId, String userId, String taskId, String parameter, HttpServletRequest request) {
        logger.warn("TaskAPIController.complete.param {},{},{},{}", systemId, userId, taskId, parameter);
        APISimpleResult simpleResult = new APISimpleResult();
        if(apiParameterCheck.commonParamCheck(systemId)){
            return this.simpleResult(simpleResult,"systemId");
        }
        if(apiParameterCheck.commonParamCheck(userId)){
            return this.simpleResult(simpleResult,"审核人userId");

        }
        if(apiParameterCheck.commonParamCheck(taskId)){
            return this.simpleResult(simpleResult,"taskId");
        }

        //执行校验器
        ValidateInterface validateInterface = validateParams(request, new TaskCompletedValidate());
        if (!validateInterface.validateParms()) {
            return simpleResult(simpleResult, validateInterface.getErrorMessage());
        }
        Map<String, Object> map = validateInterface.getParms();
        try {
            //正常流转
            String processsInstanceId = taskServiceEx.doTask(systemId, userId, taskId, new JSONObject(map));
            simpleResult.setMessage("任务节点完成");
            simpleResult.setResult(0);
            TaskCompleteAfterApi taskCompleteAfterApi = new TaskCompleteAfterApi();
            Boolean processStop = taskServiceEx.isProcessStop(taskId);
            taskCompleteAfterApi.setProcessIsStop(processStop);
            if(processStop!=null){
                taskCompleteAfterApi.setAfterTaskInfo(taskServiceEx.queryTaskInfoByProcessInstanceId(processsInstanceId));
            }
            simpleResult.setData(taskCompleteAfterApi);
        } catch (RuntimeException e) {
        	ErrorInfo errorInfo = ExpectionUtils.getConditionErrorMsg(e);
        	simpleResult.setErrorMsg(errorInfo.errorMsg);
        	simpleResult.setErrorCode(errorInfo.errorCode);
            simpleResult.setMessage("任务节点完成失败");
            simpleResult.setResult(1);
            e.printStackTrace();
            logger.error("TaskAPIController.complete {}", e);
        } finally {
            return simpleResult;
        }
    }


    /**
     * 撤回已经审批过的流程
     * @param taskId
     * @param processInstanceId
     * @param systemId
     * @return
     */
    @RequestMapping("pickTaskCompleted")
    @ResponseBody
    public APISimpleResult pickTaskCompleted(String taskId, String processInstanceId, String userId,String systemId){
        logger.warn("TaskAPIController.pickTaskCompleted.param {},{},{}",systemId,processInstanceId,taskId);
       APISimpleResult simpleResult = new APISimpleResult();
        if(apiParameterCheck.commonParamCheck(systemId)){
            return this.simpleResult(simpleResult,"systemId");
        }
        if(apiParameterCheck.commonParamCheck(processInstanceId)){
            return this.simpleResult(simpleResult,"流程实例processInstanceId");
        }
        if(apiParameterCheck.commonParamCheck(taskId)){
            return this.simpleResult(simpleResult,"taskId");
        }
        try {
            historyCheckedTaskService.doPick(processInstanceId,taskId,userId);
            simpleResult.setMessage("已办撤销成功");
            simpleResult.setResult(0);
        } catch (Exception e) {
            simpleResult.setMessage("已办撤销失败");
            simpleResult.setResult(1);
            e.printStackTrace();
        }finally {
            return simpleResult;
        }

    }




    @RequestMapping(value = "processStop",method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult processStop(String systemId, String userId,String taskId,String remark ){
        logger.warn("TaskAPIController.processStop.param {},{},{},{}",systemId,userId,taskId,remark);
        APISimpleResult simpleResult = new APISimpleResult();
        if(apiParameterCheck.commonParamCheck(systemId)){
            return this.simpleResult(simpleResult,"systemId");
        }
        if(apiParameterCheck.commonParamCheck(userId)){
            return this.simpleResult(simpleResult,"审核人userId");

        }
        if(apiParameterCheck.commonParamCheck(taskId)){
            return this.simpleResult(simpleResult,"taskId");
        }
        try {
            //正常流转
            processServiceEx.processInstanceDelete(userId,taskId,remark,"2");
            simpleResult.setMessage("流程终止");
            simpleResult.setResult(0);
        }catch (RuntimeException e){
            simpleResult.setMessage("流程终止失败");
            simpleResult.setResult(1);
            logger.error("TaskAPIController.complete {}",e);
        }finally {
            return simpleResult;
        }
    }




    /**
     * 任务催办
     * @param systemId
     * @param processInstanceId
     * @param userId
     * @return
     */
    @RequestMapping(value = "taskUrge",method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult taskUrge(String systemId,String processInstanceId,String userId){
        logger.warn("TaskAPIController.taskUrge.param {},{},{}",systemId,processInstanceId,userId);
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if(apiParameterCheck.commonParamCheck(systemId)){
            return this.simpleResult(apiSimpleResult,"systemId");
        }
        if(apiParameterCheck.commonParamCheck(processInstanceId)){
            return this.simpleResult(apiSimpleResult,"processInstanceId");
        }
        if(apiParameterCheck.commonParamCheck(userId)){
            return this.simpleResult(apiSimpleResult,"userId");
        }
            logger.info("=======taskUrge start ======= ");
            logger.info("processInstanceId {},userId {} ",processInstanceId,userId);
            try {
                taskUrgeService.urge(processInstanceId);
                apiSimpleResult.setResult(0);
                apiSimpleResult.setMessage("催办成功");
            }catch (RuntimeException e){
                apiSimpleResult.setMessage("催办失败");
                apiSimpleResult.setResult(1);
                logger.error("TaskAPIController.taskUrge {}",e);
            }finally {
                logger.info("=======taskUrge end ======= ");
                return apiSimpleResult;
            }

    }

    /**
     * 获取任务节点对应的变量接口
     * @param systemId
     * @param taskKey
     * @param processDefId
     * @return
     */
    @RequestMapping(value = "queryTaskVar",method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult queryTaskVar(String systemId,String taskKey,String processDefId){
        logger.warn("TaskAPIController.queryTaskVar.param {},{},{}",systemId,taskKey,processDefId);
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if(apiParameterCheck.commonParamCheck(systemId)){
            return this.simpleResult(apiSimpleResult,"systemId");
        }
        if(apiParameterCheck.commonParamCheck(taskKey)){
            return this.simpleResult(apiSimpleResult,"processDefId");

        }
        if(apiParameterCheck.commonParamCheck(processDefId)){
            return this.simpleResult(apiSimpleResult,"processDefId");
        }
        JSONArray jsonObject = userTaskVarPoolService.getUserTaskVarByProcessKeyAndTaskKey(processDefId,taskKey);
        apiSimpleResult.setMessage("查询任务节点所需变量成功");
        apiSimpleResult.setResult(0);
        apiSimpleResult.setData(jsonObject);
        logger.info("TaskAPIController.queryTaskVar.result {}",apiSimpleResult);
        return apiSimpleResult;
    }
    /**
     * 获取任务节点表单字段和属性
     * @param systemId
     * @param taskKey
     * @param processDefId
     * @return
     */
    @RequestMapping(value = "queryFormProperties",method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult queryFormProperties(String systemId,String taskKey,String processDefId){
        logger.warn("TaskAPIController.queryTaskVar.param {},{},{}",systemId,taskKey,processDefId);
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if(apiParameterCheck.commonParamCheck(systemId)){
            return this.simpleResult(apiSimpleResult,"systemId");
        }
        if(apiParameterCheck.commonParamCheck(taskKey)){
            return this.simpleResult(apiSimpleResult,"taskKey");

        }
        if(apiParameterCheck.commonParamCheck(processDefId)){
            return this.simpleResult(apiSimpleResult,"processDefId");
        }
        JSONArray jsonArray = userTaskVarPoolService.getFormPropertiesVarByProcessKeyAndTaskKey(processDefId,taskKey);
        apiSimpleResult.setMessage("查询任务节点所需表单属性成功");
        apiSimpleResult.setResult(0);
        apiSimpleResult.setData(jsonArray);
        logger.info("TaskAPIController.queryTaskVar.result {}",apiSimpleResult);
        return apiSimpleResult;
    }







    /**
     * 为了拼接参数
     * @param apiSimpleResult
     * @return
     */
    private APISimpleResult simpleResult(APISimpleResult apiSimpleResult,String name){
        String s  = "必需参数";
        apiSimpleResult.setMessage(name+s);
        apiSimpleResult.setResult(1);
        return apiSimpleResult;
    }


    /**
     * 完成任务
     *
     * @param systemId
     * @param userId
     * @param parameter
     * @return
     */
    @RequestMapping(value = "backToStartUserId", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult backToStartUserId(String systemId, CheckInfo checkInfo, String userId, String taskId, String parameter, HttpServletRequest request) {
        logger.warn("TaskAPIController.complete.param {},{},{},{}", systemId, userId, taskId, parameter);
        APISimpleResult simpleResult = new APISimpleResult();
        if(apiParameterCheck.commonParamCheck(systemId)){
            return this.simpleResult(simpleResult,"systemId");
        }
        if(apiParameterCheck.commonParamCheck(userId)){
            return this.simpleResult(simpleResult,"审核人userId");

        }
        if(apiParameterCheck.commonParamCheck(taskId)){
            return this.simpleResult(simpleResult,"taskId");
        }
        try {
            List<UserTaskInfo> userTaskInfoList = taskServiceEx.handleSkipNode(systemId, userId, taskId, checkInfo,"1");
            simpleResult.setMessage("节点回退到发起人成功");
            simpleResult.setResult(0);
            simpleResult.setData(userTaskInfoList);
        }catch (NullPointerException n){
            simpleResult.setErrorMsg(n.getMessage());
            simpleResult.setErrorCode("10003");
            simpleResult.setMessage("任务节点完成失败");
            simpleResult.setResult(1);
            n.printStackTrace();
        } catch (RuntimeException e) {
            simpleResult.setMessage("回退到发起人失败");
            simpleResult.setResult(1);
            logger.error("TaskAPIController。backToStartUserId {}", e);
        } finally {
            return simpleResult;
        }
    }

    /**
     * 校验器
     * @param request
     * @param validateInterface
     * @return
     */
    private ValidateInterface validateParams(HttpServletRequest request, ValidateInterface validateInterface){
        Map<String,String[]> map = request.getParameterMap();
        Map<String,String> params = new HashMap<>();
        for(String key : map.keySet()){
            String[] values = map.get(key);
            if(!CTIME.equals(key)&&!SIGN.equals(key)&&!NONCE.equals(key)){
                params.put(key,values[0]);
            }

        }
        //校验
        validateInterface.setParms(params);
        return validateInterface;
    }









}
