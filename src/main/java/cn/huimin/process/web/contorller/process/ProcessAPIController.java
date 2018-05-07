package cn.huimin.process.web.contorller.process;

import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.InnerBusinessVarConstants;
import cn.huimin.process.validate.StartProcessValidate;
import cn.huimin.process.validate.TaskCompletedValidate;
import cn.huimin.process.validate.ValidateInterface;
import cn.huimin.process.web.dto.APIQuerySimpleResult;
import cn.huimin.process.web.dto.APISimpleResult;
import cn.huimin.process.web.dto.UserTaskNodeInfo;
import cn.huimin.process.web.model.ProcessData;
import cn.huimin.process.web.model.ProcessDefEntity;
import cn.huimin.process.web.model.ProcessType;
import cn.huimin.process.web.model.SystemEntity;
import cn.huimin.process.web.service.*;
import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.ExpectionUtils;
import cn.huimin.process.web.util.ExpectionUtils.ErrorInfo;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wyp on 2017/3/9.
 */
@RequestMapping("/openapi/process")
@Controller
public class ProcessAPIController {

    @Autowired
    private SystemService systemService;
    private static final transient Logger logger = LoggerFactory.getLogger(ProcessAPIController.class);
    @Autowired
    private APIParameterCheck apiParameterCheck;

    @Autowired
    private ProcessTypeService processTypeService;

    @Autowired
    private ProcessServiceEx processServiceEx;
    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @Value("${lookPicBeforeStart}")
    private String lookPicBeforeStart;
    @Value("${lookPic}")
    private String lookPic;
    @Autowired
    private LogService logService;

    @Autowired
    private TaskServiceEx taskServiceEx;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActivitiPicService activitiPicService;



    /**
     * 流程模型创建 页面调用
     *
     * @param systemId
     * @return
     */
    @RequestMapping(value = "create")
    public String create(String systemId, Model model) {
        if (systemId == null) {
            //返回参数
            model.addAttribute("systemId", "系统id systemId");
            return "jsp/error";
        }
        //过滤
        List<SystemEntity> systemEntities = systemService.queryList(null);
        model.addAttribute("systemEntities",systemEntities);
        model.addAttribute("systemId", systemId);
        return "jsp/processManager/processDefManager";
    }


