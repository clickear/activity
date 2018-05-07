package cn.huimin.process.web.contorller;

import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.model.ProcessData;
import cn.huimin.process.web.model.TaskAPIData;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.service.*;
import cn.huimin.process.web.util.Constants;
import cn.huimin.process.web.util.ProcessInstanceUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 查询流程相关的
 */
@Controller
@RequestMapping("query")
public class QueryController {
	//日志打印
	private static  final transient Logger log = Logger.getLogger(QueryController .class);



	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;



	@Autowired
	private ProcessServiceEx processServiceEx;

	@Value("${getRoleByUserId}")
	private String getRoleByUserId;

	@Autowired
	private TaskServiceEx taskServiceEx;

	@Autowired
	private UserTaskVarPoolService userTaskVarPool;
	@Autowired
	private LogService logService;




	/**
	 * 跳转审批页面
	 * @param processId
	 * @param taskId
	 * @param model
	 * @return
     */
	@RequestMapping("/taskInfo")
		public String gotoTaskInfo( String processId,String taskId,String taskKey,String processDefId,Model model,HttpServletRequest request) {
		Task task =taskService.createTaskQuery().taskId(taskId).singleResult();
		model.addAttribute("processId",processId);
		model.addAttribute("taskId",taskId);

		//任务类型的属性
		 //int priority =task.getPriority();
		String formKey =task.getFormKey();
		//代表是知会节点
		if("2".equals(formKey)){
			//不唤醒
			model.addAttribute("notify",false);
		}else {
			//不是
			model.addAttribute("notify",true);
		}
		//form表单
		JSONArray formPropertie = userTaskVarPool.getFormPropertiesVarByProcessKeyAndTaskKey(processDefId,taskKey);
		 Task task1 =taskService.createTaskQuery().taskId(taskId).singleResult();
		String val = String.valueOf(ProcessInstanceUtils.getHistoryVar(historyService,processId,TestApiConstants.FORM_DATA_TEST));
		 //String val =  String.valueOf(runtimeService.getVariable(task1.getExecutionId(),"fomrData_test"));
		//流程相关变量审核表
		JSONArray checkInfo = userTaskVarPool.getUserTaskVarByProcessKeyAndTaskKey(processDefId,taskKey);
		model.addAttribute("formPropertie",formPropertie);
		model.addAttribute("checkInfo",checkInfo);
		//表单数据
		if(JSONObject.parseObject(val)==null){
			model.addAttribute("formDataInfo",null);

		}else {
			model.addAttribute("formDataInfo",JSONObject.parseObject(val));
		}
		List<JSONObject> list =logService.queryCheckLogByProcessInstanceId(processId,InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO);
		//只是用来展示的 0是发起 1是展示 2 编辑
		model.addAttribute("historyAdviceList",list);
		return "jsp/checkPage";
	}


	/**
	 * 分页查询当前用户的所有信息
	 * @param page
	 * @param pageSize
	 * @param request
     * @return
     */
	@RequestMapping(value = "taskListPage",produces = "application/json",method = RequestMethod.POST)
	@ResponseBody
	public Page taskListPage(Integer page, Integer pageSize, HttpServletRequest request){
		HttpSession session =request.getSession();
		UserInfo admin = (UserInfo) session.getAttribute(Constants.userInfo);
		Page pageJson = new Page();
		TaskAPIData taskAPIData = new TaskAPIData();
		taskAPIData.setHanderId(String.valueOf(admin.getAdminid()));
		PageInfo pageInfo =taskServiceEx.TaskList(taskAPIData,page,pageSize);
		pageJson.setTotalPage(pageInfo.getPages());
		pageJson.setTotal(pageInfo.getTotal());
		pageJson.setRows(pageInfo.getList());
		pageJson.setPage(page);
		return  pageJson;
	}












	/**
	 * 分页查询发起已经完成的
	 *
	 * @return
	 */
	@RequestMapping(value = "applyProcess",method = RequestMethod.POST)
	@ResponseBody
	public Page  queryApplyProcess(String processName,HttpServletRequest request,Integer page, Integer pageSize){
		HttpSession  session =request.getSession();
		UserInfo admin = (UserInfo) session.getAttribute(Constants.userInfo);
		ProcessData processData = new ProcessData();
		processData.setProcessDefName(processName);
		Page pageJson = new Page();
		processData.setStartUserId(String.valueOf(admin.getAdminid()));
		PageInfo pageInfo =processServiceEx.queryProcessInstancesByStarterId(processData,page,pageSize);
		 List<ProcessData> list = pageInfo.getList();
		for(ProcessData processData1:list){
			//循环获取流程审批结果
			Boolean result = (Boolean) ProcessInstanceUtils.getHistoryVar(historyService,processData1.getProcessInstanceId(), InnerActivitiVarConstants.HM_ACITVITI_PROCESS_RESULT);
			processData1.setResult(result);
		}
		pageJson.setTotalPage(pageInfo.getPages());
		pageJson.setTotal(pageInfo.getTotal());
		pageJson.setRows(list);
		pageJson.setPage(page);
		log.info("流程申请数据："+pageJson);
		return  pageJson;
	}


	@RequestMapping("goToCurrentUserInfo")
	public String goToCurrentUserInfo(String processInstanceId,Model model){
		model.addAttribute("checkUser",taskServiceEx.queryTaskInfoByProcessInstanceId(processInstanceId));
		return "jsp/taskHandler";
	}


}
