package cn.huimin.process.listener;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 任务完成的处理
 */
public class TaskCompleteHandler implements ActivitiEventListener {

    private  final static Logger logger = LoggerFactory.getLogger(TaskCompleteHandler.class);


    @Override
    public void onEvent(ActivitiEvent event) {
        System.out.print(event.getExecutionId());
       TaskService taskService= event.getEngineServices().getTaskService();
       List<Task> task= taskService.createTaskQuery().executionId(event.getExecutionId()).list();
        logger.info("taskId {}",task);
        }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
