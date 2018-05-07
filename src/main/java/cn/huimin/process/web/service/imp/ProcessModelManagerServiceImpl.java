package cn.huimin.process.web.service.imp;

import cn.huimin.process.core.HmBpmnJsonConverter;
import cn.huimin.process.core.HmUserTaskJsonConverter;
import cn.huimin.process.web.dao.UserTaskVarPoolDao;
import cn.huimin.process.web.model.UserTaskVarPool;
import cn.huimin.process.web.service.ProcessModelManagerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2017/1/17.
 */
@Service
public class ProcessModelManagerServiceImpl implements ProcessModelManagerService{

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private UserTaskVarPoolDao userTaskVarPoolDao;

    /**
     * 流程定义部署
     * @param modelId
     * @throws IOException
     */
    @Override
    @Transactional
    public void processModelStart(String modelId) throws Exception {

        byte[] bpmnBytes = null;
        String filename = null;
        JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelId));
        BpmnJsonConverter jsonConverter = new HmBpmnJsonConverter();
        HmBpmnJsonConverter.getConvertersToBpmnMap().put("UserTask", HmUserTaskJsonConverter.class);
        HmBpmnJsonConverter.getconvertersToJsonMap().put(UserTask.class, HmUserTaskJsonConverter.class);
        BpmnModel model = jsonConverter.convertToBpmnModel(editorNode);
       //filename = model.getMainProcess().getId() + ".bpmn20.xml";
        filename = model.getMainProcess().getName()+new Date().getTime()+".bpmn20.xml";
        bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        System.out.println(new String(bpmnBytes));
        ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        Model model1 = repositoryService.getModel(modelId);
        //部署进去后
        deploymentBuilder.addInputStream(filename,in);
        deploymentBuilder.tenantId(model1.getTenantId());
        Deployment deployment = deploymentBuilder.deploy();
        //发布
       ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
      //获取模板的信息
       List<UserTaskVarPool> list= userTaskVarPoolDao.getListByProcessKey(processDefinition.getKey());
        //复制并添加定义id
        for (UserTaskVarPool userTaskVarPool : list){
            userTaskVarPool.setProcessDefId(processDefinition.getId());
            userTaskVarPoolDao.insert(userTaskVarPool);
        }
        model1.setDeploymentId(deployment.getId());
        repositoryService.saveModel(model1);
    }




}
