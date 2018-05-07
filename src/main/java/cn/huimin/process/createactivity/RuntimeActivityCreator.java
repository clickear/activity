package cn.huimin.process.createactivity;


import cn.huimin.process.web.model.RuntimeActivityCreateInterface;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;


/**
 * Created by Administrator on 2016/12/20.
 */
public interface RuntimeActivityCreator
{
    public ActivityImpl[] createActivities(ProcessEngine processEngine, ProcessDefinitionEntity processDefinition,
                                           RuntimeActivityCreateInterface info);
}
