package cn.huimin.process.core;

import cn.huimin.process.web.util.ObjectCheckUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.activiti.editor.language.json.converter.util.JsonConverterUtil.getProperty;

public class HmBpmnJsonConverter extends BpmnJsonConverter {
	public static Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> getconvertersToJsonMap() {
		return convertersToJsonMap;
	}
	public static Map<String, Class<? extends BaseBpmnJsonConverter>> getConvertersToBpmnMap(){
		return convertersToBpmnMap;
	}

	@Override
	public BpmnModel convertToBpmnModel(JsonNode modelNode) {
		BpmnModel bpmnModel = super.convertToBpmnModel(modelNode);
		//bpmnModel.setTargetNamespace(getProcessProperty(StencilConstants.PROPERTY_PROCESS_NAMESPACE,modelNode));
		Process process = bpmnModel.getProcesses().get(0);
		process.setCandidateStarterUsers(handlePropertys(HmXMLConstants.CANDIDATE_STARTER_USERS,modelNode));
		process.setCandidateStarterGroups(handlePropertys(HmXMLConstants.CANDIDATE_STARTER_GROUP,modelNode));
		return bpmnModel;
	}

	/**
	 * 处理一个属性由多个值
	 * @param jsonNode
	 * @return
	 */
	private String getProcessProperty(String key,JsonNode jsonNode){
		String value = null;
		JsonNode property = getProperty(key, jsonNode);
		if(property.asText()==""||property.asText()=="{}"){
			return value;
		}
		if(property!=null&&property.isNull()==false){
			JsonNode processNode = property.get(HmXMLConstants.PROPERTY_PROCESS_TYPE);
			value =processNode.asText();
		}
		return value;
	}


	/**
	 * 处理一个属性由多个值
	 * @param jsonNode
	 * @return
	 */
	private List<String> handlePropertys(String key,JsonNode jsonNode){
		List<String> list = new ArrayList<>();
		JsonNode values =getProperty(
				key, jsonNode);
		if(values!=null &&values.isNull()==false){
			JsonNode adminJsonNode = values.get(HmXMLConstants.ADMINS);
			if(adminJsonNode!=null){
				for (int i = 0; i < adminJsonNode.size(); i++) {
					JsonNode json = adminJsonNode.get(i);
					list.add(json.get(HmXMLConstants.ID).asText());
				}
			}else {
				String text = values.asText();
				JSONObject jsonObject = JSON.parseObject(text);
				if(jsonObject!=null){
					String json= jsonObject.getString(HmXMLConstants.ADMINS);
					if(!ObjectCheckUtils.isEmptyString(json)){
                        JSONArray jsonArray = JSON.parseArray(json);
                        for(int i= 0;i<jsonArray.size();i++){
							list.add(jsonArray.getJSONObject(i).getString(HmXMLConstants.ID));
						}
					}

				}

			}

		}
		return list;
	}



}
