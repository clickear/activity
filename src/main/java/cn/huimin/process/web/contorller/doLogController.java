package cn.huimin.process.web.contorller;

import cn.huimin.process.web.service.LogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 操作记录controller
 */
@Controller
@RequestMapping("doLog")
public class doLogController {

    private static  final transient Logger log = LoggerFactory.getLogger(doLogController.class);



    @Autowired
    private LogService logService;

    /**
     * 前往操作记录界面
     * @param processId
     * @return
     */
    @RequestMapping("goToLog")
    public String gotoLogPage(String processId, Model model){
        model.addAttribute("historyList",logService.queryHandleLogByProcessInstanceId(processId));
        return "jsp/log";
    }


}
