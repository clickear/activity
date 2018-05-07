package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dao.ProcessDataDao;
import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.ProcessData;
import cn.huimin.process.web.service.WaitApplyProcessService;
import cn.huimin.process.web.util.Constants;
import cn.huimin.process.web.util.EhrUserDataHandleUtils;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 分页实现代发流程
 */
@Service
public class WaitApplyProcessServiceImpl implements WaitApplyProcessService {
    @Autowired
    private ProcessDataDao processDataDao;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Value("${getRoleByUserId}")
    private String getRoleByUserId;

    @Override
    public Page queryPageWaitProcess(ProcessData processData,Integer start,Integer max) {
        Page page = new Page();
        //processData.setSupState(2);
        PageHelper.startPage(start,max);
        List<ProcessData> list=  processDataDao.queryRestartProcess(processData);
        PageInfo pageInfo = new PageInfo(list);
        for(ProcessData processData1:list){
                //processData1.setStartUserId(EhrUserDataHandleUtils.employeeInfo(processData.getStartUserId(),getRoleByUserId));
               String applayName = EhrUserDataHandleUtils.employeeInfo(processData1.getStartUserId(),getRoleByUserId);
                processData1.setStartUserId(applayName);
                if(applayName!=null){
                        processData1.setProcessDefName(applayName.concat("  |  ").concat(processData1.getProcessDefName()));
                }

            }
            //活动节点
        page.setTotal(pageInfo.getTotal());
        page.setRows(list);
        page.setPage(pageInfo.getPrePage());
        return page;
    }

    /**
     * 实现表单重新提交
     *
     * @param
     * @return
     */
    @Transactional
    @Override
    public SimpleResult formSubmitAgain(Map<String,Object> parms , String userId) {
        SimpleResult simpleResult = new SimpleResult();
        //在提交之前重新启动该流程
        String processId = String.valueOf(parms.get("processId"));
        runtimeService.activateProcessInstanceById(processId);
        Task task =  taskService.createTaskQuery().processInstanceBusinessKey(userId).processInstanceId(processId).active().singleResult();
        runtimeService.setVariable(processId, Constants.form1, JSON.toJSONString(parms));
        simpleResult.setTaskId(task.getId());
        simpleResult.setProcessId(processId);
        return simpleResult;
    }

}
