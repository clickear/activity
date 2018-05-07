package cn.huimin.process.web.service;

import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.InnerBusinessVarConstants;
import cn.huimin.process.web.model.CheckData;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * Created by wyp on 2017/4/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/springcfg/spring-context.xml",
        "classpath:/springcfg/spring-context.xml"})
public class LogServiceTest {

    Logger logger = LoggerFactory.getLogger(LogServiceTest.class);
    @Autowired
    private LogService logService;

    @Test
    public void queryHandleLogByProcessInstanceId() throws Exception {
        String processInstanceId = "cdf1c7a0-1e9e-11e7-8e80-e8b1fc035b51";
        List<CheckData> list= logService.queryHandleLogByProcessInstanceId(processInstanceId);
        logger.info("queryHandleLogByProcessInstanceId {}",list);
    }

    @Test
    public void queryCheckLogByProcessInstanceId() throws Exception {
        String processInstanceId = "33119ef2-58bf-11e7-b30e-e8b1fc035b51";
       List<JSONObject> list = logService.queryCheckLogByProcessInstanceId(processInstanceId, InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO);
        logger.info("queryCheckLogByProcessInstanceId {}",list);
    }

    /**
     * 获取流程中上传的文件路径
     * @throws Exception
     */
    @Test
    public void queryCheckLogByProcessInstanceId1() throws Exception {
        String  processInstanceId = "9d49872e-556c-11e7-9d38-e8b1fc035b51";
        List<JSONObject> list =   logService.queryCheckLogByProcessInstanceId(processInstanceId,InnerBusinessVarConstants.FILE_URL_PATH);
        logger.info("queryCheckLogByProcessInstanceId1 {}",list);
    }
}