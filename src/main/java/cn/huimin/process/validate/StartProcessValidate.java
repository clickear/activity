package cn.huimin.process.validate;

import cn.huimin.process.web.util.ObjectCheckUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wyp on 2017/5/4.
 */
public class StartProcessValidate  implements ValidateInterface {
    //必填参数
        private static final String[] mustParams = {"systemId","key","startUserId","startName","userId","userName","branchId","branchName","departmentId","departmentName","departmentLevel","leader","roleId","roleName"};

    private Map<String,Object> parmsObject = new HashMap<>(32);
    //校验的结果
    private boolean validate = true;
    //结果
    private String errorMessage = null;

    @Override
    public void setParms(Map<String, String> parms) {
        for(String param : mustParams){
            String value =parms.get(param);
/*            if(ObjectCheckUtils.isEmptyString(value)){
                validate = false;
                errorMessage = param+"为必传参数";
                return;
            }*/
            parmsObject.putAll(parms);
        }
    }
    @Override
    public boolean validateParms() {
        return validate;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public Map<String, Object> getParms() {
        return parmsObject;
    }
}
