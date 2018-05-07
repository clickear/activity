package cn.huimin.process.web.service;

import cn.huimin.process.web.dto.HistoricTaskInstanceType;
import cn.huimin.process.web.model.HistoricTaskInstanceEx;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wyp on 2017/4/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/springcfg/spring-context.xml",
        "classpath:/springcfg/spring-context.xml"})
public class HistoricTaskInstanceExServiceTest {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(HistoricTaskInstanceExServiceTest.class);
    @Autowired
    private HistoricTaskInstanceExService historicTaskInstanceExService;

    /**
     *  查询历史完成的任务
     * @throws Exception
     */
    @Test
    public void queryHistoricTaskByProcessInstanceId() throws Exception {
       String processInstanceId = "a54ba43b-1e64-11e7-b0ea-e8b1fc035b51";
        //String deleteReson = "completed";
        HistoricTaskInstanceEx historicTaskInstanceEx = new HistoricTaskInstanceEx();
        historicTaskInstanceEx.setProcessInstanceId(processInstanceId);
        //historicTaskInstanceEx.setDeleteReason(deleteReson);
        HistoricTaskInstanceType historicTaskInstanceType  = historicTaskInstanceExService.queryHistoricTaskByProcessInstanceId(historicTaskInstanceEx );
        logger.info("queryHistoricTaskByProcessInstanceId {}",historicTaskInstanceType);

    }


}