package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.UserTaskNodeInfo;
import cn.huimin.process.web.model.ActCreationEx;
import cn.huimin.process.web.service.ActCreationServiceEx;
import cn.huimin.process.web.service.ActivitiPicService;
import cn.huimin.process.web.util.ProcessInstanceUtils;
import org.activiti.engine.ProcessEngine;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/10/28.
 * 图片controller层
 */
@RequestMapping("activitiPic")
@Controller
public class ActivitiPicController {
    private static final transient Logger logger = LoggerFactory.getLogger(ActivitiPicController.class);

    @Autowired
    private ActivitiPicService activitiPicService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private ActCreationServiceEx actCreationServiceEx;



    /**
     * 查询所有的活动节点信息
     * @param processId
     * @return
     */
    @RequestMapping("queryActivitis")
    @ResponseBody
    public List<UserTaskNodeInfo> queryActivitis(String processId,Integer method) {
        //List<UserTaskNodeInfo> list = (List<UserTaskNodeInfo>) activitiPicService.getActivitiPicByProcessId(processId,2);
        List<UserTaskNodeInfo> list = null;
        if(method == 3){
            list = activitiPicService.getUserTaskNodeInfos(processId);
            logger.info("ActivitiPicController.queryActivitis {}",list);
        }
        if(method == 0){
            list = activitiPicService.queryActivitis(processId);
        }
        if(method == 1){
            list = activitiPicService.getUserTaskNodeInfos(processId);

        }
        if(method == 2){
            list = activitiPicService.queryActivitis(processId);
        }
        return list;
    }


    /**
     * 查询所有的活动节点信息
     * @param processId
     * @return
     */
    @RequestMapping("queryActivitis1")
    @ResponseBody
    public List<UserTaskNodeInfo> queryActivitis1(String processId,Integer method) {
        List<UserTaskNodeInfo> list1 = activitiPicService.queryActivitis(processId);
        return list1;
    }
    @RequestMapping("gotoPic")
    public String gotoPic(String processInstanceId,Model model){
        model.addAttribute("processId",processInstanceId);
        return "jsp/activitiPic2";
    }




    //查看流程图

    /**
     * 未发起的流程图
     * @param response
     * @param key
     * @throws IOException
     */
    @RequestMapping("queryPicBeforeStart")
    public void queryPicBeforeStart(HttpServletResponse response, HttpServletRequest request,String key)throws IOException{
        InputStream in =  activitiPicService.getActivitiPicByProcessDefKey(key);
        OutputStream out= response.getOutputStream();
        copyPic(in,out);
    }


    /**
     * 未发起的
     * @param response
     * @param key
     * @throws IOException
     */
    @RequestMapping("queryActivityBeforeStart")
    public List<Map<String, Object>> queryActivitysBeforeStart(HttpServletResponse response,String key)throws IOException{
        return activitiPicService.queryActivitisByDefKey(key);
    }

    /**
     * 跳转到流程图页面
     * @param
     * @param model
     * @return
     */
    @RequestMapping("lookPicBeforeStart")
    public String lookPicBeforeStart(String  key,Model model){
        model.addAttribute("key",key);
        return "jsp/activitiPicBeforeStart";
    }


    /**
     * 跳转到流程图页面
     * @param processId
     * @param model
     * @return
     */
    @RequestMapping("lookMakePic")
    public String lookMakePic(String  processId,Model model,Integer method){
        model.addAttribute("processId",processId);
        model.addAttribute("method",method);
        return "jsp/activitiPic";
    }


    /**
     * 跳转到流程图页面
     * @param processId
     * @param model
     * @return
     */
    @RequestMapping("lookPic")
    public String lookPic(String  processId,Integer method,Model model){
        model.addAttribute("processId",processId);
        //如果终止的话直接是时序图就行
        if(ProcessInstanceUtils.isStop(processEngine,processId)){
            model.addAttribute("method",3);
            return "jsp/activitiPic";
        }
        if(!ProcessInstanceUtils.isStop(processEngine,processId)){
            //如果等于2的未完成分成两种情况
            ActCreationEx actCreationEx = new ActCreationEx();
            actCreationEx.setProcessInstanceId(processId);
            actCreationEx.setState(0);
            List<ActCreationEx> list =  actCreationServiceEx.query(actCreationEx);
            if(list!=null&&list.size()>0){
                //有加签功能
                return "jsp/processTotalPic";
            }
        }
        model.addAttribute("method",0);
        return "jsp/activitiPic";

    }



    /**
     * 获取图片
     * @param processId
     * @param response
     * @throws IOException
     */
    @RequestMapping("activitPic")
    public void queryPic2(String processId, HttpServletResponse response,Integer method)throws IOException{
        InputStream in = null;
        OutputStream out = response.getOutputStream();
        if(method == 3 || method ==1){
             in =  activitiPicService.createCustomeActivitiPicByProcessId(processId);
        }
        if (method ==2 || method ==0){
             in =  activitiPicService.getActivitiPicModelByProcessId(processId);
        }
       copyPic(in,out);
    }

    private void copyPic(InputStream in,OutputStream out){
        try {
            IOUtils.copy(in,out);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }






}
