package cn.huimin.process.core.rule;

import cn.huimin.process.core.HmXMLConstants;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wyp on 2017/5/3.
 */
public class UserRuleAbstract implements UserRule {

    @Override
    public List<String> getUsersId(PvmActivity pvmActivity, ActivityExecution execution) {
        return null;
    }

    /**
     * 获取的唯一一个属性
     *
     * @param map
     * @param ruleKey
     * @return
     */
    public  String getValue(Map<String, List<ExtensionAttribute>> map, String ruleKey) {
        List<ExtensionAttribute> list = map.get(ruleKey);
        if (list != null && list.size() > 0) {
            for (ExtensionAttribute extensionAttribute2 : list) {
                if (!StringUtils.isEmpty(extensionAttribute2.getValue())) {
                    return extensionAttribute2.getValue();
                }
            }
        }
        return null;
    }


    public Map<String, List<ExtensionAttribute>> getExtensionAttributes(PvmActivity pvmActivity){
        Object property = pvmActivity.getProperty(HmXMLConstants.EXTENSIONATTRIBUTE_KEY);
        Map<String, List<ExtensionAttribute>> extensionAttribute = null;
        if (property != null) {
            extensionAttribute = (Map<String, List<ExtensionAttribute>>) property;
        }
        return extensionAttribute;
    }

}
