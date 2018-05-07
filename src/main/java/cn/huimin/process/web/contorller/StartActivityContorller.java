package cn.huimin.process.web.contorller;

import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.InnerBusinessVarConstants;
import cn.huimin.process.web.dto.APISimpleResult;
import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.ProcessDefEntity;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.service.LogService;
import cn.huimin.process.web.service.ProcessDefinitionService;
import cn.huimin.process.web.service.ProcessServiceEx;
import cn.huimin.process.web.util.Constants;
import cn.huimin.process.web.util.ExpectionUtils;
import cn.huimin.process.web.util.ProcessInstanceUtils;
import cn.huimin.process.web.util.ExpectionUtils.ErrorInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动流程
 */
@Controller
@RequestMapping("startActivity")
public class StartActivityContorller {

    private static final transient Logger log = LoggerFactory.getLogger(StartActivityContorller.class);



    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @Autowired
    private HistoryService historyService;


    @Autowired
    private ProcessServiceEx processServiceEx;


    /**
     * 可以启动的所有流程
     *
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "startActivityPage", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Page startActivityPage(ProcessDefEntity processDefEntity, Integer page, Integer pageSize, HttpServletRequest request) {
        Page pageJson = new Page();
        PageInfo<ProcessDefEntity> pageInfo = processDefinitionService.queryPageAllProcess(processDefEntity, page, pageSize);
        pageJson.setTotal(pageInfo.getTotal());
        pageJson.setRows(pageInfo.getList());
        pageJson.setPage(pageInfo.getPrePage());
        pageJson.setTotalPage(pageInfo.getPages());
        log.info("startActivityPage {}", pageJson);
        return pageJson;
    }


    @RequestMapping(value = "startProcess", method = RequestMethod.POST)
    @ResponseBody
    public SimpleResult startProcess(String key, String formData, HttpServletRequest request) {
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startUserId", userInfo.getAdminid());
        map.put("departmentLevel", "1");
        map.put("leader", false);
        map.put(InnerBusinessVarConstants.BRANCH_ID, "117");
        map.put(InnerBusinessVarConstants.PROCESS_INSTANCE_NAME,userInfo.getDepartmentname()+"-"+userInfo.getAdminname()+"员工转正");
        JSONObject formDataH = new JSONObject();
        //业务数据
        JSONArray jsonArray = JSON.parseArray(formData);
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String name1 = jsonObject1.getString("name");
                Object value1 = jsonObject1.get("value");
                formDataH.put(name1, value1);
            }
        }
        //执行发起
        map.put(TestApiConstants.FORM_DATA_TEST, formDataH.toJSONString());
        map.put(TestApiConstants.processInstanceName, userInfo.getAdminname().concat("-").concat(userInfo.getDepartmentname()).concat(""));
        //启动成功
        SimpleResult simpleResult = new SimpleResult();
        APISimpleResult apiSimpleResult = new APISimpleResult();
        try {
            processServiceEx.start(map, String.valueOf(userInfo.getAdminid()), key, TestApiConstants.SYSTEM_ID_VALUE, apiSimpleResult);
            simpleResult.setSuccess(true);
            simpleResult.setMessage("发起成功");
        } catch (Exception e) {
        	ErrorInfo errorInfo = ExpectionUtils.getConditionErrorMsg(e);
        	simpleResult.setErrorMsg(errorInfo.errorMsg);
        	simpleResult.setErrorCode(errorInfo.errorCode);
            simpleResult.setSuccess(false);
            simpleResult.setMessage("发起失败");
            log.error("startProcess", e);
        } finally {
            return simpleResult;
        }
    }

    /**
     * 前往流程key
     *
     * @param processKey
     * @return
     */
    @RequestMapping("qotoStartForm")
    public String qotoStartForm(String processKey, Model model) {
        model.addAttribute("processKey", processKey);
        return "jsp/startForm";
    }
    @Autowired
    private LogService logService;


    //
    @RequestMapping("inviteForm")
    public String gotoInvitePage2(String taskId, String processId, Model model) throws UnsupportedEncodingException {
        //通过表单
        String val = String.valueOf(ProcessInstanceUtils.getHistoryVar(historyService,processId, TestApiConstants.FORM_DATA_TEST));
        if (JSONObject.parseObject(val) == null) {
            model.addAttribute("dataInfo", "");
        } else {
            model.addAttribute("dataInfo", JSONObject.parseObject(val));
        }
        List<JSONObject> list = logService.queryCheckLogByProcessInstanceId(processId, InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO);
        //只是用来展示的 0是发起 1是展示 2 编辑
        model.addAttribute("show", 1);
        model.addAttribute("historyAdviceList", list);
        //历史操作痕迹
        return "jsp/form";
    }


}
