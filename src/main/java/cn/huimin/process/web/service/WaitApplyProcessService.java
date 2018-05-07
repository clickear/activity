package cn.huimin.process.web.service;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.ProcessData;

import java.util.Map;

/**
 * Created by Administrator on 2017/2/16.
 */
public interface WaitApplyProcessService {

    public Page queryPageWaitProcess(ProcessData processData,Integer start,Integer max);


    /**
     * 重新发起表单
     * @param jsonObject
     * @return
     */
    public SimpleResult formSubmitAgain(Map<String,Object> parms , String userId);


}
