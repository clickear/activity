package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.RmsMenu;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.util.Constants;
import cn.huimin.process.web.util.HttpClientUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import cn.huimin.web.jwt.sign.SHA256Sign;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseArray;


/**
 * 单点登录的权限系统认证
 */
@Controller
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(AuthController.class);
    //退出单点登录系统
    @Value("${orgLoginOut}")
     private  String orgLoginOut;
    @Value("${rmsUrl}")
    private String rmsUrl;
    @Value("${checkPer}")
    private String checkPer;
    @Value("${systemKey}")
    private String systemKey;
    @Value("${getPer}")
    private String getPer;

    @RequestMapping("")
    public String home(){
    	return "redirect:index";
    }
    
    @RequestMapping("index")
    public String index(HttpServletRequest request,HttpServletResponse response) {
        //UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        //直接跳转到获取
        Assertion assertion = (Assertion) request.getSession().getAttribute(
                AbstractCasFilter.CONST_CAS_ASSERTION);
        AttributePrincipal principal = assertion.getPrincipal();
        String userJson = "";
        List<UserInfo> list = new ArrayList();
        if (principal != null) {
            userJson = principal.getName();
            JSONArray userArray = parseArray(userJson);
            if (userArray.size() == 1) {
                JSONObject jsonObject = userArray.getJSONObject(0);
                if (userJson != null) {
                    UserInfo admin = new UserInfo();
                    admin.setAdminid(jsonObject.getInteger("adminid"));
                    admin.setAdminname(jsonObject.getString("adminname"));
                    admin.setBranchname(jsonObject.getString("branchname"));
                    admin.setBranchid(jsonObject.getInteger("branchid"));
                    admin.setDepartmentid(jsonObject.getInteger("departmentId"));
                    admin.setSpecrolename(jsonObject.getString("specrolename"));
                    admin.setDepartmentname(jsonObject.getString("departmentname"));
                    admin.setRoleid(jsonObject.getInteger("roleid"));
                    list.add(admin);
                }
            } else {
                for (int i = 0; i < userArray.size(); i++) {
                    JSONObject userList = userArray.getJSONObject(i);
                    UserInfo admin = new UserInfo();
                    admin.setAdminid(userList.getInteger("adminid"));
                    admin.setAdminname(userList.getString("adminname"));
                    admin.setBranchname(userList.getString("branchname"));
                    admin.setBranchid(userList.getInteger("branchid"));
                    admin.setDepartmentid(userList.getInteger("departmentId"));
                    admin.setSpecrolename(userList.getString("specrolename"));
                    admin.setDepartmentname(userList.getString("departmentname"));
                    admin.setRoleid(userList.getInteger("roleid"));
                    admin.setId(userList.getInteger("id"));
                    list.add(admin);
                }

            }
            request.getSession().setAttribute("userArray",userArray);
        }
        request.getSession().setAttribute(Constants.userInfo, list.get(0));

        Object attribute1 = request.getSession().getAttribute(Constants.menus);
        //如果为空的话
        if(list.get(0)!=null&&attribute1==null){
            handeMenu(request,list.get(0).getAdminid());
        }
        request.getSession().setAttribute(Constants.userInfos, list);
        return "jsp/index";
    }

    private void handeMenu(HttpServletRequest request,Integer userId){
        JSONObject jsonObject = new JSONObject();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", String.valueOf(userId));
        params.put("sysKey",String.valueOf(systemKey));
        params = SHA256Sign.sign(params, "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N");
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime", ctime);
        jsonObject.put("nonce", nonce);
        jsonObject.put("sign", sign);
        jsonObject.put("userId", userId);
        jsonObject.put("sysKey",systemKey);
        logger.info("请求用户菜单", jsonObject);
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(rmsUrl+getPer, jsonObject);
        if (!ObjectCheckUtils.isEmptyString(jsonResponse)) {
            JSONObject jsonObject1 = JSONObject.parseObject(jsonResponse);
            if (jsonObject1 != null && jsonObject1.getBoolean("success")) {
                //执行操作
                String result = jsonObject1.getString("result");
                if(!ObjectCheckUtils.isEmptyString(result)){
                    List<RmsMenu> rmsMenus = JSONObject.parseArray(result, RmsMenu.class);
                    request.getSession().setAttribute(Constants.menus,rmsMenus);
                }
            }
        }
    }



    /**
     * 前往流程系统对外的接口
     *
     * @param request
     * @return
     */
    @RequestMapping("goToActivity")
    public String getAdminInfo(HttpServletRequest request, Integer id) {
        // 获得cas服务端登陆的用户信息
        //获取相同的信息
        Assertion assertion = (Assertion) request.getSession().getAttribute(
                AbstractCasFilter.CONST_CAS_ASSERTION);
        AttributePrincipal principal = assertion.getPrincipal();
        String userJson = "";
        UserInfo admin = new UserInfo();
        if (principal != null) {
            userJson = principal.getName();
            JSONArray userArray = parseArray(userJson);
            //JSONObject jsonObject =  userArray.getJSONObject(0);
            if (userArray == null || userArray.size() == 0) {
                return "redirect:logOut";
            }
            for (int i = 0; i < userArray.size(); i++) {
                if (userArray.getJSONObject(i).getString("id").equals(String.valueOf(id))) {
                    JSONObject jsonObject = userArray.getJSONObject(i);
                    admin.setAdminid(jsonObject.getInteger("adminid"));
                    admin.setAdminname(jsonObject.getString("adminname"));
                    admin.setBranchname(jsonObject.getString("branchname"));
                    admin.setBranchid(jsonObject.getInteger("branchid"));
                    admin.setDepartmentid(jsonObject.getInteger("departmentId"));
                    admin.setSpecrolename(jsonObject.getString("specrolename"));
                    admin.setDepartmentname(jsonObject.getString("departmentname"));
                    admin.setRoleid(jsonObject.getInteger("roleid"));
                }
            }
        }
        request.getSession().setAttribute(Constants.userInfo, admin);
        //返回页面
        return "jsp/welcome";
    }


    //登出操作
    @RequestMapping(value = "logOut")
    public String logOut(HttpServletRequest request, HttpServletResponse response) {
        //清除获取的菜单信息
        request.getSession().removeAttribute(Constants.menus);
        request.getSession().removeAttribute(Constants.userInfo);
        //清除用户信息
        return "redirect:"+orgLoginOut;
    }

    @RequestMapping("removeSession")
    @ResponseBody
    public SimpleResult clearSession(HttpServletRequest request) {
        SimpleResult simpleResult = new SimpleResult();
        request.getSession().removeAttribute(Constants.userInfo);
        simpleResult.setSuccess(true);
        return simpleResult;
    }
}
