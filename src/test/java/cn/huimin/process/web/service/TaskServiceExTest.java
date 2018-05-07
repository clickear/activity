package cn.huimin.process.web.service;

import cn.huimin.process.web.dto.UserTaskInfo;
import cn.huimin.process.web.model.TaskAPIData;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
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
@ContextConfiguration(locations = {"classpath:/springcfg/spring-context.xml"})
public class TaskServiceExTest {
    private static final String systemId = "1";
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceExTest.class);
    @Autowired
    private TaskServiceEx taskServiceEx;
    @Test
    public void doTask() throws Exception {
        String systemId ="1" ;
        String userId = "17102";
        String taskId = "8057aaf8-9692-11e7-bfdd-28d244ea4646";
        String s = taskServiceEx.doTask(systemId, userId, taskId, null);
        logger.info("doTask {}",s);
    }

    @Test
    public void taskCompletedList() throws Exception {
       TaskAPIData taskAPIData = new TaskAPIData();
        taskAPIData.setHanderId("17102");
        //taskAPIData.setProcessInstanceName("");
        taskAPIData.setProcessCategory("1");
      PageInfo pageInfo  =  taskServiceEx.TaskCompletedList(taskAPIData,1,40);
      logger.info("taskCompletedList {}", JSON.toJSONString(pageInfo.getList()));
    }

    @Test
    public void taskList() throws Exception {

    }


    @Test
    public void isProcessStop() throws Exception {
        String taskId = "c6ccb1fb-15ef-11e7-81d5-e8b1fc035b51";
        logger.info("isProcessStop {}",taskServiceEx.isProcessStop(taskId));


    }

    @Test
    public void handleSkipNode() throws Exception {
        List<UserTaskInfo> list = taskServiceEx.handleSkipNode(systemId, "17102", "f3d560af-58bd-11e7-b30e-e8b1fc035b51", null, "1");
        logger.info("handleSkipNode {}",JSON.toJSONString(list));
    }

    @Test
    public void taskDistinctCompletedList() throws Exception {
        TaskAPIData taskAPIData = new TaskAPIData();
        taskAPIData.setHanderId("17102");
        taskAPIData.setState(3);
        //taskAPIData.setProcessInstanceName("武");
        PageInfo<TaskAPIData> taskAPIDataPageInfo = taskServiceEx.taskDistinctCompletedList(taskAPIData, 1, 20);
        logger.info("taskDistinctCompletedList {}",JSON.toJSONString(taskAPIDataPageInfo.getList()));
    }
}