    /**
     * 查询发起的流程
     *
     * @return
     */
    @RequestMapping(value = "queryProcessInstancesByStarterId", method = RequestMethod.POST)
    @ResponseBody
    public APIQuerySimpleResult queryProcessInstancesByStarterId(ProcessData processData, @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "40") Integer pageSize) {
        logger.info("ProcessAPIController.queryProcessInstancesByStarterId {} {} {}", processData.toString(), pageNo, pageSize);
        APIQuerySimpleResult apiQuerySimpleResult = new APIQuerySimpleResult();
        PageInfo<ProcessData> processDataPageInfo = processServiceEx.queryProcessInstancesByStarterId(processData, pageNo, pageSize);
        apiQuerySimpleResult.setMessage("请求已发流程成功");
        apiQuerySimpleResult.setData(processDataPageInfo.getList());
        apiQuerySimpleResult.setTotalCount(processDataPageInfo.getTotal());
        apiQuerySimpleResult.setTotalPage(processDataPageInfo.getPages());
        return apiQuerySimpleResult;
    }


    /**
     * 查询流程的类别
     *
     * @return
     */
    @RequestMapping(value = "processType")
    @ResponseBody
    public List<ProcessType> processType() {
        ProcessType processType = new ProcessType();
        processType.setState(0);
        return processTypeService.queryList(processType);
    }

    @RequestMapping(value = "getProcessPic",method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult getProcessPic(String processInstanceId, String systemId, HttpServletResponse response){
        logger.warn("TaskAPIController.getProcessPic.param {},{},{}", systemId, processInstanceId);
        APISimpleResult simpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(simpleResult, "systemId");
        }
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(simpleResult, "流程实例processInstanceId");
        }
        InputStream activitiPicModelByProcessId = activitiPicService.getActivitiPicModelByProcessId(processInstanceId);
        //OutputStream out = new ByteOutputStream();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try {
            //IOUtils.copy(activitiPicModelByProcessId,out);
            //ServletOutputStream outputStream = response.getOutputStream();

            IOUtils.copy(activitiPicModelByProcessId,b);
            simpleResult.setResult(0);
            simpleResult.setData(b.toByteArray());
        } catch (RuntimeException e) {
            simpleResult.setResult(1);
            simpleResult.setMessage("流程图获取失败");
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(activitiPicModelByProcessId);
            IOUtils.closeQuietly(b);
            //IOUtils.closeQuietly(out);
            return simpleResult;
        }

    }

    /**
     * 根据流程实例获取流程各个节点详情
     * @param processInstanceId
     * @param systemId
     * @return
     */
    @RequestMapping(value = "getProcessNodeInfos")
    @ResponseBody
    public APISimpleResult getProcessNodeInfos(String processInstanceId,String systemId){
        APISimpleResult simpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(simpleResult, "systemId");
        }
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(simpleResult, "流程实例processInstanceId");
        }
        try {
            List<UserTaskNodeInfo> userTaskNodeInfos = activitiPicService.queryActivitis(processInstanceId);
            simpleResult.setResult(0);
            simpleResult.setMessage("各节点详情查询成功");
            simpleResult.setData(userTaskNodeInfos);
        }catch (RuntimeException e){
            simpleResult.setResult(1);
            simpleResult.setMessage("各节点详情查询失败");
            logger.error("getProcessNodeInfos{}");e.printStackTrace();
        }finally {
            return simpleResult;
        }
    }



    /**
     * 分页查询所有的流程
     *
     * @param processDefEntity
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "processList",method = RequestMethod.POST)
    @ResponseBody
    public APIQuerySimpleResult processList(ProcessDefEntity processDefEntity, @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "40") Integer pageSize) {
        logger.info("ProcessAPIController，processList");
        APIQuerySimpleResult apiQuerySimpleResult = new APIQuerySimpleResult();
        PageInfo<ProcessDefEntity> processDataPageInfo = processDefinitionService.queryPageAllProcess(processDefEntity, pageNo, pageSize);
        apiQuerySimpleResult.setMessage("请求所有流程成功");
        apiQuerySimpleResult.setData(processDataPageInfo.getList());
        apiQuerySimpleResult.setTotalCount(processDataPageInfo.getTotal());
        apiQuerySimpleResult.setTotalPage(processDataPageInfo.getPages());
        return apiQuerySimpleResult;
    }


    /**
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "queryFormAdvice", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult queryFormAdvice(String processInstanceId, String systemId) {
        logger.warn("TaskAPIController.pickTaskCompleted.param {},{},{}", systemId, processInstanceId);
        APISimpleResult simpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(simpleResult, "systemId");
        }
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(simpleResult, "流程实例processInstanceId");
        }
        try {
            List<JSONObject> list = logService.queryCheckLogByProcessInstanceId(processInstanceId, InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO);
            simpleResult.setMessage("查询表单审核数据成功");
            simpleResult.setResult(0);
            simpleResult.setData(list);
        } catch (Exception e) {
            simpleResult.setMessage("查询表单审核数据失败");
            simpleResult.setResult(1);
            e.printStackTrace();
        } finally {
            return simpleResult;
        }
    }

    /**
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "queryUploadFiles", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult queryUploadFiles(String processInstanceId, String systemId) {
        logger.warn("ProcessAPIController.queryUploadFiles.param {},{}", systemId, processInstanceId);
        APISimpleResult simpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(simpleResult, "systemId");
        }
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(simpleResult, "流程实例processInstanceId");
        }
        try {
            List<JSONObject> list = logService.queryCheckLogByProcessInstanceId(processInstanceId, InnerBusinessVarConstants.FILE_URL_PATH);
            simpleResult.setMessage("查看上传文件路径成功");
            simpleResult.setResult(0);
            simpleResult.setData(list);
        } catch (Exception e) {
            simpleResult.setMessage("查看上传文件路径失败");
            simpleResult.setResult(1);
            e.printStackTrace();
        } finally {
            return simpleResult;
        }
    }


    /**
     * 流程发起校验
     *
     * @param systemId
     * @param key
     * @param roleId
     * @param userId
     * @return
     */
    @RequestMapping(value = "processCheck", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult processKey(String systemId, String key, String roleId, String userId,HttpServletRequest request) {
        logger.warn("ProcessAPIController.processKey {}，{}，{}，{}", systemId, key, roleId, userId);
        APISimpleResult simpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(simpleResult, "systemId");
        }
        if (apiParameterCheck.commonParamCheck(key)) {
            return this.simpleResult(simpleResult, "流程key");
        }
        try {
            processServiceEx.processCheck(key, userId, roleId,simpleResult);
        } catch (Exception e) {
            simpleResult.setMessage("校验失败");
            simpleResult.setResult(1);
            logger.error("processCheck {}", e);
        } finally {
            return simpleResult;
        }
    }


    /**
     * 发起流程
     *
     * @param systemId
     * @param key
     * @param startUserId
     * @param parameter
     * @return
     */
    @RequestMapping(value = "start", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult start(String systemId, String key, String startUserId, String parameter, HttpServletRequest request) {
        APISimpleResult simpleResult = new APISimpleResult();
        if(apiParameterCheck.commonParamCheck(systemId)){
            return this.simpleResult(simpleResult,"systemId");
        }
        if(apiParameterCheck.commonParamCheck(startUserId)){
            return this.simpleResult(simpleResult,"审核人userId");

        }
        if(apiParameterCheck.commonParamCheck(key)){
            return this.simpleResult(simpleResult,"流程key");
        }
        ValidateInterface validateInterface = validateParams(request, new StartProcessValidate());
      /*  if(!validateInterface.validateParms()){
            return simpleResult(simpleResult, validateInterface.getErrorMessage());
        }*/

        logger.warn("ProcessAPIController.start {}", validateInterface.getParms());
        //JSONObject jsonObject = JSON.parseObject(parameter);
        try {
            processServiceEx.start(validateInterface.getParms(), startUserId, key, systemId, simpleResult);
            simpleResult.setMessage("发起成功");
            simpleResult.setResult(0);
        } catch (Exception e) {
            e.printStackTrace();
        	ErrorInfo errorInfo = ExpectionUtils.getConditionErrorMsg(e);
        	simpleResult.setErrorMsg(errorInfo.errorMsg);
        	simpleResult.setErrorCode(errorInfo.errorCode);
            simpleResult.setMessage("发起失败");
            simpleResult.setResult(1);
            logger.error("start {}", e);
        } finally {
            return simpleResult;
        }
    }

    /**
     * 通过流程实例id获取流程表单
     *
     * @param systemId
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "getProcessForm", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult getProcessFormByProcessInstanceId(String systemId, String processInstanceId) {
        logger.warn("ProcessAPIController.getProcessFormByProcessInstanceId {},{}", systemId, processInstanceId);
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(apiSimpleResult, "流程processInstanceId");
        }
        apiSimpleResult.setMessage("获取流程运行中表单数据成功");
        apiSimpleResult.setResult(0);
        apiSimpleResult.setData(processServiceEx.getFormInfoByProcessInstanceId(processInstanceId));
        return apiSimpleResult;
    }

    /**
     * 未发起的流程key
     *
     * @param systemId
     * @param key
     * @return
     */
    @RequestMapping(value = "processPicBeforeStart", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult processPicBeforeStart(String systemId, String key, HttpServletRequest request) {
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            //返回参数
            //model.addAttribute("systemId","系统systemId");
            return this.simpleResult(apiSimpleResult, "系统systemId");
            //return "jsp/error";
        }
        if (apiParameterCheck.commonParamCheck(key)) {
            //返回参数
            // model.addAttribute("systemId","流程key");
            return this.simpleResult(apiSimpleResult, "流程key");
            //return "jsp/error";
        }
        apiSimpleResult.setMessage("请求未发起的流程url成功");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(lookPicBeforeStart).append("?key=").append(key);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", stringBuilder.toString());
        apiSimpleResult.setResult(0);
        apiSimpleResult.setData(jsonObject);
        return apiSimpleResult;
    }


    /**
     * 判定是否可以撤销流程
     *
     * @param systemId
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "isCanProcessDelete", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult isCanProcessDelete(String systemId, String processInstanceId) {
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(apiSimpleResult, "系统systemId");
        }
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(apiSimpleResult, "流程实例processInstanceId");
        }
        if (!apiParameterCheck.checkProcessInstanceExist(processInstanceId)) {
            apiSimpleResult.setMessage("流程实例processInstanceId不能为空");
            apiSimpleResult.setResult(1);
            return apiSimpleResult;
        }
        Boolean flag = processServiceEx.isCanProcessInstanceDeleteBySystemId(processInstanceId, systemId);
        //系统id
        apiSimpleResult.setMessage("请求是否可以撤销成功");
        apiSimpleResult.setResult(0);
        //设置true或者false
        apiSimpleResult.setData(flag);
        return apiSimpleResult;
    }




    /**
     * 挂起
     *
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "processSuppend", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult processSuppend(String taskId, String processInstanceId) {
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (!apiParameterCheck.checkProcessInstanceExist(processInstanceId)) {
            apiSimpleResult.setMessage("流程实例processInstanceId不能为空");
            apiSimpleResult.setResult(1);
            return apiSimpleResult;
        }
        if (!apiParameterCheck.checkProcessInstanceExist(taskId)) {
            apiSimpleResult.setMessage("任务taskId不能为空");
            apiSimpleResult.setResult(1);
            return apiSimpleResult;
        }
        try {
            processServiceEx.processSuppend(processInstanceId);
            //系统id
            apiSimpleResult.setMessage("挂起成功");
            apiSimpleResult.setResult(0);
        } catch (RuntimeException r) {
            //系统id
            apiSimpleResult.setMessage("挂起失败");
            apiSimpleResult.setResult(1);
        } finally {

            return apiSimpleResult;
        }
    }

    /**
     * 激活
     *
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "processActive", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult processActive(String taskId, String processInstanceId) {
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (!apiParameterCheck.checkProcessInstanceExist(processInstanceId)) {
            apiSimpleResult.setMessage("流程实例processInstanceId不能为空");
            apiSimpleResult.setResult(1);
            return apiSimpleResult;
        }
        if (!apiParameterCheck.checkProcessInstanceExist(taskId)) {
            apiSimpleResult.setMessage("任务taskId不能为空");
            apiSimpleResult.setResult(1);
            return apiSimpleResult;
        }
        try {
            processServiceEx.processActive(processInstanceId);
            apiSimpleResult.setMessage("激活成功");
            apiSimpleResult.setResult(0);
        } catch (RuntimeException r) {
            apiSimpleResult.setMessage("激活失败");
            apiSimpleResult.setResult(1);
        } finally {
            return apiSimpleResult;
        }

    }


    /**
     * 其他系统业务系统直接删除该流程
     *
     * @param systemId
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "processDelete", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult processDelete(String systemId, String userId, String processInstanceId) {
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(apiSimpleResult, "系统systemId");
        }
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(apiSimpleResult, "流程实例processInstanceId");
        }
        if (apiParameterCheck.commonParamCheck(userId)) {
            return this.simpleResult(apiSimpleResult, "删除人userId");
        }
        try {
            processServiceEx.processInstanceDeleteBySystemId(systemId, userId, processInstanceId);
            apiSimpleResult.setMessage("流程实例撤销成功");
            apiSimpleResult.setResult(0);
        }catch (ActivitiObjectNotFoundException e){
            apiSimpleResult.setResult(1);
            apiSimpleResult.setErrorCode("10002");
            apiSimpleResult.setErrorMsg("流程实例processInstanceId不存在");
            logger.error("ProcessAPIController.processDelete {}",e.getMessage());
        }catch (RuntimeException e){
             apiSimpleResult.setResult(1);
            apiSimpleResult.setMessage("流程实力撤销失败");
            e.printStackTrace();
        }finally {
            return apiSimpleResult;
        }

    }


    /**
     * 其他系统业务系统直接删除该流程
     *
     * @param systemId
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "processInstanceByReject", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult processInstanceByReject(String systemId, String userId, String taskId, String processInstanceId, HttpServletRequest request) {
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(apiSimpleResult, "系统systemId");
        }
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(apiSimpleResult, "流程实例processInstanceId");
        }
        if (apiParameterCheck.commonParamCheck(userId)) {
            return this.simpleResult(apiSimpleResult, "操作人userId");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("systemId", systemId);
        jsonObject.put("userId", userId);
        ValidateInterface validateInterface = validateParams(request, new TaskCompletedValidate());
        if (!validateInterface.validateParms()) {
            return simpleResult(apiSimpleResult, validateInterface.getErrorMessage());
        }
        Map<String, Object> map = validateInterface.getParms();
        //这里获取表单相关的数据
        Map<String, Object> data = taskServiceEx.handleCheckedCompletedData(map);
        //将该数据放入到变量中
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        data.put("taskKey", task.getTaskDefinitionKey());
        data.put("taskName", task.getName());
        data.put("doTime", DateUtils.now());
        runtimeService.setVariable(processInstanceId, InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO, JSONObject.toJSONString(data));
        try {
            processServiceEx.processInstanceDeleteByReject(processInstanceId, taskId, userId, jsonObject);
            //系统id
            apiSimpleResult.setMessage("流程实例驳回成功");
            apiSimpleResult.setResult(0);
        } catch (Exception e) {
            apiSimpleResult.setMessage("流程实例驳回失败");
            apiSimpleResult.setResult(1);
            e.printStackTrace();
        } finally {

            return apiSimpleResult;
        }

    }


    /**
     * 已发起的流程
     *
     * @param systemId
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "processInstancePic", method = RequestMethod.POST)
    @ResponseBody
    public APISimpleResult processPic(String systemId, String processInstanceId, Model model) {
        APISimpleResult apiSimpleResult = new APISimpleResult();
        if (apiParameterCheck.commonParamCheck(systemId)) {
            return this.simpleResult(apiSimpleResult, "系统systemId");
        }
        if (apiParameterCheck.commonParamCheck(processInstanceId)) {
            return this.simpleResult(apiSimpleResult, "流程实例processInstanceId");

        }
        apiSimpleResult.setMessage("请求已发起的流程图url成功");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(lookPic).append("?processId=").append(processInstanceId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", stringBuilder.toString());
        apiSimpleResult.setResult(0);
        apiSimpleResult.setData(jsonObject);
        return apiSimpleResult;
    }


    /**
     * 校验器
     *
     * @param request
     * @param validateInterface
     * @return
     */
    private ValidateInterface validateParams(HttpServletRequest request, ValidateInterface validateInterface) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, String> params = new HashMap<>(30);
        for (String key : map.keySet()) {
            String[] values = map.get(key);
            params.put(key, values[0]);
        }
        //校验
        validateInterface.setParms(params);
        return validateInterface;
    }


    /**
     * 为了拼接参数
     *
     * @param apiSimpleResult
     * @return
     */
    private APISimpleResult simpleResult(APISimpleResult apiSimpleResult, String name) {
        String s = "必需参数";
        apiSimpleResult.setMessage(name.concat(s));
        apiSimpleResult.setResult(1);
        return apiSimpleResult;
    }


}
