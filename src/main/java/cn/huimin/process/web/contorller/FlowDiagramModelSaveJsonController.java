
package cn.huimin.process.web.contorller;

import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.web.model.UserTaskVarPool;
import cn.huimin.process.web.service.UserTaskVarPoolService;
import cn.huimin.process.web.util.ObjectCheckUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tijs Rademakers
 */
@RestController
public class FlowDiagramModelSaveJsonController implements ModelDataJsonConstants {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FlowDiagramModelSaveJsonController.class);

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private UserTaskVarPoolService userTaskVarPoolService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/model/{modelId}/save", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) {
        try {

            Model model = repositoryService.getModel(modelId);

            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

            modelJson.put(MODEL_NAME, values.getFirst("name"));
            modelJson.put(MODEL_DESCRIPTION, values.getFirst("description"));
            model.setMetaInfo(modelJson.toString());
            model.setName(values.getFirst("name"));
            //保存版本
            //Integer version =  model.getVersion()+1;
            //model.setVersion(version);
            repositoryService.saveModel(model);
            //处理json_xml的数据格式
            String json_xml = values.getFirst("json_xml");
            LOGGER.debug("变更前保存数据的json传", json_xml);
            json_xml = this.formatModelJson(json_xml);

            repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));

            InputStream svgStream = new ByteArrayInputStream(values.getFirst("svg_xml").getBytes("utf-8"));
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            // Setup output
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);
            // Do the transformation
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();

        } catch (Exception e) {
            LOGGER.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }
    }

    /**
     * 将格式化的模型数据又重新拿出来
     *
     * @param modelJson
     * @return
     */
    private String formatModelJson(String modelJson) {
        JSONObject jsonObject = JSON.parseObject(modelJson);
        //获取json流程属性 处理类型
        JSONObject processProperties = jsonObject.getJSONObject("properties");
        this.deleteNoNeedData(processProperties, "process_namespace", HmXMLConstants.PROPERTY_PROCESS_TYPE);
        //获取childShapes model的子形状
        JSONArray childShapes = jsonObject.getJSONArray("childShapes");
        //获取所有的子形状
        for (int i = 0; i < childShapes.size(); i++) {
            //循环获取单个子形状
            JSONObject childShape = childShapes.getJSONObject(i);
            //获取子属性
            JSONObject properties = childShape.getJSONObject("properties");
            properties = this.deleteNoNeedData(properties, HmXMLConstants.PROPERTY_USERTASK_TYPE, HmXMLConstants.PROPERTY_USERTASK_TYPE);
            properties = this.deleteNoNeedData(properties, "documentation", "documentation");
            properties = this.handleDepartmentSet(properties, HmXMLConstants.PROPERTY_USERTASK_DEPNAME, HmXMLConstants.PROPERTY_USERTASK_DEPNAME);
            properties = this.deleteNoNeedData(properties, HmXMLConstants.PROPERTY_USERTASK_RULE, HmXMLConstants.PROPERTY_USERTASK_RULE);
            //properties = this.deleteNoNeedData(properties, HmXMLConstants.PROPERTY_ROLE_SET, HmXMLConstants.PROPERTY_ROLE_SET);

            //设置部门
            properties = this.handleDepartmentSet(properties, HmXMLConstants.PROPERTY_DEPARTMENT_SET, HmXMLConstants.PROPERTY_DEPARTMENT_SET);
            properties = this.deleteNoNeedData(properties,HmXMLConstants.DEPARTMENT_TYPE_SET,HmXMLConstants.DEPARTMENT_TYPE_SET);
            //设置人特殊处理 添加一个特殊标签
            String setcheckperson = properties.getString(HmXMLConstants.PROPERTY_USERTASK_ASSIGN);
            if (setcheckperson != null && !"".equals(setcheckperson.trim())) {
                //任何人
                this.handleUsers(properties, HmXMLConstants.PROPERTY_USERTASK_ASSIGN, "admins");
            }
            //设置特殊节点
            properties = this.deleteNoNeedData(properties, HmXMLConstants.FORM_KEY_DEFINITION, HmXMLConstants.FORM_KEY_DEFINITION);

            childShape.put("properties", properties);
        }
        //设置流程变量
        jsonObject = this.handleUserTaskPool(jsonObject);
        //确定有这个属性啊！
        LOGGER.info("handleXmlAfter {}", jsonObject.toString());
        return jsonObject.toJSONString();
    }


    private JSONObject handleDepartmentSet(JSONObject properties, String defName, String defKey){
        String propertiesString = properties.getString(defName);
        if(!ObjectCheckUtils.isEmptyString(propertiesString)){
            JSONObject jsonObject = JSONObject.parseObject(propertiesString);
            if(jsonObject!=null){
                List<String> list = new ArrayList<>();
                String string = jsonObject.getString(defKey);
                JSONArray jsonArray = jsonObject.getJSONArray(HmXMLConstants.ADMINS);
                if(jsonArray!=null){
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String string1 = jsonArray.getJSONObject(i).getString(HmXMLConstants.ID);
                        if(!list.contains(string1)){
                            list.add(string1);
                        }
                    }
                }
                if(!ObjectCheckUtils.isEmptyString(string)&&!list.contains(string)){
                    list.add(string);
                }
                properties.put(defName,StringUtils.join(list,","));
            }
        }
        return properties;
    }



    private JSONObject deleteNoNeedData(JSONObject properties, String defName, String defKey) {
        String defString = properties.getString(defName);
        if (defString != null && !"".equals(defString.trim())) {
            //通过属性key属性获取表单定义属性队形
            JSONObject formkeydefinition = JSON.parseObject(defString);
            //当前设置的表单的id
            if (formkeydefinition != null) {
                String formId = formkeydefinition.getString(defKey);
                //放入到当前属性
                properties.put(defName, formId);
                if(defKey.equals(HmXMLConstants.FORM_KEY_DEFINITION)&& "2".equals(formId)){
                    //知会节点 将知会节点变为多实例的节点
                    properties.put(HmXMLConstants.PROPERTY_MULTIINSTANCE_CARDINALITY,-1);
                    String condition = "${nrOfCompletedInstances/nrOfInstances >=1}";
                    properties.put(HmXMLConstants.PROPERTY_MULIINSTANCE_CONDITION, condition);
                }

            }

        }

        return properties;
    }


    /**
     * 处理多个人的方式
     *
     * @param properties
     * @param defName
     * @param defKey
     * @return
     */
    private JSONObject handleUsers(JSONObject properties, String defName, String defKey) {
        JSONObject userJsonObject = properties.getJSONObject(defName);
        JSONArray jsonArray = userJsonObject.getJSONArray(defKey);
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("id");
            if (!ids.contains(id)) {
                ids.add(id);
            }
        }
        properties.put(defName, StringUtils.join(ids, ","));
        String multiType = properties.getString(HmXMLConstants.PROPERTY_MULIINSTANCE_TYPE);

        //并行的话设置
        if (!ObjectCheckUtils.isEmptyString(multiType)) {
            if (HmXMLConstants.MULIINSTANCE_PARALLEL.equals(multiType)) {
                properties.put(HmXMLConstants.PROPERTY_MULTIINSTANCE_CARDINALITY, ids.size());
                String condition = "${nrOfCompletedInstances/nrOfInstances >=1/" + ids.size() + "}";
                properties.put(HmXMLConstants.PROPERTY_MULIINSTANCE_CONDITION, condition);
            }

            if (HmXMLConstants.MULIINSTANCE_SEQUENTIAL.equals(multiType)) {
                properties.put(HmXMLConstants.PROPERTY_MULTIINSTANCE_CARDINALITY, ids.size());
                String condition = "${nrOfCompletedInstances/nrOfInstances >=1}";
                properties.put(HmXMLConstants.PROPERTY_MULIINSTANCE_CONDITION, condition);
            }
            //将人员随机可变

        }

        return properties;
    }


    private JSONObject handleUserTaskPool(JSONObject jsonObject) {
        UserTaskVarPool userTaskVarPool = new UserTaskVarPool();
        userTaskVarPool.setType(1);
        UserTaskVarPool activitiFormVar = new UserTaskVarPool();
        activitiFormVar.setType(2);
        //流程整体信息
        JSONObject processDef = jsonObject.getJSONObject("properties");
        //获取流程key
        String processKey = processDef.getString("process_id");
        userTaskVarPool.setProcessKey(processKey);
        activitiFormVar.setProcessKey(processKey);
        //获取对应的信息任务节点信息
        JSONArray childShapes = jsonObject.getJSONArray("childShapes");
        JSONObject activitiVar = new JSONObject(0);
        JSONObject activitiFrom = new JSONObject(0);
        for (int i = 0; i < childShapes.size(); i++) {
            //循环获取单个子形状
            JSONObject childShape = childShapes.getJSONObject(i);
            //获取子属性
            JSONObject properties = childShape.getJSONObject("properties");
            //活动节点id
            String activitiId = properties.getString(HmXMLConstants.ACTIVITY_ID);
            //获取节点相关变量
            String var = properties.getString(HmXMLConstants.TASK__WITH_VAR);
            //获取节点表单字段
            String formVar = properties.getString(HmXMLConstants.FORM_WITH_VAR);
            //通过属性获取表单绑定的字段
            if (!StringUtils.isEmpty(formVar)) {
                //获取表单属性
                JSONArray formArray = JSON.parseObject(formVar).getJSONArray("formWithVar");
                //将数据和对应的活动节点添加到一起
                activitiFrom.put(activitiId, formArray);
            }

            //如果不等于空的
            if (!StringUtils.isEmpty(var)) {
                JSONArray taskWithVar = JSON.parseObject(var).getJSONArray("taskWithVar");
                activitiVar.put(activitiId, taskWithVar);
            }
        }

        userTaskVarPool.setUserTaskVarInfo(activitiVar.toJSONString());
        activitiFormVar.setUserTaskVarInfo(activitiFrom.toJSONString());
        try {
            if(activitiVar.size()!=0){

                userTaskVarPoolService.insertOrUpdate(userTaskVarPool);
            }
            if(activitiFrom.size()!=0){

                userTaskVarPoolService.insertOrUpdate(activitiFormVar);
            }
        } catch (Exception e) {
            LOGGER.error("FlowDiagramModelSaveJsonController.handleUserTaskPool {}", e);
        }
        LOGGER.info("usertTask {}", userTaskVarPool);
        return jsonObject;
    }


}
