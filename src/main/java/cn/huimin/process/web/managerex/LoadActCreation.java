package cn.huimin.process.web.managerex;

import cn.huimin.process.createactivity.RuntimeActivityCreator;
import cn.huimin.process.web.model.ActCreation;
import cn.huimin.process.web.service.ActCreationService;
import cn.huimin.process.web.util.ProcessDefinitionUtils;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * 流程启动加载自定义的流程后动创建
 */
@Service
public class LoadActCreation implements StartEngineEventListener
{
    public ActCreationService getActCreationService() {
        return actCreationService;
    }

    public void setActCreationService(ActCreationService actCreationService) {
        this.actCreationService = actCreationService;
    }
    @Autowired
    @Qualifier("actCreationService")
    private ActCreationService actCreationService;


    @Override
    public void afterStartEngine(ProcessEngineConfigurationImpl conf, ProcessEngine processEngine) throws Exception
    {
        if(actCreationService.list()==null || actCreationService.list().size()==0){
            return;
        }
        for (ActCreation entity : actCreationService.list())
        {
            ProcessDefinitionEntity processDefinition = ProcessDefinitionUtils.getProcessDefinition(processEngine,
                    entity.getProcessDefinitionId());
            //空指针异常 如何
            if (processDefinition != null)
            {
                RuntimeActivityCreator activitiesCreator = (RuntimeActivityCreator) Class.forName(
                        entity.getFactoryName()).newInstance();

                //创建activity
                entity.deserializeProperties();
                activitiesCreator.createActivities(processEngine, processDefinition, entity);
            }
        }
    }
    @Override
    public void beforeStartEngine(ProcessEngineConfigurationImpl conf) throws Exception
    {
    }


}