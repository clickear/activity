package cn.huimin.process.web.service.imp;


import cn.huimin.process.activiticmd.ExecuteCustomeCmd;
import cn.huimin.process.activiticmd.UpdateHiTaskPriorityCommand;
import cn.huimin.process.web.dao.ProcessPriorityDao;
import cn.huimin.process.web.model.ProcessPriority;
import cn.huimin.process.web.service.TaskUrgeService;

import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by wyp on 2017/4/6.
 */
@Service
public class TaskUrgeServiceImpl implements TaskUrgeService {


    @Value("${urgeCount}")
    private Integer urgeCount;
    @Value("${urgeBase}")
    private Integer urgeBase;
    @Value("${urgeTime}")
    private Integer urgeTime;
    @Autowired
    private ProcessPriorityDao processPriorityDao;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    /**
     * 执行催办流程变动
     *
     * @param processInstanceId
     */
    @Override
    @Transactional
    public void urge(String processInstanceId) {
        ProcessPriority processPriority = processPriorityDao.selectByProcInstId(processInstanceId);
        if (processPriority != null) {
            int updateBase = processPriority.getPriority() + urgeBase;
            processPriority.setPriority(updateBase);
            int handleTime =  processPriority.getHandTime() / urgeCount;
            processPriority.setHandTime(handleTime);
            processPriority.setProcInstId(processInstanceId);
            processPriorityDao.updateProcessPriorityByProcessInstanceId(processPriority);
            //同时更新审核期限
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            if(!ObjectCheckUtils.isEmptyCollection(tasks)){
                //在执行更行操作
                for(Task task :tasks){
                    taskService.setPriority(task.getId(),updateBase);
                    taskService.setDueDate(task.getId(),DateUtils.calTime(task.getCreateTime(),String.valueOf(handleTime)));
                }
            }
            ExecuteCustomeCmd.execute(runtimeService,new UpdateHiTaskPriorityCommand(processInstanceId,updateBase));
        }
    }
}
