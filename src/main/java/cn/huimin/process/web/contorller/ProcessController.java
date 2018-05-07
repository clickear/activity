package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.service.ProcessServiceEx;
import cn.huimin.process.web.service.TaskServiceEx;
import cn.huimin.process.web.util.Constants;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-7-15.
 * 第二版本
 */
@Controller
@RequestMapping("process")  
public class ProcessController {
    private static  final transient Logger log = LoggerFactory.getLogger(ProcessController.class);


    @Autowired
    TaskService taskService;

    @Autowired
    RuntimeService runtimeService;



    @Autowired
    private ProcessServiceEx processServiceEx;

    @Autowired
    private TaskServiceEx taskServiceEx;




    /**
     * 完成审批
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "doTask" , method = RequestMethod.POST)
    @ResponseBody
    public SimpleResult doTask(HttpServletRequest request,HttpSession session){
        SimpleResult result = new SimpleResult();
        Map<String,String[]> input_parms = request.getParameterMap();
        Map<String,String> parms = new HashMap<String, String>();
        for (String key:input_parms.keySet()) {
            String[] values = input_parms.get(key);
            if(values.length > 1){
                result.setMessage(key+"的值重复！");
                return result;
            }
            parms.put(key,values[0]);
        }
        UserInfo admin = (UserInfo) session.getAttribute(Constants.userInfo);
        parms.put("assignee",String.valueOf(admin.getAdminid()));
        SimpleResult simpleResult = new SimpleResult();
        try {
            simpleResult.setSuccess(true);
            simpleResult.setMessage("处理成功");
            taskServiceEx.doTask("1",String.valueOf(admin.getAdminid()),parms.get("taskId"),null);
        }catch (RuntimeException e){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("处理失败");
            log.error("doTask {}",e);
        }finally {
            return simpleResult;
        }

    }




    /**
     * 审核不通过
     * @param processId
     * @param taskId
     * @param remark
     * @param noProblem
     * @param request
     * @return
     */
    @RequestMapping("processDelete")
    @ResponseBody
    public SimpleResult processDelete(String processId, String taskId, String remark,String noProblem, HttpServletRequest request){
        HttpSession session =request.getSession();
        UserInfo admin = (UserInfo) session.getAttribute(Constants.userInfo);
        String userId = String.valueOf(admin.getAdminid());
        SimpleResult simpleResult = new SimpleResult();
        //流程终止
        if(remark==null||"".equals(remark)){
             processServiceEx.processInstanceDelete(userId,taskId,remark,"2");
        }else {
            processServiceEx.processInstanceDelete(userId,taskId,remark,"1");
        }
        simpleResult.setSuccess(true);
        simpleResult.setMessage("操作成功");
        //这里是erh执行的不通过
       return  simpleResult;

    }

    /**
     * 挂起
     * @param processId
     * @return
     */
    @RequestMapping("processSuppend")
    @ResponseBody
    public SimpleResult processSuppend(String processId){
        SimpleResult simpleResult= new SimpleResult();
        try {
          processServiceEx.processSuppend(processId);
            simpleResult.setMessage("挂起成功");
        }catch (Exception e){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("挂起失败");
            log.error("processSuppend {}",e);
        }finally {
            return simpleResult;
        }
    }









    /**
     * 激活流程
     * @param processId
     * @return
     */
    @RequestMapping("processStart")
    @ResponseBody
    public SimpleResult processStart(String processId){
        SimpleResult simpleResult=new SimpleResult();
        try {
          processServiceEx.processActive(processId);

            simpleResult.setMessage("启动成功");
        }catch (Exception e){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("启动失败");
            log.error("processStart {}",e);
        }finally {
            return simpleResult;
        }
    }



}
