package cn.huimin.process.core.rule;

import cn.huimin.process.core.HmXMLConstants;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wyp on 2017/5/3.
 */
public class RuleManager {
 private   static Map<String,UserRule> rules = new HashMap<>();
    private RuleManager(){

    }
static {
    rules.put(HmXMLConstants.PROPERTY_USERTASK_ASSIGN,new SetCheckPersonRule());
    rules.put(HmXMLConstants.PROPERTY_ROLE_SET,new RoleRule());
    rules.put(HmXMLConstants.PROPERTY_USERTASK_DEPNAME,new SpecificRoleRule());
    rules.put(HmXMLConstants.PROPERTY_USERTASK_RULE,new SpecialCheckPersonRule());
}
    /**
     * 提供规则
     * @return
     */
    public static Map<String,UserRule> produceUserRule(){

        return rules;
    }
}
