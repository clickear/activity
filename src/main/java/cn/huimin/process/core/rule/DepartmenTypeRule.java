package cn.huimin.process.core.rule;

import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.InnerBusinessVarConstants;
import cn.huimin.process.web.util.EhrRequestApiUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyp on 2017/5/3.
 * 相对岗位生成
 */
public class DepartmenTypeRule extends UserRuleAbstract implements UserRule {

    /**
     * 获取数字字典岗位信息
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
        //获取部门类别
        String departmetnType = this.getValue(extensionAttribute, HmXMLConstants.DEPARTMENT_TYPE_SET);
        String  specialcheckperson = this.getValue(extensionAttribute,HmXMLConstants.PROPERTY_USERTASK_RULE);
        if (!ObjectCheckUtils.isEmptyString(departmetnType)&&"4".equals(specialcheckperson)) {
            Object branchId = execution.getVariable(InnerActivitiVarConstants.HM_ACTIVITI_START_BRANCH_ID);
            if (branchId != null) {
                String branch = String.valueOf(branchId);
                Set<String> userId = EhrRequestApiUtils.getRelativeRule(branch, null, departmetnType);
                userIds.addAll(userId);
            }
        }
        return userIds;
    }
}
