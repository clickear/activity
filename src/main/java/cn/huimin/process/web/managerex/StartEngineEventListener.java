package cn.huimin.process.web.managerex;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;


/**
 * 流程引擎启动监听器
 */
public interface StartEngineEventListener
{

    void afterStartEngine(ProcessEngineConfigurationImpl conf, ProcessEngine processEngine) throws Exception;

    void beforeStartEngine(ProcessEngineConfigurationImpl conf) throws Exception;

}
