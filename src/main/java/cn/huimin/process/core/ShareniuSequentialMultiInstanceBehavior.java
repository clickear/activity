package cn.huimin.process.core;

import java.util.*;

import cn.huimin.process.processengine.ProcessEngineConfigurationEx;
import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/**
 * 串行
 * @author Administrator
 *
 */
public class ShareniuSequentialMultiInstanceBehavior extends SequentialMultiInstanceBehavior {

    private ProcessEngineConfigurationEx processEngineConfigurationEx;

    public ShareniuSequentialMultiInstanceBehavior(ActivityImpl activity,
                                                   AbstractBpmnActivityBehavior innerActivityBehavior) {
        super(activity, innerActivityBehavior);
    }

    protected void createInstances(ActivityExecution execution) throws Exception {
        int nrOfInstances = resolveNrOfInstances(execution);
        if (nrOfInstances < 0) {
            throw new ActivitiIllegalArgumentException(
                    "Invalid number of instances: must be a non-negative integer value" + ", but was " + nrOfInstances);
        }
        setLoopVariable(execution, NUMBER_OF_INSTANCES, nrOfInstances);
        setLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES, 0);
        setLoopVariable(execution, getCollectionElementIndexVariable(), 0);
        setLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES, 1);
        logLoopDetails(execution, "initialized", 0, 0, 1, nrOfInstances);
        if (nrOfInstances > 0) {
            executeOriginalBehavior(execution, 0);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void executeOriginalBehavior(ActivityExecution execution, int loopCounter) throws Exception {
        if (usesCollection() && collectionElementVariable != null) {
            Collection collection = null;
            if (collectionExpression != null) {
                collection = (Collection) collectionExpression.getValue(execution);
            } else if (collectionVariable != null) {
                collection = (Collection) execution.getVariable(collectionVariable);
            }
            Object value = null;
            int index = 0;
            Iterator it = collection.iterator();
            while (index <= loopCounter) {
                value = it.next();
                index++;
            }
            setLoopVariable(execution, collectionElementVariable, value);
        }
        ExpressionManager expressionManager = processEngineConfigurationEx.getExpressionManager();
        TaskDefinition taskDefinition = (TaskDefinition) activity.getProperty("taskDefinition");
        List<String> ids = new ArrayList<>();
        //解析扩展属性
        Object object =activity.getProperty("shareniuExt");
        if(object!=null){
            //扩展属性获取
            Map<String, List<ExtensionAttribute>> attributes = (Map<String, List<ExtensionAttribute>>) object;
            //获取设置人的属性
            List<ExtensionAttribute> list =attributes.get(HmXMLConstants.PROPERTY_USERTASK_ASSIGN);
            if(!ObjectCheckUtils.isEmptyCollection(list)){
                //执行循环获取
                for(ExtensionAttribute extensionAttribute:list){
                    if(HmXMLConstants.PROPERTY_USERTASK_ASSIGN.equals(extensionAttribute.getName())){
                        String assingers = extensionAttribute.getValue();
                        if(!ObjectCheckUtils.isEmptyString(assingers)){
                         ids =  Arrays.asList(assingers.split(","));
                        }
                    }
                }
            }
            String dateTime =	this.getValue(attributes,HmXMLConstants.DUE_DATE_SET);
            if(!ObjectCheckUtils.isEmptyString(dateTime)){
                DateTime dateTime1 = new DateTime(DateUtils.calHour(dateTime));
                Expression expression= expressionManager.createExpression(String.valueOf(dateTime1));
                taskDefinition.setDueDateExpression(expression);
            }
        }


        if(!ObjectCheckUtils.isEmptyCollection(ids)){
            String id = ids.get(loopCounter);
            Expression expression = expressionManager.createExpression(id);
            taskDefinition.setAssigneeExpression(expression);
        }
        if (loopCounter == 0) {
            callCustomActivityStartListeners(execution);
            innerActivityBehavior.execute(execution);
        } else {
            execution.executeActivity(activity);
        }
    }

    public void setProcessEngineConfigurationEx(ProcessEngineConfigurationEx processEngineConfigurationEx) {
        this.processEngineConfigurationEx = processEngineConfigurationEx;
    }

    /**
     * 获取的唯一一个属性
     * @param map
     * @param ruleKey
     * @return
     */
    private String getValue(Map<String, List<ExtensionAttribute>> map,String ruleKey){
        List<ExtensionAttribute> list = map.get(ruleKey);
        if (list != null && list.size() > 0) {
            for(ExtensionAttribute extensionAttribute2 : list){
                if(!StringUtils.isEmpty(extensionAttribute2.getValue())){
                    return extensionAttribute2.getValue();
                }
            }
        }

        return null;
    }

}
