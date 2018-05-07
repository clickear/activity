package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.InformProcess;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.service.InformProcessService;
import cn.huimin.process.web.util.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/14.
 */
@Controller
@RequestMapping("informAct")
public class InformProcessController {

    private static  final transient Logger log = Logger.getLogger(InformProcessController.class);

    @Autowired
    private InformProcessService informProcessService;

    //分页查询当前知会的所有流程
    @RequestMapping(value = "informActPage",produces = "application/json",method = RequestMethod.POST)
    @ResponseBody
    public Page informActPage(Integer page, Integer pageSize, HttpServletRequest request){
        HttpSession session =request.getSession();
        UserInfo admin = (UserInfo) session.getAttribute(Constants.userInfo);
        //Page pageJson = informProcessService.queryInformPage(admin.getAdminid(),page,pageSize);
        //log.info(pageJson);
        //return pageJson;
        return null;
    }
    @RequestMapping("insertProcessRole")
    @ResponseBody
    public SimpleResult insertinformAct(String processId,String taskId,String informPersonId, HttpServletRequest request){
        SimpleResult simpleResult = new SimpleResult();
        if(informPersonId ==null ||"".equals(informPersonId)){
            simpleResult.setMessage("请选择要知会的人");
            return simpleResult;
        }
        InformProcess informProcess =new InformProcess();
        UserInfo admin = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        informProcess.setCreateTime(new Date());
        informProcess.setTaskId(taskId);
        //informProcess.setState(1);
        informProcess.setOperatePersonId(String.valueOf(admin.getAdminid()));
        informProcess.setProcessInstanceId(processId);
        try {
            informProcessService.insertInformPage(informProcess,informPersonId);
            simpleResult.setSuccess(true);
            log.info("知会:"+ informProcess);
            simpleResult.setMessage("知会成功");
        }catch (Exception e){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("知会失败");
            log.error(e);
        }finally {
            return simpleResult;
        }
    }

    /**
     * 前往加签页面
     * @param processId
     * @return
     */
    @RequestMapping("gotoInformPage")
    public String gotoInformPage(String processId,String taskId, Model model){
        model.addAttribute("processId",processId);
        model.addAttribute("taskId",taskId);
        return "jsp/addInformAct";
    }


}
