package cn.huimin.process.core.pvm;

import cn.huimin.process.activiticmd.DeleteRunningTaskCmd;
import cn.huimin.process.activiticmd.ExecuteCustomeCmd;
import cn.huimin.process.activiticmd.StartActivityCmd;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * Created by wyp on 2017/6/26.
 * 流程节点自由跳转工具类
 */
public class ProcessNodeSkipUtils {
    /**
     * 处理跳过节点
     * @param processEngine
     * @param key
     * @param executionId
     */
    public static void handleNodeSkip(ProcessEngine processEngine, String key,String processDefId, String executionId,String deleteReson,String checkInfo){
        ActivityImpl activity = ProcessDefinitionUtils.getActivity(processEngine.getRepositoryService(),processDefId,key);
        //执行删除操作
        deleteRuntimeTaskByeId(processEngine,executionId,deleteReson);
        ExecuteCustomeCmd.execute(processEngine.getRuntimeService(),new StartActivityCmd(executionId,activity,checkInfo));
    }

    /**
     * 删除所有运行中的节点
     * @param processEngine
     * @param executionId
     */
    public static void deleteRuntimeTaskByeId(ProcessEngine processEngine,String executionId,String deleteReson){
    List<Task>  list =processEngine.getTaskService().createTaskQuery().processInstanceId(executionId).list();
      RuntimeService runtimeService = processEngine.getRuntimeService();
        for(Task task : list){
            ExecuteCustomeCmd.execute(runtimeService,new DeleteRunningTaskCmd((TaskEntity) task,deleteReson));
        }
    }
}
