package cn.huimin.process.web.util;

import cn.huimin.process.web.model.Employeerole;
import cn.huimin.web.jwt.sign.SHA256Sign;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by wyp on 2017/2/23.
 * ehr人员信息数据处理
 */
public class EhrUserDataHandleUtils {

    private final  static String key = "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N";


    /**
     * 单个人员信息的处理
     * @param jsonObject
     * @return 员工部门岗位和人员姓名
     */
    public static Employeerole handleOnlyUserData(JSONObject jsonObject){
        Employeerole employeerole = new Employeerole();
          StringBuilder sb = new StringBuilder();
        //获取用户的每个用户的数据
        //jsonObject 为一个用户的数据
            JSONObject newRoleAPI = jsonObject.getJSONObject("newRoleAPI");
            JSONArray organs = newRoleAPI.getJSONArray("organs");
            if(organs!=null && organs.size()>0){
                for(int i=0;i<organs.size();i++){
                    String name = organs.getJSONObject(i).getString("org_name");
                    if(i ==0){
                        sb.append(name);
                    }else {
                        sb.append("-").append(name);
                    }
                }
            }
            //添加部门
            employeerole.setDepartmentname(sb.toString());
            //添加岗位
            employeerole.setSpecrolename(jsonObject.getString("role_name"));
            employeerole.setAdminname(jsonObject.getString("user_name"));
                //用户信息
            employeerole.setAdminid(jsonObject.getInteger("user_id"));
         return employeerole;
    }


    /**
     * 获取岗位下的所有人员并进行处理
     * @param jsonArray
     * @return
     */
    public static JSONArray handleRoleData(JSONArray jsonArray){
        for(int i=0;i<jsonArray.size();i++){
            //获取用户的每个用户的数据
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            //用户信息
            String id =jsonObject1.getString("user_id");
            String orId = jsonObject1.getString("or_id");
            String orOrgId = jsonObject1.getString("or_org_id:");
            jsonObject1.clear();
            jsonObject1.put("adminid",id);
            jsonObject1.put("roleid",orId);
            jsonObject1.put("departmentid",orOrgId);

        }
        return jsonArray;
    }



    /**
     * 第一种是 公司部门 和 岗位 姓名 分开
     * @param jsonArray
     * @return
     */
    public static JSONArray handleUsersData(JSONArray jsonArray){
        if(jsonArray == null || jsonArray.size()==0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<jsonArray.size();i++){
            //获取用户的每个用户的数据
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            JSONObject newRoleAPI = jsonObject1.getJSONObject("newRoleAPI");
            JSONArray organs = newRoleAPI.getJSONArray("organs");
            if(organs!=null && organs.size()>0){
                for(int j=0;j<organs.size();j++){
                    String name = organs.getJSONObject(j).getString("org_name");
                    if(j ==0){
                        sb.append(name);
                    }else {
                        sb.append("-").append(name);
                    }
                }

            }
            //用户信息
            String roleName = jsonObject1.getString("role_name");
            String userName = jsonObject1.getString("user_name");
            String id =jsonObject1.getString("user_id");
            String orId = jsonObject1.getString("or_id");
            String orOrgId = jsonObject1.getString("or_org_id:");
            jsonObject1.clear();
            jsonObject1.put("departmentname",sb.toString());
            jsonObject1.put("adminid",id);
            jsonObject1.put("roleid",orId);
            jsonObject1.put("departmentid",orOrgId);
            jsonObject1.put("specrolename",roleName);
            jsonObject1.put("adminname",userName);
            sb.delete(0,sb.length());
        }
       return  jsonArray;
    }


