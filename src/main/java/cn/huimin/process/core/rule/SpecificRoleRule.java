package cn.huimin.process.core.rule;

import cn.huimin.process.core.HmXMLConstants;
import cn.huimin.process.web.util.EhrRequestApiUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import cn.huimin.process.web.util.ReadFileUtils;
import com.alibaba.fastjson.JSONArray;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wyp on 2017/5/3.
 */
public class SpecificRoleRule extends UserRuleAbstract implements UserRule{


    @Override
    public List<String> getUsersId(PvmActivity pvmActivity,ActivityExecution execution) {
        List<String> userIds = new ArrayList<>(0);
        Map<String, List<ExtensionAttribute>> extensionAttribute = getExtensionAttributes(pvmActivity);
        List<ExtensionAttribute> list = extensionAttribute
                .get(HmXMLConstants.PROPERTY_USERTASK_DEPNAME);// 获取autoUse属性值
        if (list != null && list.size() > 0) {
            for (ExtensionAttribute extensionAttribute2 : list) {
                String name = extensionAttribute2.getName();
                if (name != null
                        && name.equals(HmXMLConstants.PROPERTY_USERTASK_DEPNAME)) {
                    String value = extensionAttribute2.getValue();
                    if (ObjectCheckUtils.isEmptyString(value))
                    {
                        continue;
                    }
                    String[] split = StringUtils.split(value, ",");
                    for (int j = 0; j <split.length ; j++) {
                        // 通过岗位获取对应的的所有人员
                        JSONArray jsonArray = EhrRequestApiUtils
                                .getUsersOrRoleInfoByOrId(split[j], ReadFileUtils
                                        .readProperties("url",
                                                "getUserByRoleId"));
                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                //用户id
                                String  userId =jsonArray
                                        .getJSONObject(i).getString("user_id");
                                if (!userIds.contains(userId)) {
                                    userIds.add(userId);
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
