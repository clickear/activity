package cn.huimin.process.core.rule;

import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.web.util.ObjectCheckUtils;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直接设置人员的规则
 */
public class SetCheckPersonRule extends UserRuleAbstract implements UserRule {

    /**
     * 直接获取人的
     *
     * @param pvmActivity
     * @return
     */
    @Override
    public List<String> getUsersId(PvmActivity pvmActivity,ActivityExecution execution) {
        List<String> list = new ArrayList<>(0);
        //获取属性
        Map<String, List<ExtensionAttribute>> extensionAttribute = getExtensionAttributes(pvmActivity);
        if(extensionAttribute == null){
            return list;
        }
        List<ExtensionAttribute> assigner = extensionAttribute.get(HmXMLConstants.PROPERTY_USERTASK_ASSIGN);
        if (assigner != null && assigner.size() > 0) {
            for (ExtensionAttribute extensionAttribute1 : assigner) {
                String value = extensionAttribute1.getName();
                if (HmXMLConstants.PROPERTY_USERTASK_ASSIGN.equals(value)) {
                    String id = extensionAttribute1.getValue();
                    if (!ObjectCheckUtils.isEmptyString(id)) {
                        String[] ids = id.split(",");
                        for (String i : ids) {
                            if (!list.contains(i)) {
                                list.add(i);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

}
