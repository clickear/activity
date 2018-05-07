package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.Employeerole;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.service.TempActivityChangeService;
import cn.huimin.process.web.util.Constants;
import cn.huimin.process.web.util.EhrRequestApiUtils;
import cn.huimin.process.web.util.EhrUserDataHandleUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 临时签的变动
 */
@Controller
@RequestMapping("tempActivityChange")
public class TempActivityChangeController {

    private static  final Logger log = LoggerFactory.getLogger(TempActivityChangeController.class);

    @Autowired
    private TempActivityChangeService tempActivityChangeService;


    @Autowired
    private TaskService taskService;

    @Value("${getRoleByUserId}")
    private String getRoleByUserId;



    /**
     * 加签功能
     * @param taskId
     * @param processId
     * @return
     */
    @RequestMapping(value = "add",method = RequestMethod.POST)
    @ResponseBody
    public SimpleResult addActivity(String taskId, String processId,String userCode,String activityNames, Integer type,HttpServletRequest request ) {
            SimpleResult simpleResult = new SimpleResult();
        if(userCode==null||userCode.equals("")||activityNames==null||activityNames.equals("")){
            simpleResult.setMessage("不设置加签的岗位或人 不支持加签");
            return  simpleResult;
        }
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
            try {
                if(type==1){
                    userCode = userInfo.getAdminid()+","+userCode;
                    tempActivityChangeService.addActivity(taskId,processId,userCode,activityNames,String.valueOf(userInfo.getAdminid()));
                }else {
                    userCode = userInfo.getAdminid()+","+userCode;
                    tempActivityChangeService.addJoinActivity(taskId,processId,userCode,activityNames,String.valueOf(userInfo.getAdminid()));
                }
                simpleResult.setMessage("加签成功");
                simpleResult.setSuccess(true);
                //这里是需要添加的
                log.info("返回结果 {}",simpleResult);
        }catch (Exception e){
                simpleResult.setMessage("这里 不支持加签");
                simpleResult.setSuccess(false);
                //log.error(e);
                e.printStackTrace();
            }finally {
                //清除session中的缓存
                request.getSession().removeAttribute("roles");
                return simpleResult;
            }

    }

    /**
     * 变更后的任务id
     * @param processId
     * @return
     */
    @RequestMapping("changeAfterTask")
    @ResponseBody
    public SimpleResult changeAfterTask(String processId){
        SimpleResult simpleResult = new SimpleResult();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processId).active().list();
        Task task =null;
        if(tasks!=null && tasks.size()==1){
            task = tasks.get(0);
        }else {
            for(Task task1:tasks){
                if(task1.getOwner()!=null){
                    task = task1;
                    break;
                }
            }
        }
        simpleResult.setProcessId(task.getProcessInstanceId());
        simpleResult.setTaskId(task.getId());
        return simpleResult;
    }

    /**
     * 判断是否可以加签
     * @param processId
     * @param taskId
     * @param request
     * @return
     */
    @RequestMapping("isAddActivity")
    @ResponseBody
    public SimpleResult isAddActivity(String processId,String taskId,HttpServletRequest request){
       UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
       SimpleResult simpleResult = tempActivityChangeService.isAddActivity(processId,taskId,String.valueOf(userInfo.getAdminid()));
        return  simpleResult;
    }


    /**
     * 前往加签页面
     * @param processId
     * @param taskId
     * @return
     */
    @RequestMapping("gotoAddActivityPage")
    public String gotoAddActivityPage(String processId, String taskId, Model model){
        //加签之前获取所有的人员
        model.addAttribute("processId",processId);
        model.addAttribute("taskId",taskId);
        return "jsp/addActivity";
    }



    /**
     * 前往减签
     * @param id 临时加签的id
     * @param processId
     * @param taskId
     * @return
     */
    @RequestMapping("gotoCutActivityPage")
    public String gotoCutActivityPage(Integer id,String processId, String taskId, Model model,HttpServletRequest request){
        model.addAttribute("processId",processId);
        model.addAttribute("taskId",taskId);
        JSONArray jsonArray = tempActivityChangeService.queryTempActData(id).getJSONArray("employees");
        model.addAttribute("type",tempActivityChangeService.queryTempActData(id).getInteger("type"));
        model.addAttribute("id",id);
        request.getSession().setAttribute("oldAddActivity", jsonArray);
        return "jsp/cutActivity";
    }

    /**
     * 提供选择需要减的签
     * @param request
     * @param index
     * @return
     */
    @RequestMapping("choseNeedCutActivity")
    @ResponseBody
    public SimpleResult choseNeedCutActivity(HttpServletRequest request,Integer index){
        SimpleResult simpleResult = new SimpleResult();
       JSONArray jsonArray = (JSONArray) request.getSession().getAttribute("oldAddActivity");
        jsonArray.remove(index-1);
        request.getSession().setAttribute("oldAddActivity",jsonArray);
        simpleResult.setSuccess(true);
        return simpleResult;
    }



