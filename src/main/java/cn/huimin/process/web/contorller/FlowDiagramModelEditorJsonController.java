/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.huimin.process.web.contorller;

import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.web.service.ProcessTypeService;
import cn.huimin.process.web.util.EhrRequestApiUtils;
import cn.huimin.process.web.util.EhrUserDataHandleUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author Tijs Rademakers
 */
@RestController
public class FlowDiagramModelEditorJsonController implements ModelDataJsonConstants {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FlowDiagramModelEditorJsonController.class);


    @Autowired
    private RepositoryService repositoryService;

    @Value("${getRoleByUserId}")
    private String getRoleByUserId;

    @Value("${getRoleByRoleId}")
    private String getRoleByRoleId;

    @Value("${getAllRoleInfo}")
    private String getAllRoleInfo;

    @Value("${getOrganizationByOrgId}")
    private String getOrganizationByOrgId;
    @Autowired
    private ProcessTypeService processTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/model/{modelId}/json", method = RequestMethod.GET, produces = "application/json")
    public ObjectNode getEditorJson(@PathVariable String modelId) {
        ObjectNode modelNode = null;

        Model model = repositoryService.getModel(modelId);

        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
                } else {
                    modelNode = objectMapper.createObjectNode();
                    modelNode.put(MODEL_NAME, model.getName());
                }
                modelNode.put(MODEL_ID, model.getId());
                //对json格式的戒心
                String jsonNode = new String(repositoryService.getModelEditorSource(model.getId()), "utf-8");
                jsonNode = this.addModelJson(jsonNode,model.getTenantId());
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
                        jsonNode);
                modelNode.put("model", editorJsonNode);
                modelNode.put("tenantId",model.getTenantId());
            } catch (Exception e) {
                LOGGER.error("Error creating model JSON", e);
                throw new ActivitiException("Error creating model JSON", e);
            }
        }
        return modelNode;
    }

    /**
     * 重新添加相关数据
     *
     * @param modelJson
     * @return
     */
    private String addModelJson(String modelJson,String tenantId) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("FlowDiagramModelEditorJsonController.addModelJson {}", "=========begin======");
            LOGGER.info("FlowDiagramModelEditorJsonController.addModelJson {}", modelJson);
        }
        JSONObject jsonObject = JSON.parseObject(modelJson);

        this.handleProcess(jsonObject,tenantId);
        //获取childShapes model的子形状
        JSONArray childShapes = jsonObject.getJSONArray("childShapes");

        if (childShapes == null) {
            return modelJson;
        }
        //获取所有的子形状
        for (int i = 0; i < childShapes.size(); i++) {
            //循环获取单个子形状
            JSONObject childShape = childShapes.getJSONObject(i);
            //获取子属性
            JSONObject properties = childShape.getJSONObject("properties");
            //确定有这个属性啊！
            String formkeydefinitionString = properties.getString("formkeydefinition");
            String specialcheckpersonString = properties.getString("specialcheckperson");

      /*if (documentationString != null &&! "".equals(documentationString.trim())) {
        //获取的是字符串的
        String processKey = properties.getString("documentation");
        //对流程定义key进行特殊字符转换
        processKey= processKey.replace("_","-");
        //构建数据一个json数据
        JSONObject jsonObject1 = new JSONObject();
        ProcessDefEntity activityEntity = processDefinitionService.queryProcessByKey(processKey);
        if (activityEntity != null) {
          jsonObject1.put("documentation", activityEntity.getDefKey());
          jsonObject1.put("documentationInfo", activityEntity.getName());
          //将构建好的放入到原先的地方
          properties.put("documentation", jsonObject1);
        }
      }*/

            if ((formkeydefinitionString != null && !"".equals(formkeydefinitionString.trim()) || (specialcheckpersonString != null && !"".equals(specialcheckpersonString)&&!specialcheckpersonString.equals("{}")))) {
                //获取的是字符串的
                Integer prioritydefinitionId = properties.getInteger(HmXMLConstants.FORM_KEY_DEFINITION);
                Integer specialcheckperson = null;
                if(StringUtils.isNumeric(properties.getString(HmXMLConstants.PROPERTY_USERTASK_RULE))){

                     specialcheckperson = properties.getInteger(HmXMLConstants.PROPERTY_USERTASK_RULE);
                }
                //构建数据一个json数据//获取json格式
                InputStream dataJson = this.getClass().getClassLoader().getResourceAsStream("data.json");
                String json = null;
                try {
                    json = IOUtils.toString(dataJson, "utf-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //解析json字符串
                JSONObject priortityDefJSON = JSON.parseObject(json);
                JSONArray jsonArray = priortityDefJSON.getJSONArray(HmXMLConstants.FORM_KEY_DEFINITION);
                for (int i1 = 0; i1 < jsonArray.size(); i1++) {
                    //在这里比较
                    if (jsonArray.getJSONObject(i1).getInteger("id") == prioritydefinitionId) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("formkeydefinition", jsonArray.getJSONObject(i1).getInteger("id"));
                        jsonObject1.put("forminfo", jsonArray.getJSONObject(i1).getString("name"));
                        properties.put("formkeydefinition", jsonObject1);
                        break;
                    }
                }

                JSONArray jsonArray2 = priortityDefJSON.getJSONArray("specialcheckperson");
                for (int i2 = 0; i2 < jsonArray2.size(); i2++) {
                    //在这里比较
                    if (jsonArray2.getJSONObject(i2).getInteger("id") == specialcheckperson) {
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("specialcheckperson", jsonArray2.getJSONObject(i2).getInteger("id"));
                        jsonObject2.put("specialcheckpersonInfo", jsonArray2.getJSONObject(i2).getString("name"));
                        properties.put("specialcheckperson", jsonObject2);
                        break;
                    }
                }


            }
            //获取设置人
            // String usertaskassignment = properties.getString("usertaskassignment");
            //如果数字的话执行
      /*if(!StringUtils.isEmpty(usertaskassignment)){
        //获取设置的审核人
        JSONObject usertaskassignmentObject = JSONObject.parseObject(usertaskassignment);
        JSONObject assignment = usertaskassignmentObject.getJSONObject("assignment");
        String  assignee =assignment.getString("assignee");
        JSONObject jsonObject1= new JSONObject();
        //是数字
        if(StringUtils.isNumeric(assignee)) {
          //获取用户的信息用于展示
          jsonObject1.put("setCheckPersonInfo", EhrUserDataHandleUtils.employeeInfo(assignee, getRoleByUserId));
          jsonObject1.put("setCheckPerson", assignee);
          properties.remove("usertaskassignment");
          properties.put("setcheckperson", jsonObject1);
        }
        if(!StringUtils.isNumeric(assignee)&&!StringUtils.isEmpty(assignee)){
          jsonObject1.put("setCheckPersonInfo",assignee);
          jsonObject1.put("setCheckPerson",assignee);
          properties.remove("usertaskassignment");
          properties.put("setcheckperson",jsonObject1);
        }
      }*/
            String setcheckperson = properties.getString(HmXMLConstants.PROPERTY_USERTASK_ASSIGN);
            if (setcheckperson != null && !"".equals(setcheckperson.trim())) {
                this.handleAdmins(HmXMLConstants.PROPERTY_USERTASK_ASSIGN, "admins", properties);
            }
            //岗位Id
            String departmentName = properties.getString("departmentname");
            if (departmentName != null && !"".equals(departmentName)) {
                handleRoleSet(HmXMLConstants.PROPERTY_USERTASK_DEPNAME,HmXMLConstants.ADMINS,properties);
        /*        //这里需要把id和字符串用于显示
                JSONObject jsonObjectDepartment = new JSONObject();
                JSONArray jsonArray = EhrRequestApiUtils.getUsersOrRoleInfoByOrId(departmentName, getRoleByRoleId);
                if (jsonArray != null && jsonArray.size() != 0) {
                    jsonObjectDepartment.put("departmentnameInfo", EhrUserDataHandleUtils.handleRoleInfo(jsonArray.getJSONObject(0)));
                }
                jsonObjectDepartment.put("departmentname", departmentName);
                properties.put("departmentname", jsonObjectDepartment);*/
            }

            String departmentTypeSet = properties.getString(HmXMLConstants.DEPARTMENT_TYPE_SET);
            if (departmentTypeSet != null && !"".equals(departmentTypeSet)) {
                //这里需要把id和字符串用于显示
                JSONObject jsonObjectRoleSet = new JSONObject();
                JSONArray jsonArray = EhrRequestApiUtils.getMajorTypeByDictionaryId(Integer.parseInt(departmentTypeSet),15);
                if (jsonArray != null && jsonArray.size() != 0) {
                    jsonObjectRoleSet.put("departmenttypesetInfo", jsonArray.getJSONObject(0).getString("diction_name"));
                }
                jsonObjectRoleSet.put("departmenttypeset", departmentTypeSet);
                properties.put("departmenttypeset", jsonObjectRoleSet);
            }
            //获取department
            String department = properties.getString(HmXMLConstants.PROPERTY_DEPARTMENT_SET);
            //获取不属性
            if (department != null && !"".equals(department)) {
                this.handleDepartmentSet(HmXMLConstants.PROPERTY_DEPARTMENT_SET, "admins", properties);
            }

        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("FlowDiagramModelEditorJsonController.addModelJson {}", jsonObject.toJSONString());
            LOGGER.info("FlowDiagramModelEditorJsonController.addModelJson {}", "=========end======");
        }
        return jsonObject.toJSONString();
    }

    /**
     * 对部门属性扩展
     * @param defKey
     * @param value
     * @param properties
     * @return
     */
    private JSONObject handleDepartmentSet(String defKey, String value, JSONObject properties){
        String[] ids = properties.getString(defKey).split(",");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < ids.length; i++) {
            if (StringUtils.isNumeric(ids[i])) {
                JSONObject jsonObject = new JSONObject();
                String id = ids[i];
                //部门
                JSONArray jsonArray1 = EhrRequestApiUtils.getOrganizationByOrgId(id);
                if(jsonArray1!=null && jsonArray1.size()>0){
                    String s = EhrUserDataHandleUtils.handleDepartmentInfo(jsonArray1.getJSONObject(0)).getString("org_name");
                    jsonObject.put("name", s);
                }
                jsonObject.put("id", id);
                jsonArray.add(jsonObject);
            }
        }
        properties.put(defKey, new JSONObject().put(value, jsonArray));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(value, jsonArray);
        properties.remove(defKey);
        properties.put(defKey, jsonObject);
        return properties;

    }


    /**
     * 对部门属性扩展
     * @param defKey
     * @param value
     * @param properties
     * @return
     */
    private JSONObject handleRoleSet(String defKey, String value, JSONObject properties){
        String[] ids = properties.getString(defKey).split(",");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < ids.length; i++) {
            if (StringUtils.isNumeric(ids[i])) {
                JSONObject jsonObject = new JSONObject();
                String id = ids[i];
                //部门
                JSONArray jsonArray1 = EhrRequestApiUtils.getUsersOrRoleInfoByOrId(id, getRoleByRoleId);
                if(jsonArray1!=null && jsonArray1.size()>0){
                    String s = EhrUserDataHandleUtils.handleRoleInfo(jsonArray1.getJSONObject(0));
                    jsonObject.put("name", s);
                }
                jsonObject.put("id", id);
                jsonArray.add(jsonObject);
            }
        }
        properties.put(defKey, new JSONObject().put(value, jsonArray));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(value, jsonArray);
        properties.remove(defKey);
        properties.put(defKey, jsonObject);
        return properties;

    }
    /**
     * 处理多个人
     *
     * @param defKey
     * @param value
     * @param properties
     * @return
     */
    private JSONObject handleAdmins(String defKey, String value, JSONObject properties) {
        String[] ids = properties.getString(defKey).split(",");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < ids.length; i++) {
            if (StringUtils.isNumeric(ids[i])) {
                JSONObject jsonObject = new JSONObject();
                String id = ids[i];
                String name = EhrUserDataHandleUtils.employeeInfo(id, getRoleByUserId);
                jsonObject.put("id", id);
                jsonObject.put("name", name);
                jsonArray.add(jsonObject);
            }
        }
        properties.put(defKey, new JSONObject().put(value, jsonArray));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(value, jsonArray);
        properties.remove(defKey);
        properties.put(defKey, jsonObject);
        return properties;
    }


    private JSONObject handleProcess(JSONObject jsonObject,String tenantId) {
        JSONObject processProperties = jsonObject.getJSONObject("properties");
        if (processProperties != null) {
            String process_name = processProperties.getString("process_namespace");
            if (StringUtils.isNumeric(process_name)) {
                JSONObject jsonObject1 = new JSONObject();
                String id = processProperties.getString("process_namespace");
                jsonObject1.put(HmXMLConstants.PROPERTY_PROCESS_TYPE, id);
                if(processTypeService.queryById(id,tenantId)!=null){
                    jsonObject1.put(HmXMLConstants.PROPERTY_PROCESS_TYPE + "Info", processTypeService.queryById(id,tenantId).getName());
                }
                processProperties.put("process_namespace", jsonObject1);
            }
        }
        return jsonObject;

    }


}
