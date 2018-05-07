package cn.huimin.process.web.contorller;

import cn.huimin.process.web.model.ProcessDefEntity;
import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.service.ProcessDefinitionService;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 流程定义相关信息查询
 */
@Controller
@RequestMapping("processDefinition")
public class ProcessDefinitionController {

    private static  final transient Logger log = Logger.getLogger(ProcessDefinitionController.class);

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @ResponseBody
    @RequestMapping("list")
    public List<ProcessDefEntity> queryProcessDefList(){
        return processDefinitionService.queryProcessDefList();
    }

    @RequestMapping(value = "processDefPage",produces = "application/json")
    @ResponseBody
    public Page processDefPage(Integer page, Integer pageSize, HttpServletRequest request){
        Integer startResutl = (page-1)*pageSize;
        Page pageJson =processDefinitionService.queryProcessDefPage(startResutl,pageSize);
        log.info(pageJson);
        return pageJson;
    }

    /**
     * 分页查询所有的流程
     * @param processDefEntity
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/queryPageAllProcess",produces = "application/json", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Page queryPageAllProcess(ProcessDefEntity processDefEntity, @RequestParam(value="page",defaultValue="1")Integer page,
                                    @RequestParam(value="pageSize",defaultValue="10")Integer pageSize) {
        Page pageJson = new Page();
        PageInfo<ProcessDefEntity> pageInfo =processDefinitionService.queryPageAllProcess(processDefEntity,page,pageSize);
        pageJson.setTotal(pageInfo.getTotal());
        pageJson.setRows(pageInfo.getList());
        pageJson.setPage(pageInfo.getPrePage());
        pageJson.setTotalPage(pageInfo.getPages());
        //log.info("queryPageAllProcess {}",pageJson);
        return pageJson;

    }


}
