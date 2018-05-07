package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.TaskAPIData;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.service.HistoryCheckedTaskService;
import cn.huimin.process.web.service.TaskServiceEx;
import cn.huimin.process.web.util.Constants;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 取回已审批的流程
 */
@Controller
@RequestMapping("historyCheckedTask")
public class HistoryCheckedTaskController {

    private static  final transient Logger log = LoggerFactory.getLogger(HistoryCheckedTaskController.class);

    @Autowired
    private HistoryCheckedTaskService historyCheckedTaskService;

    @Autowired
    private TaskServiceEx taskServiceEx;


    /**
     * 分页已办事项
     * @return
     */
    @RequestMapping(value = "checkedTaskAuth",method = RequestMethod.POST)
    @ResponseBody
    public Page queryTaskCompletedByAuthId(HttpServletRequest request, Integer page, Integer pageSize){
        HttpSession session =request.getSession();
        UserInfo admin = (UserInfo) session.getAttribute(Constants.userInfo);
        Page pageJson = new Page();
        TaskAPIData taskAPIData = new TaskAPIData();
        taskAPIData.setHanderId(String.valueOf(admin.getAdminid()));
        PageInfo pageInfo =taskServiceEx.TaskCompletedList(taskAPIData,page,pageSize);
        pageJson.setTotalPage(pageInfo.getPages());
        pageJson.setTotal(pageInfo.getTotal());
        pageJson.setRows(pageInfo.getList());
        pageJson.setPage(page);
        log.info("HistoryCheckedTaskController.queryTaskCompletedByAuthId {}",pageJson);
        return  pageJson;
    }


    /**
     * 判断是否支持取回
     * @param processId
     * @param taskId
     * @return
     */
    @RequestMapping("isPick")
    @ResponseBody
    public SimpleResult isPick(String processId,String taskId){
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.setSuccess(historyCheckedTaskService.isPick(processId,taskId));
        return simpleResult;
    }

    /**
     * 取回流程
     * @param processId
     * @param taskId
     * @return
     */
    @RequestMapping("pickActivity")
    @ResponseBody
    public SimpleResult pickActivity(String processId, String taskId,HttpServletRequest request){
        HttpSession session =request.getSession();
        UserInfo admin = (UserInfo) session.getAttribute(Constants.userInfo);
        SimpleResult simpleResult = new SimpleResult();
        try {
                simpleResult = historyCheckedTaskService.doPick(processId,taskId,String.valueOf(admin.getAdminid()));
                simpleResult.setMessage("取回成功");

        }catch (Exception e){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("取回失败");
            e.printStackTrace();
        }finally {
            return simpleResult;
        }
    }
}
