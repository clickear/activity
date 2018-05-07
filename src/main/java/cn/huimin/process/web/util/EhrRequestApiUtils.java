package cn.huimin.process.web.util;

import cn.huimin.web.jwt.sign.SHA256Sign;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* Created by wyp on 2017/2/23
 */
public class EhrRequestApiUtils {

    private final  static String key = "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N";
    private static final Logger logger = LoggerFactory.getLogger(EhrRequestApiUtils.class);



    /**
     * 根据用户id获取用去用户的信息
     * @param userId 请求的参数
     * @param  url 请求的地址
     * @return
     */
    public static JSONArray getUserInfoByUserId(String userId, String url){
        //获取员工信息
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("user_id",userId);
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //请求的参数
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("user_id",userId);
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        if(jsonResponse==null ||"".equals(jsonResponse)){
            return null;
        }
        JSONObject dataObject = JSONObject.parseObject(jsonResponse);
        JSONArray jsonArray = dataObject.getJSONArray("data");
        return jsonArray;
    }








    /**
     * 根据岗位id获取所有的用户
     * 或者是根据根据岗位id获取岗位信息
     * @param orId 岗位逻辑id
     * @param  url 请求的地址
     * @return
     */
    public static JSONArray getUsersOrRoleInfoByOrId(String orId, String url){
        //获取员工信息
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("or_id",orId);
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //请求的参数
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("or_id",orId);
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        JSONArray jsonArray =null;
        JSONObject dataObject;
		try {
			dataObject = JSONObject.parseObject(jsonResponse);
			jsonArray = dataObject.getJSONArray("data");
		} catch (Exception e) {
			e.printStackTrace();
		}
     
        return jsonArray;
    }

    /**
     * 获取相对的岗位或者人
     * @param branchId
     * @param roleId
     * @param majorId
     */
    public static Set<String> getRelativeRule(String branchId,String roleId,String majorId){
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("branchId",branchId);
        params.put("roleId",roleId);
        if(!ObjectCheckUtils.isEmptyString(majorId)){
            params.put("majorId",majorId);
        }
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("branchId",branchId);
        jsonObject.put("roleId",roleId);
        if(!ObjectCheckUtils.isEmptyString(majorId)){
            jsonObject.put("majorId",majorId);
        }
        Long start = DateUtils.nowTime();
        logger.info("startTime {}",start );
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(ReadFileUtils.readProperties(null,"getApprovingUserByBranchIdAndMajorIdAndRoleId"),jsonObject);
        Long end = DateUtils.nowTime();
        logger.info("totalTime {}",end-start);
        logger.warn("getRelativeRule.EhrRequestApiUtils {}",jsonResponse);
        //对数据进行处理
        if(ObjectCheckUtils.isEmptyString(jsonResponse)){
            return null;
        }
       JSONArray jsonArray = JSONObject.parseObject(jsonResponse).getJSONArray("data");
       if(!ObjectCheckUtils.isEmptyCollection(jsonArray)){
           return handleRelativeRuleInfo(jsonArray);
       }
        return null;
    }


    private static Set<String> handleRelativeRuleInfo(JSONArray jsonArray){
       Set set = new HashSet<>(0);
        for (int i=0;i<jsonArray.size();i++){
            set.add(jsonArray.getJSONObject(i).getString(EhrDataVarConstants.USER_ID));
        }
        return set;
    }





    /**
     * 通过组织id获取组织信息
     * @param
     * @return
     */
    @Test
    public  void getOrganizationByOrgId2(){
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("org_id","108");
        params.put("role_id","117");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //请求的参数
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("org_id","108");
        jsonObject.put("role_id","117");
        String url = "http://101.200.215.189:12001/ehr/rest/generation/system/getAllUserByOrgId";
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("getOrganizationByOrgId {}",jsonResponse);

    }

    /**
     * 通过组织id获取组织信息
     * @param orgId
     * @return
     */
    public static  JSONArray getOrganizationByOrgId(String orgId){
        if(ObjectCheckUtils.isEmptyString(orgId)){
            return new JSONArray(0);
        }
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("org_id",orgId);
        logger.info("getOrganizationByOrgId {}",orgId);
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //请求的参数
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("org_id",orgId);
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(ReadFileUtils.readProperties("url","getOrganizationByOrgId"),jsonObject);
       logger.info("getOrganizationByOrgId {}",jsonResponse);
        JSONArray jsonArray =null;
        JSONObject dataObject;
        dataObject = JSONObject.parseObject(jsonResponse);
        jsonArray = dataObject.getJSONArray("data");
        return jsonArray;
    }

    /**
     * 获取
     * @param args
     */
    public static void main(String[] args){
        String orginLay  = "|1|110|233|";
        String a = null;
            if (ObjectCheckUtils.isEmptyString(orginLay)) {
            }
            String[] lays = StringUtils.split(orginLay, "\\|");
            if (lays.length > 2) {
              a = lays[2];
            }
        JSONArray organizationByOrgId = getOrganizationByOrgId(a);
    }

