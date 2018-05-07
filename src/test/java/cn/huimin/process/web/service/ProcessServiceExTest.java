package cn.huimin.process.web.service;

import cn.huimin.process.core.InnerBusinessVarConstants;
import cn.huimin.process.web.dto.APISimpleResult;
import com.alibaba.fastjson.JSON;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by wyp on 2017/4/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/springcfg/spring-context.xml"})
public class ProcessServiceExTest {

    private Logger logger = LoggerFactory.getLogger(ProcessServiceExTest.class);
    @Autowired
    ProcessServiceEx processServiceEx;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void processSuppend() throws Exception {
        //processServiceEx.processSuppend("3bc07cce-1dc6-11e7-b39d-e8b1fc035b51");

    }

    @Test
    public void processActive() throws Exception {

    }

    @Test
    public void depoyProcessTest() throws IOException, ClassNotFoundException {
        //部署
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.addInputStream("test13.bpmn", this.getClass().getClassLoader().getResourceAsStream("output.xml"));
        deployment.deploy();
    }

    @Test
    public void processCheck() throws Exception {
        String key = "ac22b249d-f543-44cf-81b3-9ea7ff3dc41e";
        String roleId = "111";
        APISimpleResult apiSimpleResult = new APISimpleResult();
         processServiceEx.processCheck(key,null,roleId,apiSimpleResult);
       logger.info("ProcessServiceExTest.processCheck {}",apiSimpleResult);
    }

    @Test
    public void start() throws Exception {
        String startUserId = "17102";
        String key = "cc4c88272-aed2-4e96-a999-836525ed58f1";
        String systemId = "1";
        APISimpleResult apiSimpleResult = new APISimpleResult();
        Map<String, Object> parms = new HashMap<>();
        parms.put(InnerBusinessVarConstants.BUSINESS_KEY,"122222");
        parms.put(InnerBusinessVarConstants.BRANCH_ID,"1");
        parms.put("type",12);
        parms.put("expendTotal",10000);
        parms.put("startId",startUserId);
        parms.put(InnerBusinessVarConstants.ORG_LEVEL, "1|134");
        parms.put(InnerBusinessVarConstants.DEPARTMETN_LEVEL, 1);
        parms.put(InnerBusinessVarConstants.USER_ID,startUserId);
        processServiceEx.start(parms,startUserId,key,systemId,apiSimpleResult);
        logger.info("ProcessServiceExTest.start {}", JSON.toJSONString(apiSimpleResult));
   }
}