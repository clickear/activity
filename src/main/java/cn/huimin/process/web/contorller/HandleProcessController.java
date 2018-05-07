
package cn.huimin.process.web.contorller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.service.HandleProcessService;
import cn.huimin.process.web.util.Constants;

/**
 * Created by Administrator on 2016/10/28.
 * 流程处理
 */
@RequestMapping("handle")
@Controller
public class HandleProcessController {

    private static  final transient Logger log = Logger.getLogger(HandleProcessController.class);

    @Autowired
    private HandleProcessService handleProcessService;
    @Autowired
    private  HistoryService historyService;

    @Autowired
    private TaskService taskService;







    /**
     * 获取可以回退的所有节点id和name
     * @param processId
     */
    @RequestMapping("/getCanGoBackActivtiys")
    @ResponseBody
     public  Map<String,Object> getCanGoBackActivtiys(String processId,HttpServletRequest request){
          request.getSession().getAttribute(Constants.userInfo);
         return handleProcessService.getCanGoBackActivtiys(processId);
    }

    @RequestMapping("delete")
    @ResponseBody
    public SimpleResult delete(String processId){

        SimpleResult simpleResult = new SimpleResult();

        try {
            handleProcessService.delete(processId);
            simpleResult.setSuccess(true);
            simpleResult.setMessage("流程终止成功");
        }catch (Exception e){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("操作失败");

        }finally {
            return simpleResult;
        }
    }




    /**
     * 前往回退页面
     * @param processId
     * @param taskId
     * @return
     */
    @RequestMapping("/goToBackPage")
    public String goToBackPage(String processId,String taskId,Model model){
        model.addAttribute("processId",processId);
        model.addAttribute("taskId",taskId);
        model.addAttribute("activitys", handleProcessService.getCanGoBackActivtiys(processId));
        return  "jsp/backPage";
    }

    /**
     * 支持任意完成节点的回退
     * @param processId 流程实例id
     * @param activityId 回退到节点的id
     * @param remark 回退的原因
     * @return
     */
    @RequestMapping(value = "doBackActivitys",method = RequestMethod.POST)
    @ResponseBody
    public SimpleResult doBackActivitys(HttpServletRequest request,String processId,String taskId,String  activityId,String remark){
       UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        SimpleResult result = new SimpleResult();
        if(activityId==null || activityId.equals("")){
            result.setMessage("没有可回退节点");
            return  result;
        }
        //不同意的请填写原因
        if(remark==null||remark.equals("")){
            result.setSuccess(false);
            result.setMessage("回退， 请填写原因");
            return result;
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processId).active().list();
        if(tasks==null||tasks.size()==0){
            result.setMessage("该流程已经结束，不支持回退");
            result.setSuccess(false);
            return  result;
        }
        if(tasks.size()>1){
            result.setMessage("该节点有多个活动点，不支持回退");
            result.setSuccess(false);
            return  result;
        }
        TaskEntity task = (TaskEntity)tasks.get(0);
        //TODO
        //设置回退节点的审批人
        List<String> backIds = new ArrayList<String>();
        backIds.add(activityId);
        String userId = "";
       List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).taskDefinitionKey(activityId).list();
        for(HistoricTaskInstance historicTaskInstance:historicTaskInstances){
          if(historicTaskInstance.getAssignee()!=null){
                userId = historicTaskInstance.getAssignee();
              break;
          }
       }
        return handleProcessService.dbBackTo(task.getId(),backIds,String.valueOf(userInfo.getAdminid()),processId,userId,"回退",remark);
    }

    }
