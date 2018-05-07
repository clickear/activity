package cn.huimin.process.core.rule;

import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.web.util.EhrRequestApiUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wyp on 2017/5/3.
 * 相对岗位生成
 */
public class RoleRule extends UserRuleAbstract implements UserRule {

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
        List<ExtensionAttribute> roleSets = extensionAttribute.get(HmXMLConstants.PROPERTY_ROLE_SET);
        if (roleSets != null && roleSets.size() > 0) {
            //如果过是岗位的话获取岗位下的人
            for (ExtensionAttribute extensionAttribute2 : roleSets) {
                if (HmXMLConstants.PROPERTY_ROLE_SET.equals(extensionAttribute2.getName())) {
                    //获取岗位id
                    String roleId = extensionAttribute2.getValue();
                    if (!ObjectCheckUtils.isEmptyString(roleId)) {
                        //获取组织id
                        Object branchId = execution.getVariable(InnerActivitiVarConstants.HM_ACTIVITI_START_BRANCH_ID);
                        if (branchId != null) {
                            String branch = String.valueOf(branchId);
                            String[] split = StringUtils.split(roleId, ",");
                            for (int i = 0; i < split.length; i++) {
                                if (!ObjectCheckUtils.isEmptyString(split[i])) {
                                    Set<String> userId = EhrRequestApiUtils.getRelativeRule(branch, split[i], null);
                                    if (userId != null)
                                        userIds.addAll(userId);
                                }
                            }

                        }
                    }

                }
            }
        }
        return userIds;
    }
}