    /**
     * 第二种是公司部门岗位姓名不分开的
     * 这里只是用于模糊和自动补全使用 单独
     * @param jsonArray
     * @return
     */
    public static JSONArray handleUsersDataInfo(JSONArray jsonArray){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<jsonArray.size();i++){
            //获取用户的每个用户的数据
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            JSONObject newRoleAPI = jsonObject1.getJSONObject("newRoleAPI");
            JSONArray organs = newRoleAPI.getJSONArray("organs");
            if(organs!=null && organs.size()>0){
                for(int j=0;j<organs.size();j++){
                    String name = organs.getJSONObject(j).getString("org_name");
                    if(j ==0){
                        sb.append(name);
                    }else {
                        sb.append("-").append(name);
                    }
                }
            }
            //用户信息
            sb.append("-").append(jsonObject1.getString("role_name")).append("-").append(jsonObject1.getString("user_name"));
            String id =jsonObject1.getString("user_id");
            jsonObject1.clear();
            jsonObject1.put("name",sb.toString());
            jsonObject1.put("id",id);
            sb.delete(0,sb.length());
        }
        return  jsonArray;
    }




    /**
     * 对岗位信息处理信息
     * @param jsonArray
     * @return
     */
    public static JSONArray handleRolesData(JSONArray jsonArray){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<jsonArray.size();i++){
            //获取用户的每个用户的数据
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            JSONArray organs = jsonObject1.getJSONArray("organs");
            if(organs!=null && organs.size()>0){
                for(int j=0;j<organs.size();j++){
                    String name = organs.getJSONObject(j).getString("org_name");
                    if(j ==0){
                        sb.append(name);
                    }else {
                        sb.append("-").append(name);
                    }
                }
            }
            sb.append("-").append(jsonObject1.getString("role_name"));
            String roleid = jsonObject1.getString("or_id");
            jsonObject1.clear();
            jsonObject1.put("name",sb.toString());
            jsonObject1.put("id",roleid);
            sb.delete(0,sb.length());
        }
        return  jsonArray;
    }


    /**
     *处理单个的
     * @param  jsonObject1 只为拼接数据处理
     * @return
     */
    public static JSONObject handleUsersDataInfo(JSONObject jsonObject1){
        StringBuilder sb = new StringBuilder();
            //获取用户的每个用户的数据
            JSONObject newRoleAPI = jsonObject1.getJSONObject("newRoleAPI");
            JSONArray organs = newRoleAPI.getJSONArray("organs");
            if(organs!=null && organs.size()>0){
                for(int j=0;j<organs.size();j++){
                    String name = organs.getJSONObject(j).getString("org_name");
                    if(j ==0){
                        sb.append(name);
                    }else {
                        sb.append("-").append(name);
                    }
                }
            }
            //用户信息
            sb.append("-").append(jsonObject1.getString("role_name")).append("-").append(jsonObject1.getString("user_name"));
            String id =jsonObject1.getString("user_id");
            jsonObject1.clear();
            jsonObject1.put("name",sb.toString());
            jsonObject1.put("id",id);
             return  jsonObject1;
    }

    /**
     * 对岗位信息进行处理返回岗位详情
     * @param jsonObject
     * @return
     */
    public static String handleRoleInfo(JSONObject jsonObject){
        StringBuffer sb = new StringBuffer();
        JSONArray organs = jsonObject.getJSONArray("organs");
        if(organs!=null && organs.size()>0){
            for(int j=0;j<organs.size();j++){
                String name = organs.getJSONObject(j).getString("org_name");
                if(j ==0){
                    sb.append(name);
                }else {
                    sb.append("-").append(name);
                }
            }
        }
        sb.append("-").append(jsonObject.getString("role_name"));
        return  sb.toString();
    }


    /**
     * 对岗位信息进行处理返回岗位详情
     * @param jsonObject
     * @return
     */
    public static JSONObject handleDepartmentInfo(JSONObject jsonObject){
        StringBuffer stringBuffer = new StringBuffer();
        JSONArray js = jsonObject.getJSONArray("organs");
        for(int j=0;j<js.size();j++){
            if(j==0){
                stringBuffer.append(js.getJSONObject(j).getString("org_name"));
            }else {
                stringBuffer.append("-").append(js.getJSONObject(j).getString("org_name"));
            }
        }
        //  "org_name"
        jsonObject.put("org_name",stringBuffer.toString());
        return  jsonObject;
    }



    /**
     * 根据用户id获取上级部门的负责人
     * @param userId
     * @return
     */
    public static Set<String> getLeaderByUserId(String userId){
        //获取对应的部门
        JSONArray jsonArray =  EhrRequestApiUtils.getUserInfoByUserId(userId,ReadFileUtils.readProperties("url","getRoleByUserId"));
        String orgId="";
        String orgLayer = "";
        String departmetnLeader  = "";
        if(jsonArray!=null&&jsonArray.size()>0){
            for(int i=0;i<jsonArray.size();i++){
                //获取前一个处理人的信息
               if("1".equals(jsonArray.getJSONObject(i).getString("uor_ismain"))){
                   orgId = jsonArray.getJSONObject(i).getString("or_org_id");
                   orgLayer = jsonArray.getJSONObject(i).getString("org_layer");
                   departmetnLeader = jsonArray.getJSONObject(i).getString("org_leader_id");
                   break;
               }
            }
            if("".equals(orgId)){
                orgId = jsonArray.getJSONObject(0).getString("or_org_id");
                orgLayer = jsonArray.getJSONObject(0).getString("org_layer");
                departmetnLeader = jsonArray.getJSONObject(0).getString("org_leader_id");
            }
        }
        Set<String> set = new HashSet<>(0);
        //获取组织层次结构
        if(!"".equals(orgLayer)){
            String[] strings = orgLayer.trim().split("\\|");
            strings =StringUtils.split(orgLayer,"\\|");
            if(strings.length==1){
                return set;
            }
            if(strings.length ==2){
                if(!ObjectCheckUtils.isEmptyString(departmetnLeader)){

                    set.add(departmetnLeader);
                }
                return set;
           }
       }
        if(1==1){
            return set;
        }
         set = EhrRequestApiUtils.getParDempartmentInfo(orgId);
        //如果不相同的话
        return set;
    }

    /**
     * 根据用户id获取上级部门的负责人
     * @param userId
     * @return
     */
    public static List<String> getPara(String userId){
        //获取对应的部门
        JSONArray jsonArray =  EhrRequestApiUtils.getUserInfoByUserId(userId,ReadFileUtils.readProperties("url","getRoleByUserId"));
        //or_org_id:
        String orgId="";
        if(jsonArray!=null&&jsonArray.size()>0){
            for(int i=0;i<jsonArray.size();i++){
                if("1".equals(jsonArray.getJSONObject(i).getString("uor_ismain"))){
                    orgId = jsonArray.getJSONObject(i).getString("or_org_id");
                    break;
                }
            }
            if("".equals(orgId)){
                orgId = jsonArray.getJSONObject(0).getString("or_org_id");

            }
        }
        List<String> list = new ArrayList<>();
        //循环找到当前人的部门领导负责人
        while (!"1".equals(orgId)){
            //找到后终止循环获取组织岗位id
            JSONArray jsonArray1 = EhrRequestApiUtils.getOrganizationByOrgId(orgId);
            if(jsonArray1!=null&&jsonArray1.size()>0) {
                for (int j = 0; j < jsonArray1.size(); j++) {
                    //获取部门负责人
                    list.add(jsonArray1.getJSONObject(j).getString("org_leader_id"));
                }
                if(list.contains(userId)){
                    //走上级部门
                    orgId = jsonArray1.getJSONObject(1).getString("org_pid");
                    list = null;
                }
                //不包含的话直接加上
                else {
                    break;
                }
            }else {
                break;
            }
        }
        //如果不相同的话
        return list;
    }





    /**
     * 根据用户id获取部门部门负责人
     * @param userId
     * @return
     */
    public static List<String> getLeaderId(String userId){
        //获取对应的部门
        JSONArray jsonArray =  EhrRequestApiUtils.getUserInfoByUserId(userId,ReadFileUtils.readProperties("url","getRoleByUserId"));
        List<String> list = new ArrayList<>(0);
        JSONObject jsonObject =null;
        //找部门负责人
        if(jsonArray!=null&&jsonArray.size()>0){
            for (int i=0;i<jsonArray.size();i++){
                if("1".equals(jsonArray.getJSONObject(i).getString("uor_ismain"))){
                    jsonObject = jsonArray.getJSONObject(i);
                }
            }
            if(jsonObject==null){
                jsonObject = jsonArray.getJSONObject(0);
            }
        }
        //用户对象发起
        if(jsonObject!=null){
            //领导id
            String leaderId =jsonObject.getString("org_leader_id");
            list.add(leaderId);
        }
        return list;

    }
















    /**
     * 一套获取用户详情
     * @param adminId
     * @return
     */
    public static String employeeInfo(String adminId,String url){
        JSONArray jsonArray = EhrRequestApiUtils.getUserInfoByUserId(adminId,url);
        if (jsonArray == null || jsonArray.size()==0){
            return null;
        }
        JSONObject jsonObject = EhrUserDataHandleUtils.handleUsersDataInfo(jsonArray.getJSONObject(0));
        //获取第一个作为主要的信息
        String applayNameInfo = jsonObject.getString("name");
        return  applayNameInfo;
    }

    /**
     * 一套获取用户详情
     * @param adminId
     * @return
     */
    public static String employeeInfo(String adminId){
        JSONArray jsonArray = EhrRequestApiUtils.getUserInfoByUserId(adminId,ReadFileUtils.readProperties("url","getRoleByUserId"));
        if (jsonArray == null || jsonArray.size()==0){
            return null;
        }
        JSONObject jsonObject = EhrUserDataHandleUtils.handleUsersDataInfo(jsonArray.getJSONObject(0));
        //获取第一个作为主要的信息
        String applayNameInfo = jsonObject.getString("name");
        return  applayNameInfo;
    }


    /**
     * 根据分公司id和岗位id获取所有的人员
     * @param orgId 组织id
     * @param roleId 岗位id
     * @return
     */
    public static Set<String> getAllUserByOrgId(String orgId, String roleId,String url){
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("org_id",orgId);
        params.put("role_id",roleId);
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //请求的参数
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("org_id",orgId);
        jsonObject.put("role_id",roleId);
        Set<String> set = new HashSet<String>();
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        if(jsonResponse!=null){
            JSONObject jsonObject1 =JSON.parseObject(jsonResponse);
            if("0".equals(jsonObject1.getString("result"))){
                JSONArray jsonArray =jsonObject1.getJSONArray("data");
                for(int i=0;i<jsonArray.size();i++){
                    set.add(jsonArray.getJSONObject(i).getString("user_id"));
                }
            }
        }
        return set;
    }













}