/**
     * 见临时加的签
     * @param processId
     * @param taskId
     * @return
     *//*
    @RequestMapping("cutCurrentActivity")
    @ResponseBody
    public SimpleResult cutCurrentActivity(String processId,String taskId,HttpServletRequest request){
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        SimpleResult simpleResult = new SimpleResult();
        try{
            simpleResult=  tempActivityChangeService.cutCurrentActivity(processId,taskId,String.valueOf(userInfo.getAdminid()));
        }catch (Exception e){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("不支持减签");
            log.error(e);
        }finally {
            return  simpleResult;
        }

    }*/




   /**
     * 减临时加的签
     * @param processId
     * @param taskId
     * @return
     */
    @RequestMapping(value = "cutCurrentActivity",method = RequestMethod.POST)
    @ResponseBody
    public SimpleResult cutCurrentActivity(String taskId, String processId,String index,Integer type,HttpServletRequest request){
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        //index是不要减掉的所有号
        //获取加签的信息
        JSONArray jsonArray = (JSONArray) request.getSession().getAttribute("oldAddActivity");
        SimpleResult simpleResult = new SimpleResult();
        try{
           if(index !=null && !index.trim().equals("") ){
               //这里是有传过来值得
               String[] indexs = index.split(",");
               //这里不等于的话
               if(indexs.length!=jsonArray.size()){
                   StringBuilder userCode = new StringBuilder();
                   StringBuilder activityNames = new StringBuilder();
                   for(int i=0;i<indexs.length;i++){
                       //先执行减掉所有的签
                       simpleResult = tempActivityChangeService.cutCurrentActivity(processId, taskId, String.valueOf(userInfo.getAdminid()));
                       taskId = simpleResult.getTaskId();
                        //然后再加签
                       UserInfo user =  JSON.parseObject(jsonArray.getString(Integer.parseInt(indexs[i])),UserInfo.class);
                       if(i==index.length()-1){
                           userCode.append(user.getAdminid());
                           //这里是临时用model实际上是一个名称
                           activityNames.append(user.getAdminLogin());
                       }else {
                           userCode.append(",").append(user.getAdminid());
                           activityNames.append(user.getAdminLogin());
                       }
                   }
                   String usercodes = userInfo.getAdminid()+","+userCode.toString();
                   if(type==1){
                       tempActivityChangeService.addActivity(taskId,processId,usercodes,activityNames.toString(),String.valueOf(userInfo.getAdminid()));
                   }else {
                       tempActivityChangeService.addJoinActivity(taskId,processId,usercodes,activityNames.toString(),String.valueOf(userInfo.getAdminid()));
                   }
               }
           }
           //这里代表所有的都勾选了
           else{
               //直接执行减掉所有的
               simpleResult = tempActivityChangeService.cutCurrentActivity(processId, taskId, String.valueOf(userInfo.getAdminid()));

           }
            simpleResult.setSuccess(true);
            simpleResult.setMessage("减签成功");
          }catch (Exception e){
            simpleResult.setSuccess(false);
            simpleResult.setMessage("不支持减签");
            log.error("减签异常",e);
        }finally {
            return  simpleResult;
        }

    }


    /**
     * 添加会签的人
     * @param taskId
     * @param processId
     * @return
     */
    @RequestMapping("goToJoinProcess")
    public String goToJoinProcess(String taskId,String processId,Model model){
        model.addAttribute("processId",processId);
        model.addAttribute("taskId",taskId);
        return "jsp/addJoin";
    }

    /**
     * 加签排序
     * @param index
     * @param request
     */
    @RequestMapping("order")
    @ResponseBody
    public SimpleResult orderBy(Integer index,HttpServletRequest request){
        SimpleResult simpleResult = new SimpleResult();
       List<Employeerole> list = (List<Employeerole>) request.getSession().getAttribute("roles");
        //这里才需要排序
        Employeerole employeerole;
        //第二个位置
      if(index>1){
          employeerole =  list.get(index-1);
          list.remove(index-1);
          list.add(index-2,employeerole);
         }
      request.getSession().setAttribute("roles",list);
        return simpleResult;
    }


    /**
     * 加签排序
     * @param index
     * @param request
     */
    @RequestMapping("orderDown")
    @ResponseBody
    public SimpleResult orderByDown(Integer index,HttpServletRequest request){
        SimpleResult simpleResult = new SimpleResult();
        List<Employeerole> list = (List<Employeerole>) request.getSession().getAttribute("roles");
        //这里才需要排序
        if(index<list.size()) {
            Employeerole employeerole;
            //第二个位置
            employeerole = list.get(index - 1);
            list.remove(index - 1);
            list.add(index, employeerole);
        }
        request.getSession().setAttribute("roles",list);
        return simpleResult;
    }

    @RequestMapping("addRole")
    @ResponseBody
    public SimpleResult addRole(String adminid,String remark, HttpServletRequest request){
        SimpleResult simpleResult = new SimpleResult();
        List<Employeerole> list = (List<Employeerole>) request.getSession().getAttribute("roles");
        if(list==null || list.size()==0){
            list = new ArrayList<Employeerole>();
        }
        Employeerole employeerole = this.queryEmployeeById(adminid);
        if(employeerole==null){
            return simpleResult;
        }
            employeerole.setRemark(remark);
        //如果不包含的话
        if(!list.contains(employeerole)){
            list.add(employeerole);
        }
        request.getSession().setAttribute("roles",list);
        simpleResult.setSuccess(true);
        return simpleResult;
    }


    @RequestMapping("addPerson")
    @ResponseBody
    public SimpleResult addPerson(String adminid, HttpServletRequest request){
        //获取adminids
        SimpleResult simpleResult = new SimpleResult();
        List<Employeerole> list = (List<Employeerole>) request.getSession().getAttribute("adminIds");
        if(list==null || list.size()==0){
            list = new ArrayList<Employeerole>();
        }
       Employeerole employeerole =  this.queryEmployeeById(adminid);
        if(employeerole==null){
            return simpleResult;
        }
        if(!list.contains(employeerole)){
            list.add(employeerole);
        }
        request.getSession().setAttribute("adminIds",list);
        simpleResult.setSuccess(true);
        return simpleResult;
    }


    /**
     * 会签操作
     *
     * @param taskId    当前任务ID
     * @param  taskId 会签人账号集合
     * @throws Exception
     */
    @RequestMapping(value = "joinProcess",method = RequestMethod.POST)
    @ResponseBody
    public SimpleResult joinProcess(String taskId,String processId,String userCode,HttpServletRequest request){
        SimpleResult simpleResult = new SimpleResult();
        if(userCode==null || userCode.equals("")){
            simpleResult.setMessage("请设置需要会签的人");
            return simpleResult;
        }
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        //加上本人的信息
        //本人信息在最后面的
        userCode = userCode.concat(",")+userInfo.getAdminid();
        try {
            tempActivityChangeService.jointProcess(taskId,processId,userCode);
            simpleResult.setSuccess(true);
            simpleResult.setMessage("设置会签成功");
        } catch (Exception e) {
            simpleResult.setSuccess(false);
            simpleResult.setMessage("设置会签失败");
            e.printStackTrace();
        }finally {
            request.getSession().removeAttribute("adminIds");
            return simpleResult;
        }
    }


    /**
     * 根据员工调用对应的接口获取员工信息
     * @param adminid
     * @return
     */
    private Employeerole queryEmployeeById(String adminid){
        JSONArray jsonArray = EhrRequestApiUtils.getUserInfoByUserId(adminid,getRoleByUserId);
        if(jsonArray==null){
            return null;
        }
        //只需要第一个人的对象
        Employeerole  employeerole = EhrUserDataHandleUtils.handleOnlyUserData(jsonArray.getJSONObject(0));
        return employeerole;
    }


}
