
package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.util.EhrRequestApiUtils;
import cn.huimin.process.web.util.EhrUserDataHandleUtils;
import cn.huimin.process.web.util.HttpClientUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import cn.huimin.web.jwt.sign.SHA256Sign;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/27.
 */
@RequestMapping("adminRole")
@Controller
public class AdminRoleController {


    private final static Logger logger = LoggerFactory.getLogger(AdminRoleController.class);

    /**
     * 调用接口获取部门下的所有岗位
     */
    @Value("${getAllRoleByOrgId}")
    private String getAllRoleByOrgId;


    /**
     * 调用接口获取部门下的所有员工
     */
    @Value("${getAllUserByOrgId}")
    private String getAllUserByOrgId;

    @Value("${getUserByUserLoginOrUserName}")
    private String getUserByUserLoginOrUserName;


    @Value("${getRoleByRoleName}")
    private String getRoleByRoleName;

    @Value("${getAllRoleInfo}")
    private String getAllRoleInfo;
    @Value("${organzationAll}")
    private String organzationAll;


    /**
     * 根据 部门id查询部门下的所有岗位 包括下面的
     *
     * @param orgId
     * @return
     */
    @RequestMapping(value = "queryNoPage", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject queryNoPage(String orgId, HttpServletRequest request) {
        if (orgId == null || "".equals(orgId)) {
            return null;
        }
        logger.warn("请求的部门id参数 {}", orgId);
        JSONObject jsonObject = new JSONObject();
        Map<String, String> params = new HashMap<String, String>();
        params.put("org_id", orgId);
        params = SHA256Sign.sign(params, "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N");
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime", ctime);
        jsonObject.put("nonce", nonce);
        jsonObject.put("sign", sign);
        jsonObject.put("org_id", orgId);
        logger.info("远程获取部门下的所有岗位参数 {}", jsonObject);
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(getAllRoleByOrgId, jsonObject);
        logger.info("远程获取部门下的所有岗位: {}", jsonResponse);
        JSONObject dataObject = JSONObject.parseObject(jsonResponse);
        //获取部门
        JSONArray jsonArray = dataObject.getJSONArray("data");
        //返回给页面的数据
        if (jsonArray == null) {
            return null;
        }
        JSONObject jsonResponsePage = new JSONObject();
        jsonResponsePage.put("data", jsonArray);
        logger.info("请求岗位响应给页面的数据 {}", jsonResponsePage);
        return jsonResponsePage;
    }


    /**
     * 通过部门id获取部门下的所有员工
     *
     * @param userName 用户名 模糊查询自动补全
     * @return
     */
    @RequestMapping(value = "getUserByUserLoginOrUserName", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray getUserByUserLoginOrUserName(String userName, HttpServletRequest request) {
        //如果等于null则代表第一次访问
        if (userName == null || "".trim().equals(userName)) {
            return null;
        }
        JSONObject jsonObject  = this.getUsersByUserName(userName);
        if(jsonObject==null){
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        jsonArray = EhrUserDataHandleUtils.handleUsersDataInfo(jsonArray);
        logger.info("给页面传入参数的数据 {}", jsonArray);
        return jsonArray;
    }


    /**
     * 通过部门id获取部门下的所有员工 分页
     *
     * @param userName 用户名 模糊查询自动补全
     * @return
     */
    @RequestMapping(value = "getUserByUserLoginOrUserNamePage", method = RequestMethod.POST)
    @ResponseBody
    public Page getUserByUserLoginOrUserNamePage(String userName, @RequestParam(defaultValue = "1") String page, @RequestParam(defaultValue = "5") String pageSize, HttpServletRequest request) {
        //如果等于null则代表第一次访问
        JSONObject jsonObject  = this.getUsersByUserNamePage(userName,pageSize,page);
        if(jsonObject==null){
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        jsonArray = EhrUserDataHandleUtils.handleUsersDataInfo(jsonArray);
        logger.info("给页面传入参数的数据 {}", jsonArray);
        Page pageJson = new Page();
        pageJson.setTotalPage(jsonObject.getInteger("totalPages"));
        pageJson.setTotal(jsonObject.getLong("totalCount"));
        pageJson.setRows(jsonArray);
        pageJson.setPage(Integer.parseInt(page));
        return pageJson;
    }

    /**
     * 获取岗位字典名称
     *
     * @param userName
     * @param request
     * @return
     */
    @RequestMapping(value = "organzationAll", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray organzationAll(String userName, HttpServletRequest request) {
        //如果等于null则代表第一次访问
        if (userName == null || "".trim().equals(userName)) {
            return null;
        }
        try {
            userName = URLEncoder.encode(userName, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = EhrRequestApiUtils.getAllOrganization(userName, organzationAll);
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        if (logger.isInfoEnabled()) {
            logger.info("AdminRoleController.organzationAll {}", jsonArray);
        }
        return jsonArray;
    }


    /**
     * 获取岗位字典名称
     *
     * @param userName
     * @param request
     * @return
     */
    @RequestMapping(value = "getAllRoleInfo", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray getAllRoleInfo(String userName, HttpServletRequest request) {
        //如果等于null则代表第一次访问
        if (userName == null || "".trim().equals(userName)) {
            return null;
        }
        try {
            userName = URLEncoder.encode(userName, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = EhrRequestApiUtils.getAllRoleInfo(userName, getAllRoleInfo);
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        if (logger.isInfoEnabled()) {
            logger.info("AdminRoleController.getAllRoleInfo {}", jsonArray);
        }
        return jsonArray;
    }

    /**
     * 获取部门类别
     * @return
     */
    @RequestMapping(value = "getDepartmetnType", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray getDepartmetnType() {
        JSONArray jsonArray = EhrRequestApiUtils.getMajorTypeByDictionaryId(null,15);
        if (logger.isInfoEnabled()) {
            logger.info("AdminRoleController.getDepartmetnType {}", jsonArray);
        }
        return jsonArray;
    }


    /**
     * 根据 岗位模糊查询岗位信息 加分页
     *
     * @param userName
     * @param request
     * @return
     */
    @RequestMapping(value = "getRoleByRoleName", method = RequestMethod.POST)
    @ResponseBody
    public Page getRoleByRoleName(String userName, @RequestParam(defaultValue = "1") String page, @RequestParam(defaultValue = "5") String pageSize, HttpServletRequest request) {
        //如果等于null则代表第一次访问
        try {
            if(ObjectCheckUtils.isEmptyString(userName)){
                userName ="";
            }else {
                userName = URLEncoder.encode(userName, "UTF-8");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = EhrRequestApiUtils.getRoleByRoleName(userName, page, pageSize, getRoleByRoleName);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        jsonArray = EhrUserDataHandleUtils.handleRolesData(jsonArray);
        logger.info("给页面传入参数的数据 {}", jsonArray);
        Page pages = new Page();
        pages.setTotalPage(jsonObject.getInteger("totalPages"));
        pages.setTotal(jsonObject.getLong("totalCount"));
        pages.setRows(jsonArray);
        pages.setPage(Integer.parseInt(page));
        return pages;
    }


    /**
     * 通过用户名 部门公司分开 分开的
     *
     * @param userName 用户名
     * @return
     */
    @RequestMapping(value = "getUserByUserLoginOrUserNameList", method = RequestMethod.POST)
    @ResponseBody
    public JSONArray getUserByUserLoginOrUserNameList(String userName, HttpServletRequest request) {
        //如果等于null则代表第一次访问
        if (userName == null || "".trim().equals(userName)) {
            return null;
        }
        JSONObject jsonObject  = this.getUsersByUserName(userName);
        if (jsonObject == null) {
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        /**
         * 对获取的数据进行处理
         */
        jsonArray = EhrUserDataHandleUtils.handleUsersData(jsonArray);
        logger.info("给页面传入参数的数据 {}", jsonArray);
        return jsonArray;
    }


    /**
     * 通过员工姓名获取员工
     *
     * @param userName
     * @return
     */
    private JSONObject getUsersByUserName(String userName) {
        logger.warn("请求的员工姓名 {}", userName);
        try {
            userName = URLEncoder.encode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_name", userName);
        params = SHA256Sign.sign(params, "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N");
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime", ctime);
        jsonObject.put("nonce", nonce);
        jsonObject.put("sign", sign);
        jsonObject.put("user_name", userName);
        logger.info("远程请求员工信息请求参数 {}", jsonObject);
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(getUserByUserLoginOrUserName, jsonObject);
        logger.info("远程获取员工信息的响应数据 {}", jsonResponse);
        if (jsonResponse == null || "".equals(jsonResponse)) {
            return null;
        }
        JSONObject dataObject = JSONObject.parseObject(jsonResponse);
        //获取部门
        //JSONArray jsonArray = dataObject.getJSONArray("data");
        return dataObject;

    }

    /**
     * 通过员工姓名获取员工
     *
     * @param userName
     * @return
     */
    private JSONObject getUsersByUserNamePage(String userName,String pageSize,String pageNo) {
        logger.warn("请求的员工姓名 {}", userName);
        try {
            if(ObjectCheckUtils.isEmptyString(userName)){
                userName ="";
            }else {
                userName = URLEncoder.encode(userName, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_name", userName);
        params.put("pageNo",pageNo);
        params.put("pageSize",pageSize);
        params = SHA256Sign.sign(params, "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N");
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime", ctime);
        jsonObject.put("nonce", nonce);
        jsonObject.put("sign", sign);
        jsonObject.put("user_name", userName);
        jsonObject.put("pageNo",pageNo);
        jsonObject.put("pageSize",pageSize);
        logger.info("远程请求员工信息请求参数 {}", jsonObject);
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(getUserByUserLoginOrUserName, jsonObject);
        logger.info("远程获取员工信息的响应数据 {}", jsonResponse);
        if (jsonResponse == null || "".equals(jsonResponse)) {
            return null;
        }
        JSONObject dataObject = JSONObject.parseObject(jsonResponse);
        //获取部门
        //JSONArray jsonArray = dataObject.getJSONArray("data");
        return dataObject;

    }


}
