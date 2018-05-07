package cn.huimin.process.web.service.imp;

import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.core.InnerBusinessVarConstants;
import cn.huimin.process.web.dao.LogDao;
import cn.huimin.process.web.dto.HandlerLog;
import cn.huimin.process.web.model.CheckData;
import cn.huimin.process.web.service.LogService;
import cn.huimin.process.web.util.ReadFileUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;


/**
 * Created by wyp on 2017/4/11.
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogDao logDao;
    @Override
    public List<CheckData> queryHandleLogByProcessInstanceId(String processInstanceId) {
       List<CheckData>  list = logDao.queryHandleLogByProcessInstanceId(processInstanceId);
       if(list!=null && list.size()>0){
           for(CheckData checkData:list){
               String doWhat = checkData.getDoWhat();
              HandlerLog handlerLog =  parseObject(doWhat, HandlerLog.class);
              Map<String,String> handle = ReadFileUtils.readJsonData(LogServiceImpl.class);
               checkData.setDoWhat(handle.get(handlerLog.getType()));
              checkData.setCheckUser(handlerLog.getHandleUser());
              checkData.setDoTime(handlerLog.getHandleTime());
              checkData.setRemark(handlerLog.getRemark());
           }
       }
        return list;
    }



    @Override
    public List<JSONObject> queryCheckLogByProcessInstanceId(String processInstanceId,String type) {
        //850994f8-526d-11e7-a2b8-e8b1fc035b51
        List<String> list = logDao.queryCheckLogByProcessInstanceId(processInstanceId,type);
        List<JSONObject> list1 = new ArrayList<JSONObject>(0);
        if(InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO.equals(type)){
            for(String s : list){
                JSONObject jsonObject = JSONArray.parseObject(s);
                list1.add(jsonObject);
            }
        }
        if(InnerBusinessVarConstants.FILE_URL_PATH.equals(type)){
            for(String s : list){
                JSONArray jsonArray  =JSONArray.parseArray(s);
                for (int i = 0; i < jsonArray.size(); i++) {
                    list1.add(jsonArray.getJSONObject(i));
                }

            }
        }


        return list1;
    }
}
