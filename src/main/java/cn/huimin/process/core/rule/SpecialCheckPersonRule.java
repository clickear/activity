package cn.huimin.process.core.rule;


import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.InnerBusinessVarConstants;
import cn.huimin.process.web.util.*;
import com.alibaba.fastjson.JSONArray;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyp on 2017/5/3.
 */
public class SpecialCheckPersonRule extends UserRuleAbstract implements UserRule {

    /**
     * 特殊规则设置
     *
     * @param pvmActivity
     * @param execution
     * @return
     */
    @Override
    public List<String> getUsersId(PvmActivity pvmActivity, ActivityExecution execution) {
        List<String> userIds = new ArrayList<>(0);
        Map<String, List<ExtensionAttribute>> extensionAttribute = getExtensionAttributes(pvmActivity);
        if (extensionAttribute == null) {
            return userIds;
        }
        List<ExtensionAttribute> list = extensionAttribute
                .get(HmXMLConstants.PROPERTY_USERTASK_RULE);// 获取autoUse属性值
        if (list != null && list.size() > 0) {
            for (ExtensionAttribute extensionAttribute2 : list) {
                String name = extensionAttribute2.getName();
                if (name != null
                        && name.equals(HmXMLConstants.PROPERTY_USERTASK_RULE)) {
                    String value = extensionAttribute2.getValue();
                    // 查询前一个节点的审核人的领导作为审核人
                    if ("1".equals(value)) {
                        String preAssignee = String
                                .valueOf(ProcessDefinitionUtils.getRuntimeInnerVar(
                                        execution.getEngineServices(),
                                        execution.getId(),
                                        InnerActivitiVarConstants.HM_ACITVITI_PRE_HANDLER_ID));
                        Set<String> list1 = EhrUserDataHandleUtils
                                .getLeaderByUserId(preAssignee);
                        userIds.addAll(list1);
                    }
                    //获取发起人或者填写表单的部门负责人
                    if ("2".equals(value)) {
                        String deparrtmenId = String.valueOf(execution.getVariable(InnerActivitiVarConstants.HM_ACTIVITI_START_DEPARTMENT_ID));
                        // 通过发起人找到对应的员工id
                        if(deparrtmenId!=null){
                            JSONArray leaders = EhrRequestApiUtils.getOrganizationByOrgId(deparrtmenId);
                            for (int i = 0; i < leaders.size(); i++) {
                                userIds.add(leaders.getJSONObject(i).getString(EhrDataVarConstants.DEPARTMETN_LEADER_ID));
                            }
                        }
                    }
                    if ("3".equals(value)) {
                        String startUserId = ProcessDefinitionUtils
                                .getStartUserId(
                                        execution.getEngineServices(),
                                        execution.getProcessDefinitionId(),
                                        null,
                                        execution.getProcessBusinessKey());
                        userIds.add(startUserId);
                    }
                    //直接找部门负责人
                    if ("4".equals(value)) {
                        //部门负责人
                        String departmetnId = this.getValue(extensionAttribute, HmXMLConstants.PROPERTY_DEPARTMENT_SET);
                        //获取层级部门负责人
                        List<String> us = this.getDepartmentLeader(departmetnId);
                        if (us.size() > 0) {
                            return us;
                        }
                    }
                    //获取一级部门负责人
                    if ("5".equals(value)) {
                        String departmenti = getDepartmentIdByLay(execution, 1, InnerBusinessVarConstants.ORG_LEVEL);

                        List<String> us = this.getDepartmentLeader(departmenti);
                        if (us.size() > 0) {
                            return us;
                        }
                    }
                    //二级部门
                    if ("6".equals(value)) {
                        String departmenti = getDepartmentIdByLay(execution, 2, InnerBusinessVarConstants.ORG_LEVEL);
                        List<String> us = this.getDepartmentLeader(departmenti);
                        if (us.size() > 0) {
                            return us;
                        }
                    }
                    //三级部门
                    if ("7".equals(value)) {
                        String departmenti = getDepartmentIdByLay(execution, 3, InnerBusinessVarConstants.ORG_LEVEL);
                        List<String> us = this.getDepartmentLeader(departmenti);
                        if (us.size() > 0) {
                            return us;
                        }
                    }
                    //获取调岗之后的一级部门负责人
                    if ("8".equals(value)) {
                        String departmenti = getDepartmentIdByLay(execution, 1, InnerBusinessVarConstants.AFTER_ORG_LAYER);
                        List<String> us = this.getDepartmentLeader(departmenti);
                        if (us.size() > 0) {
                            return us;
                        }
                    }
                    //获取调岗之后的二级负责人
                    if ("9".equals(value)) {
                        String departmenti = getDepartmentIdByLay(execution, 2, InnerBusinessVarConstants.AFTER_ORG_LAYER);
                        if (departmenti != null && !departmenti.equals(1)) {
                            List<String> us = this.getDepartmentLeader(departmenti);
                            if (us.size() > 0) {
                                return us;
                            }
                        }
                    }
                    //获取三级部门负责人
                    if ("10".equals(value)) {
                        String departmenti = getDepartmentIdByLay(execution, 3, InnerBusinessVarConstants.AFTER_ORG_LAYER);
                        if (departmenti != null && !departmenti.equals(1)) {
                            List<String> us = this.getDepartmentLeader(departmenti);
                            if (us.size() > 0) {
                                return us;
                            }
                        }
                    }


                }
            }
        }
        return userIds;
    }

    /**
     * 获取发起人的对应层级的部门id
     *
     * @param
     * @return
     */
    private String getDepartmentIdByLay(ActivityExecution execution, int i, String key) {
        String orginLay = String.valueOf(execution.getVariable(key));
        if (ObjectCheckUtils.isEmptyString(orginLay)) {
            return null;
        }
        String[] lays = StringUtils.split(orginLay, "\\|");
        //如果 3 i=3
        if (lays.length >i&&i>0) {
            return lays[i];
        }
        return null;
    }

    /**
     * 根据部门id获取部门负责人
     *
     * @param departmentId
     * @return
     */
    private List<String> getDepartmentLeader(String departmentId) {
        List<String> userIds = new ArrayList<>();
        if(ObjectCheckUtils.isEmptyString(departmentId)){
            return userIds;
        }
        JSONArray jsonArray = EhrRequestApiUtils.getOrganizationByOrgId(departmentId);
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                String id = jsonArray.getJSONObject(i).getString(EhrDataVarConstants.DEPARTMETN_LEADER_ID);
                if (!userIds.contains(id)&&!ObjectCheckUtils.isEmptyString(id)) {
                    userIds.add(id);
                }
            }
        }
        return userIds;
    }


}
