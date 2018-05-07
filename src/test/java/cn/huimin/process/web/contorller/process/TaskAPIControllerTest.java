package cn.huimin.process.web.contorller.process;

import cn.huimin.process.web.model.TaskAPIData;
import cn.huimin.process.web.util.HttpClientUtils;
import cn.huimin.web.jwt.sign.SHA256Sign;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
@ContextConfiguration(locations = {"classpath:/springcfg/spring-context.xml"})
public class TaskAPIControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(TaskAPIControllerTest.class);
    //http://123.56.178.231:18080/hm_activity/openapi/task/queryChecking
    private  String url = "http://localhost:18080/hm_activity/openapi/task/";
    //private String url ="http://121.42.158.225:18082/hm_activity/openapi/task/";

    private final  static String key = "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N";
    @Autowired
    private TaskAPIController taskAPIController;
    private JSONObject jsonObject;

    private JSONObject jsonObject2;

/*@Before
    public void setUp() throws Exception {
        jsonObject = new JSONObject();
        jsonObject.put("systemId",1);
        jsonObject.put("userId","17102");
        //jsonObject.put("pageSize",10);
        //jsonObject.put("pageNo",1);
        //任务id
        jsonObject.put("taskId","6dc929b2-0636-11e7-9634-e8b1fc035b51");
        jsonObject.put("type","1");
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("departmentId","1");
        jsonObject.put("parameter",jsonObject2);
    }*/

/*
*
     * 查询之后执行
     * @throws Exception*//*

    @After
    public void tearDown() throws Exception {
        logger.info("请求参数   {}",jsonObject2);
        if(jsonObject2!=null){
           String taskId = jsonObject2.getString("taskId");
            //执行完成任务操作
            jsonObject.put("taskId",taskId);
            jsonObject.put("type",1);
            url = "http://localhost:8080/hm_activity/openapi/task/".concat("complete");
            String complete =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
            logger.info("complete {}",complete);
        }
    }*/


    @Test
    public void queryChecked() throws Exception {
        TaskAPIData taskAPIData = new TaskAPIData();
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("handerId","17102");
        //params.put("processDefName","测");
        //params.put("state","3");
        params.put("pageSize","5");
        params.put("pageNo","2");
        //params.put("parameter",jsonObject1.toJSONString());
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("handerId","17102");
        //jsonObject.put("processDefName","测");
        jsonObject.put("pageSize","5");
        jsonObject.put("pageNo","1");
        //jsonObject.put("state","3");
        url =url.concat("queryChecked");
        String queryChecked =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("queryChecked {}",queryChecked);
    }




    @Test
    public void queryChecking() throws Exception {
        TaskAPIData taskAPIData = new TaskAPIData();
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("handerId","17102");
       //params.put("processDefName","具体");
       // params.put("state","3");
        //params.put("parameter",jsonObject1.toJSONString());
        //params.put("processCategory","2");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("handerId","17102");
       //jsonObject.put("processCategory","2");
        //jsonObject.put("processInstanceName","冀");
       // jsonObject.put("processDefName","具体");
        //jsonObject.put("state","3");
        url =url.concat("queryChecking");
        String queryChecked =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("queryChecked {}",queryChecked);
    }
    //backToStartUserId

    @Test
    public void complete() throws Exception {
         //12058
        JSONObject jsonObject1 = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","1");
        params.put("taskId","ba1df99f-bd1c-11e7-b8e0-e8b1fc035b51");
        params.put("userId","17102");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject1.put("ctime",ctime);
        jsonObject1.put("nonce",nonce);
        jsonObject1.put("sign",sign);
        jsonObject1.put("systemId","2");
        jsonObject1.put("userId","17102");
        jsonObject1.put("taskId","ba1df99f-bd1c-11e7-b8e0-e8b1fc035b51");
        /**
         *     private String taskName;
         private String taskKey;
         //处理结果
         private Boolean result;
         //审批备注
         private String remark;
         private String branchName;
         private String departmentName;
         private String roleName;
         private String userName;
         */
        jsonObject1.put("result",true);
        jsonObject1.put("remark","测试1");
        jsonObject1.put("roleName","test1");
        url = url.concat("complete");
        String complete =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject1);
        logger.info("complete {}",complete);
    }


    /**
     * 回退到发起人
     * @throws Exception
     */
    @Test
    public void backToStartUserId() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","1");
        params.put("taskId","df4870d6-bd1c-11e7-b8e0-e8b1fc035b51");
        params.put("userId","17102");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject1.put("ctime",ctime);
        jsonObject1.put("nonce",nonce);
        jsonObject1.put("sign",sign);
        jsonObject1.put("systemId","2");
        jsonObject1.put("userId","17102");
        //df4870d6-bd1c-11e7-b8e0-e8b1fc035b51
        jsonObject1.put("taskId","df4870d6-bd1c-11e7-b8e0-e8b1fc035b51");
        jsonObject1.put("result",true);
        jsonObject1.put("remark","测试1");
        jsonObject1.put("roleName","test1");
        url = url.concat("backToStartUserId");
        String complete =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject1);
        logger.info("backToStartUserId {}",complete);
    }


    @Test
    public void queryTaskInfoByProcessInstanceId(){
        jsonObject = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("processInstanceId","b2291506-2fcc-11e7-b12a-e8b1fc035b51");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        jsonObject.put("processInstanceId","b2291506-2fcc-11e7-b12a-e8b1fc035b51");
        //jsonObject.put("processCategory","2");
        // jsonObject.put("processDefName","具体");
        //jsonObject.put("state","3");
        url =url.concat("queryTaskInfoByProcessInstanceId");
        String queryChecked =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject);
        logger.info("queryChecked {}",queryChecked);
    }

