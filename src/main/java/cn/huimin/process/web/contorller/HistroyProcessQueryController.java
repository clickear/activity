package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.HistoryProcessData;
import cn.huimin.process.web.service.HistroyProcessQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016/12/14.
 */
@Controller("histroyProcessQuery")
@RequestMapping("histroyProcessQuery")
public class HistroyProcessQueryController {

    @Autowired
    private HistroyProcessQueryService histroyProcessQueryService;
    @RequestMapping("queryProcessById")
    @ResponseBody
    public HistoryProcessData queryHistoryProcessByProcessId(String processId){
      return   histroyProcessQueryService.queryHistoryProcessByProcessId(processId);
    }
}
