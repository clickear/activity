package cn.huimin.process.core.pvm;

import cn.huimin.process.core.HmXMLConstants;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyp on 2017/6/23.
 * 流程自定义属性相关工具类
 */
public class ProcessExtensionAttributeUtils {
    /**
     * 根据流程定义id 流程节点key 扩展属性key 来获取对应的属性
     * @param repositoryService
     * @param processDefId
     * @param taskKey
     * @param extensionKey
     * @return
     */
    public static String getExtensionAttributeByTaskKey(RepositoryService repositoryService, String processDefId,String taskKey, String extensionKey){
      BpmnModel bpmnModel =  repositoryService.getBpmnModel(processDefId);
        if(bpmnModel!=null){
            Process process = bpmnModel.getProcesses().get(0);
            FlowElement flowElement = process.getFlowElement(taskKey);
            if(flowElement!=null){
                //获取任务属性
                //task任务节点
                if(flowElement instanceof UserTask){
                    UserTask userTask = (UserTask) flowElement;
                    return userTask.getAttributeValue(HmXMLConstants.NEMESPACE,extensionKey);
                }
            }
        }
        return null;
    }

    /**
     * 根据流程定义id 和流程配置的整体key 获取流程的配置的发起人配置
     * @param repositoryService
     * @param processDefId
     * @param extensionKey
     * @return
     */
    public static List<String> getProcessDefExtensionAttribute(RepositoryService repositoryService,String processDefId,String extensionKey){
        List<String> list = new ArrayList<String>();
        BpmnModel bpmnModel =  repositoryService.getBpmnModel(processDefId);
        if(bpmnModel!=null){
            Process process = bpmnModel.getProcesses().get(0);
            if(process!=null){
                if(HmXMLConstants.CANDIDATE_STARTER_USERS.equals(extensionKey)){
                    list.addAll(process.getCandidateStarterUsers());
                }
                if(HmXMLConstants.CANDIDATE_STARTER_GROUP.equals(extensionKey)){
                    list.addAll(process.getCandidateStarterGroups());
                }
            }
        }
        return list;
    }
}
