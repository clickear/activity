package cn.huimin.process.web.service.imp;


import cn.huimin.process.web.service.APIParameterCheck;
import com.alibaba.fastjson.JSON;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by wyp on 2017/3/9.
 */
@Service
public class APIParameterCheckImpl implements APIParameterCheck {

    @Autowired
    private HistoryService historyService;

    @Override
    public boolean commonParamCheck(Object param) {
        return StringUtils.isEmpty(param);
    }
    @Override
    public boolean jsonStringParamCheck(String jsonStringParam){
        boolean flag = false;
        try{
            JSON.parseObject(jsonStringParam);
            flag = true;
        }catch (RuntimeException e){
                flag = false;
        }finally {
            return flag;
        }
    }

    @Override
    public boolean checkProcessInstanceExist(String processInstanceId) {
        List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).list();
        if(historicProcessInstanceList!=null && historicProcessInstanceList.size()>0){
            return true;
        }
        return false;
    }


}