/*
*
     * 查询任务变量
     * @throws Exception

*/

    @Test
    public void queryTaskVar() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","1");
        params.put("taskKey","sid-C87276FB-9FB4-4F6C-B384-6A9A6469A72D");
        params.put("processDefId","d30a8881-0df9-11e7-8616-e8b1fc035b51");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject1.put("ctime",ctime);
        jsonObject1.put("nonce",nonce);
        jsonObject1.put("sign",sign);
        jsonObject1.put("systemId","1");
        jsonObject1.put("processDefId","d30a8881-0df9-11e7-8616-e8b1fc035b51");
        jsonObject1.put("taskKey","sid-C87276FB-9FB4-4F6C-B384-6A9A6469A72D");
        url = url.concat("queryTaskVar");
        String complete =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject1);
        logger.info("queryTaskVar {}",complete);

    }


/*
*
     * 查询任务变量
     * @throws Exception

*/

    @Test
    public void queryFormProperties() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","1");
        params.put("taskKey","sid-D91CA934-B374-4358-B0BA-460D9EB95715");
        params.put("processDefId","8b29dabd-0de4-11e7-8818-e8b1fc035b51");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject1.put("ctime",ctime);
        jsonObject1.put("nonce",nonce);
        jsonObject1.put("sign",sign);
        jsonObject1.put("systemId","1");
        jsonObject1.put("processDefId","8b29dabd-0de4-11e7-8818-e8b1fc035b51");
        jsonObject1.put("taskKey","sid-D91CA934-B374-4358-B0BA-460D9EB95715");
        url = url.concat("queryFormProperties");
        String complete =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject1);
        logger.info("queryTaskVar {}",complete);

    }


    @Test
    public void  taskUrge() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","1");
        params.put("processInstanceId","7203ff44-1aa5-11e7-8fdc-e8b1fc035b51");
        params.put("userId","17102");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject1.put("ctime",ctime);
        jsonObject1.put("nonce",nonce);
        jsonObject1.put("sign",sign);
        jsonObject1.put("systemId","1");
        jsonObject1.put("processInstanceId","7203ff44-1aa5-11e7-8fdc-e8b1fc035b51");
        jsonObject1.put("userId","17102");
        url = url.concat("taskUrge");
        String complete =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject1);
        logger.info("taskUrge {}",complete);
    }

    @Test
    public void pickTaskCompleted() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        Map<String,String> params = new HashMap<String,String>();
        params.put("systemId","1");
        params.put("processInstanceId","f4175172-3fa2-11e7-bf56-e8b1fc035b51");
        params.put("taskId","fc253d26-3fa2-11e7-bf56-e8b1fc035b51");
        params.put("userId","17102");
        params = SHA256Sign.sign(params, key);
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        jsonObject1.put("ctime",ctime);
        jsonObject1.put("nonce",nonce);
        jsonObject1.put("sign",sign);
        jsonObject1.put("systemId","1");
        jsonObject1.put("processInstanceId","f4175172-3fa2-11e7-bf56-e8b1fc035b51");
        jsonObject1.put("userId","17102");
        jsonObject1.put("taskId","fc253d26-3fa2-11e7-bf56-e8b1fc035b51");
        url = url.concat("pickTaskCompleted");
        String complete =  HttpClientUtils.getHttpParamsWithOutEncode(url,jsonObject1);
        logger.info("taskUrge {}",complete);
    }

    @Test
    public void complete1() throws Exception {

    }
}
