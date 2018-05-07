package cn.huimin.process.web.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wyp on 2017/6/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/springcfg/spring-context.xml"})
public class TaskUrgeServiceTest {
        @Autowired
        private TaskUrgeService taskUrgeService;
    @Test
    public void urge() throws Exception {
        taskUrgeService.urge("23a5b80e-4b71-11e7-b7cb-e8b1fc035b51");
    }
}