    /**
     * 通过部门名称获取部门信息 模糊查询
     * @param org_name
     * @param url
     * @return
     */
    public static JSONArray getAllOrganization(String org_name,String url){
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>(0);
        params.put("org_name",org_name);
        params = SHA256Sign.sign(params, "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N");
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //参数类型提供
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("org_name",org_name);
        //获取数据
        String json =HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        if(json!=null && !"".equals(json)){
            JSONObject jsonObject1 = JSONObject.parseObject(json);
           JSONArray jsonArray =jsonObject1.getJSONArray(("data"));
            //如果大于0的话
            if(jsonArray!=null && jsonArray.size()>0){
                for(int i = 0 ;i<jsonArray.size();i++){
                    StringBuffer stringBuffer = new StringBuffer();
                    String orgId = jsonArray.getJSONObject(i).getString("org_id");
                        JSONArray orgs = getOrganizationByOrgId(orgId);
                         JSONArray js = orgs.getJSONObject(0).getJSONArray("organs");
                        for(int j=0;j<js.size();j++){
                            if(j==0){
                                stringBuffer.append(js.getJSONObject(j).getString("org_name"));
                            }else {
                                stringBuffer.append("-").append(js.getJSONObject(j).getString("org_name"));
                            }

                        }
                        jsonArray.getJSONObject(i).put("org_name",stringBuffer.toString());

                 }
                return jsonArray;
            }
        }
        return null;
    }





    /**
     * 获取直属上级部门负责人
     */
    public static Set<String> getParDempartmentInfo(String org_id){
        //获取员工信息
        Set<String> set = new HashSet<>();
        String url = ReadFileUtils.readProperties(null,"getParOrganizationByOrgId");
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("org_id",org_id);
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("org_id",org_id);
        Long start = DateUtils.nowTime();
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        Long end = DateUtils.nowTime();
        logger.info("EhrRequestApiUtils。getParDempartmentInfo.data.totalTime {}",end-start);
        if(!ObjectCheckUtils.isEmptyString(jsonResponse)){
            JSONObject dataObject =JSONObject.parseObject(jsonResponse);
            if(dataObject.getInteger("result").equals(0)){
                 JSONArray jsonArray =dataObject.getJSONArray("data");
                for(int i=0;i<jsonArray.size();jsonArray.size()){
                    set.add(jsonArray.getJSONObject(i).getString(EhrDataVarConstants.DEPARTMETN_LEADER_ID));
                }

            }
        }
        return set;
    }


    /**
     * 获取字典
     * @return
     */
    public static JSONArray  getMajorTypeByDictionaryId(Integer type,Integer dictionaryFlag){
        String url = ReadFileUtils.readProperties(null,"getMajorTypeByDictionaryId");
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        if(type!=null){
            params.put("dictionaryId",String.valueOf(type));
        }
        params.put("dictionaryFlag",String.valueOf(dictionaryFlag));
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        if (type!=null){
            jsonObject.put("dictionaryId",String.valueOf(type));
        }
        jsonObject.put("dictionaryFlag",dictionaryFlag);
        String requestApi = HttpClientUtils.getHttpParamsWithOutEncode(url, jsonObject);
        if(ObjectCheckUtils.isEmptyString(requestApi)){
            return new JSONArray(0);
        }
        return JSONObject.parseObject(requestApi).getJSONArray("data");
    }














    /**
     * 分页模糊查询岗位信息
     * @param role_name
     * @param pageNo
     * @param pageSize
     * @param url
     * @return
     */
    public static JSONObject getRoleByRoleName(String role_name,String pageNo,String pageSize, String url){
        //获取员工信息
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("role_name",role_name);
        params.put("pageNo",pageNo);
        params.put("pageSize",pageSize);
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //请求的参数
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("role_name",role_name);
        jsonObject.put("pageNo",pageNo);
        jsonObject.put("pageSize",pageSize);
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        JSONObject dataObject = JSONObject.parseObject(jsonResponse);
        //JSONArray jsonArray = dataObject.getJSONArray("data");
        return dataObject;
    }
    /**
     * 分页模糊查询岗位信息
     * @param role_name
     * @param url
     * @return
     */
    public static JSONArray  getAllRoleInfo(String role_name, String url){

        //获取员工信息
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("role_name",role_name);
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //请求的参数
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("role_name",role_name);;
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        JSONObject dataObject = JSONObject.parseObject(jsonResponse);
        JSONArray jsonArray = dataObject.getJSONArray("data");
        return jsonArray;
    }


    /**
     * 分页模糊查询岗位信息
     * @param role_id
     * @param url
     * @return
     */
    public static JSONArray  getRoleInfoByRoleId(String role_id, String url){
        //获取员工信息
        JSONObject jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("role_id",role_id);
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //请求的参数
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("role_id",role_id);;
        String jsonResponse = HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        JSONObject dataObject = JSONObject.parseObject(jsonResponse);
        JSONArray jsonArray = dataObject.getJSONArray("data");
        return jsonArray;
    }






}
