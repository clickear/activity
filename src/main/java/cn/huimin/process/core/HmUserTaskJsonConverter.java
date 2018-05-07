package cn.huimin.process.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.ActivityProcessor;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.UserTaskJsonConverter;

import java.util.*;

import static cn.huimin.process.core.ExtensionAttributeUtils.generate;

public class HmUserTaskJsonConverter extends UserTaskJsonConverter {


    @Override
    public void convertToJson(BaseElement baseElement,
                              ActivityProcessor processor, BpmnModel model,
                              FlowElementsContainer container, ArrayNode shapesArrayNode,
                              double subProcessX, double subProcessY) {
        super.convertToJson(baseElement, processor, model, container,
                shapesArrayNode, subProcessX, subProcessY);
    }

    /**
     * 解析自定义的扩展属性信息
     */
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode,
                                               JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        FlowElement flowElement = super.convertJsonToElement(elementNode,
                modelNode, shapeMap);
        if (flowElement != null && flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            String specialcheckperson = getPropertyValueAsString(
                    HmXMLConstants.PROPERTY_USERTASK_RULE, elementNode);
            String departmentname = getPropertyValueAsString(
                    HmXMLConstants.PROPERTY_USERTASK_DEPNAME, elementNode);

            //获取岗位
            JsonNode roleJsonNode =getProperty(
                    HmXMLConstants.PROPERTY_ROLE_SET, elementNode);
            String roleSet = null;
            if(roleJsonNode!=null&&roleJsonNode.isNull()==false){
                    roleSet = handlePropertys(roleJsonNode);
            }

            String departmentSet = getPropertyValueAsString(
                    HmXMLConstants.PROPERTY_DEPARTMENT_SET, elementNode);
            String dueDate = getPropertyValueAsString(
                    HmXMLConstants.DUE_DATE_SET, elementNode);
            String departmentType = getPropertyValueAsString(HmXMLConstants.DEPARTMENT_TYPE_SET,elementNode);
            String assigners = getPropertyValueAsString(
                    HmXMLConstants.PROPERTY_USERTASK_ASSIGN, elementNode);
            String bussinessState = getPropertyValueAsString(HmXMLConstants.BUSINESS_STATE,elementNode);

            String nodeBelong = getPropertyValueAsString(HmXMLConstants.NODE_BELONG,elementNode);
            if (departmentname == null) {
                departmentname = "";
            }


            if (specialcheckperson == null) {
                specialcheckperson = "";
            }
            if (roleSet == null) {
                roleSet = "";
            }
            if (departmentSet == null) {
                departmentSet = "";
            }
            if (dueDate == null) {
                dueDate = "";
            }
            if(assigners == null){
                assigners ="";
            }
            if(departmentType == null){
                departmentType = "";
            }
            if(bussinessState == null){
                bussinessState = "";
            }
            if(nodeBelong == null){
                nodeBelong = "";
            }

            Map<String, List<ExtensionAttribute>> attributes = new HashMap<String, List<ExtensionAttribute>>();
            List<ExtensionAttribute> value = new ArrayList<ExtensionAttribute>();
            ExtensionAttribute ea = generate(HmXMLConstants.PROPERTY_USERTASK_DEPNAME, departmentname);
            value.add(ea);

            ExtensionAttribute ruleExtensionAttribute = generate(HmXMLConstants.PROPERTY_USERTASK_RULE, specialcheckperson);
            value.add(ruleExtensionAttribute);

            ExtensionAttribute role = generate(HmXMLConstants.PROPERTY_ROLE_SET, roleSet);
            value.add(role);
            ExtensionAttribute nodeBe = generate(HmXMLConstants.NODE_BELONG, nodeBelong);
             value.add(nodeBe);
            ExtensionAttribute department = generate(HmXMLConstants.PROPERTY_DEPARTMENT_SET, departmentSet);
            value.add(department);
            //时间期限
           ExtensionAttribute   due =  generate(HmXMLConstants.DUE_DATE_SET,dueDate);
           value.add(due);
            ExtensionAttribute assign = generate(HmXMLConstants.PROPERTY_USERTASK_ASSIGN,assigners);
            value.add(assign);
            ExtensionAttribute departmentTy = generate(HmXMLConstants.DEPARTMENT_TYPE_SET,departmentType);
            value.add(departmentTy);

            ExtensionAttribute extensionBussinessState = ExtensionAttributeUtils.generate(HmXMLConstants.BUSINESS_STATE,bussinessState);
            value.add(extensionBussinessState);
            attributes.put(HmXMLConstants.TASK_JSON_MAP_KEY, value);
            userTask.setAttributes(attributes);
            return userTask;
        }
        return flowElement;

    }

    public static void fillTypes(
            Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap,
            Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    /**
     * 处理一个属性由多个值
     * @param jsonNode
     * @return
     */
    private String handlePropertys(JsonNode jsonNode){
        //admins是固定的
        StringBuilder sb = new StringBuilder();
        JsonNode adminJsonNode = jsonNode.get(HmXMLConstants.ADMINS);
        if(adminJsonNode!=null){
            for (int i = 0; i < adminJsonNode.size(); i++) {
                JsonNode json = adminJsonNode.get(i);
                if(json.get(HmXMLConstants.ID)!=null&&json.get(HmXMLConstants.ID).isNull() ==false){
                    sb.append(json.get(HmXMLConstants.ID).asText()).append(",");
                }
            }
        }
        return sb.toString();
    }




    public static void fillJsonTypes(
            Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER,
                HmUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(
            Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, HmUserTaskJsonConverter.class);
    }
}
