package cn.huimin.process.listener;

import cn.huimin.process.core.pvm.ActivityImplUtils;
import cn.huimin.process.web.model.ActCreationEx;
import cn.huimin.process.web.service.ActCreationServiceEx;
import cn.huimin.process.web.util.ObjectCheckUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.activiti.engine.EngineServices;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程完成之后的处理
 */
public class ProcessFinishHandler implements ActivitiEventListener {

    private final static Logger logger = LoggerFactory.getLogger(ProcessFinishHandler.class);

    private ActCreationServiceEx actCreationService;

    public ActCreationServiceEx getActCreationService() {
        return actCreationService;
    }

    public void setActCreationService(ActCreationServiceEx actCreationService) {
        this.actCreationService = actCreationService;
    }

    @Override
    public void onEvent(ActivitiEvent event) {
        //测试
        String processInstanceId = event.getProcessInstanceId();
        if (logger.isInfoEnabled()) {
            logger.info("--------start {} proces complete---------", processInstanceId);
            EngineServices engineServices = event.getEngineServices();
            ProcessEngineConfiguration processEngineConfiguration = engineServices.getProcessEngineConfiguration();
            RepositoryService repositoryService = engineServices.getRepositoryService();
            List<ActCreationEx> list = this.queryByProcessInstanceId(processInstanceId);
            if (!ObjectCheckUtils.isEmptyCollection(list)) {
                ActivityImplUtils.removeTemporaryActivityImpl(
                        this.queryTempNodeId(list), this.queryProcessDefId(list), repositoryService, processEngineConfiguration);
                //移除完之后再去更新状态
                actCreationService.updateByProcessInstanceId(processInstanceId);
            }
            //回调url
            RuntimeService runtimeService = engineServices.getRuntimeService();
            //获取url
            //ProcessInstanceUtils.getRuntimeVar(runtimeService,processInstanceId, InnerBusinessVarConstants.FINISH_URL,String.class);
            //获取初始化的数据
            String url =String.valueOf(runtimeService.getVariable(processInstanceId,"url"));
            logger.info("url {}",url);
            logger.info("--------start {} proces complete---------", processInstanceId);
        }
    }

    /**
     * 查询对应的添加的节点
     *
     * @param processInstanceId
     * @return
     */
    private List<ActCreationEx> queryByProcessInstanceId(String processInstanceId) {
        ActCreationEx actCreationEx = new ActCreationEx();
        actCreationEx.setProcessInstanceId(processInstanceId);
        actCreationEx.setState(0);
        return actCreationService.query(actCreationEx);

    }

    /**
     * 查询流程定义id
     *
     * @param list
     * @return
     */
    private String queryProcessDefId(List<ActCreationEx> list) {
        if (list != null && list.size() > 0) {
            ActCreationEx actCreationEx = list.get(0);
            return actCreationEx.getProcessDefinitionId();
        }
        return null;
    }

    /**
     * 查询对应的所有临时添加的节点
     *
     * @param list
     * @return
     */
    private List<String> queryTempNodeId(List<ActCreationEx> list) {
        List<String> nodeIds = new ArrayList<>();
        for (ActCreationEx actCreationEx : list) {
            String processText = actCreationEx.getProcessText();

            JSONArray jsonArray = JSON.parseObject(processText).getJSONArray("cloneActivityIds");
            for (int i = 0; i < jsonArray.size(); i++) {
                nodeIds.add(jsonArray.getString(i));
            }

        }
        return nodeIds;
    }


    @Override
    public boolean isFailOnException() {
        return false;
    }
}
