package cn.huimin.process.web.contorller.process;

import cn.huimin.process.web.util.HttpClientUtils;
import cn.huimin.web.jwt.sign.SHA256Sign;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wyp on 2017/3/11.
 *
 *
 * */

public class ProcessAPIControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessAPIControllerTest.class);

    private  String url="http://localhost:18080/hm_activity/openapi/process/";
   //private String url ="http://123.56.178.231:18080/hm_activity/openapi/process/";
     //private String url ="http://121.42.158.225:18082/hm_activity/openapi/process/";

    private final  static String key = "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N";

    private JSONObject jsonObject;
    @Autowired
    private ProcessAPIController processAPIController;



    @Test
    public void start() throws Exception {
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","2");
        params.put("roleId","26");
        //cd988ba2f-5f8f-450b-9c49-ace5b63538b8
        params.put("key","a90878c6d-b7a6-4b84-905a-034289cf61da");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("systemId",2);
        jsonObject.put("roleId","2");
        jsonObject.put("key","a90878c6d-b7a6-4b84-905a-034289cf61da");
        url =url.concat("processCheck");
        System.out.print(jsonObject.toString());
       String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("success: {}",json);
    }
    @Test
    public void start1() throws Exception {
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","2");
        params.put("startUserId","11710");
        params.put("key","a8b465ef6-5c02-4a73-9d26-0064831b5367");
        params.put("businessKey","123456789");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("systemId",2);
        jsonObject.put("startUserId","11710");
        jsonObject.put("key","a8b465ef6-5c02-4a73-9d26-0064831b5367");
        jsonObject.put("businessKey","adddda");
        url =url.concat("start");
        System.out.print(jsonObject.toString());
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("success: {}",json);
    }


    @Test
    public void getProcessNodeInfos() throws Exception {
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","2");
        params.put("startUserId","11710");
        params.put("key","b9c8d4aa-bd1c-11e7-b8e0-e8b1fc035b51");
        params.put("businessKey","123456789");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("systemId",2);
        jsonObject.put("processInstanceId","b9c8d4aa-bd1c-11e7-b8e0-e8b1fc035b51");
        url =url.concat("getProcessNodeInfos");
        System.out.print(jsonObject.toString());
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("success: {}",json);
    }


    @Test
    public void processDelete() throws Exception {
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("userId","17102");
        params.put("processInstanceId","aaaaaa");
        params.put("systemId","1");
        //params.put("parameter",jsonObject1.toJSONString());
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("userId","17102");
        jsonObject.put("processInstanceId","aaaaa");
        jsonObject.put("systemId","1");
        url =url.concat("processDelete");
        System.out.print(jsonObject.toString());
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("success: {}",json);
    }

    @Test
    public void isCanProcessDelete() throws Exception {
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("processInstanceId","9771d42f-105a-11e7-89e0-e8b1fc035b51");
        params.put("systemId","1");
        //params.put("parameter",jsonObject1.toJSONString());
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("processInstanceId","9771d42f-105a-11e7-89e0-e8b1fc035b51");
        jsonObject.put("systemId","1");
        url =url.concat("isCanProcessDelete");
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("success: {}",json);
    }

    @Test
    public void processPicBeforeStart() throws Exception {
        url = url.concat("processPicBeforeStart");
        System.out.print(jsonObject.toString());
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("请求processPicBeforeStart的url {}", json);
    }



    @Test
    public void processPic() throws Exception {
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","1");
        params.put("processInstanceId","bd59b315-0d4b-11e7-97a5-e8b1fc035b51");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("systemId",1);
        jsonObject.put("processInstanceId","bd59b315-0d4b-11e7-97a5-e8b1fc035b51");
        url = url.concat("processInstancePic");
        //jsonObject.put("pro")
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("请求processPic的url {}", json);
    }

    @Test
    public void queryProcessInstancesByStarterId() throws Exception {
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        //params.put("startUserId","17102");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("startUserId","17102");
        //jsonObject.put("processDefName","测");
        jsonObject.put("processInstanceName","");
        //jsonObject.put("state",3);
        //jsonObject.put("processCategory","1");
        url = url.concat("queryProcessInstancesByStarterId");
        //jsonObject.put("pro")
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("请求processPic的url {}", json);
    }

    @Test
    public void processList() throws Exception {
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
       params.put("processDefName","测");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
       jsonObject.put("processDefName","测");
        //jsonObject.put("state",3);
        //jsonObject.put("processCategory","2");
        url = url.concat("processList");
        //jsonObject.put("pro")
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("请求processPic的url {}", json);
    }

    @Test
    public void startProcess() throws Exception {
        String systemId = "1";
        String startUserId = "17102";
        String key = "a1395ab21-9472-4415-a5be-f57be5dfbc52";
        processAPIController.start(systemId,key,startUserId,null,null);
        url =url.concat("start");
        System.out.print(jsonObject.toString());
        String json= HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("success: {}",json);
    }
}
