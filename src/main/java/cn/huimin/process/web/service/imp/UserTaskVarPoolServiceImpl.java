package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dao.UserTaskVarPoolDao;
import cn.huimin.process.web.model.UserTaskVarPool;
import cn.huimin.process.web.service.UserTaskVarPoolService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wyp on 2017/3/18.
 */
@Service
public class UserTaskVarPoolServiceImpl implements UserTaskVarPoolService {

    @Autowired
    private UserTaskVarPoolDao userTaskVarPoolDao;

    @Transactional
    @Override
    public void insertOrUpdate(UserTaskVarPool userTaskVarPool) {
        //先查询是否存在，在执行对应的操作
        //不存在的话
        if(userTaskVarPoolDao.getByProcessKey(userTaskVarPool)!=null){
            //执行更新操作
            userTaskVarPoolDao.update(userTaskVarPool);
        }else {
            //插入的时候为null
            //执行保存是不需要添加
            userTaskVarPoolDao.insert(userTaskVarPool);
        }
    }

    /**
     * 获取流程所有节点任务节点的流程变量池 对内的
     * @param userTaskVarPool
     * @return
     */
    @Override
    public UserTaskVarPool getByProcessKey(UserTaskVarPool userTaskVarPool) {
        return  userTaskVarPoolDao.getByProcessKey(userTaskVarPool) ;
    }



    /**
     * 获取节点相关变量属性 对外提供的
     * @param processDefId
     * @param taskKey
     * @return
    */
    @Override
    public JSONArray getUserTaskVarByProcessKeyAndTaskKey(String processDefId,String taskKey){
        UserTaskVarPool userTaskVarPool2 = new  UserTaskVarPool();
        userTaskVarPool2.setProcessDefId(processDefId);
        userTaskVarPool2.setType(1);
        UserTaskVarPool userTaskVarPool= userTaskVarPoolDao.getByProcessDefId(userTaskVarPool2);
        if(userTaskVarPool==null){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(userTaskVarPool.getUserTaskVarInfo());
        return jsonObject.getJSONArray(taskKey);
    }

    /**
     * 获取表单属性
     * @param processDefId 流程定义id
     * @param taskKey
     * @return
     */
    @Override
    public JSONArray getFormPropertiesVarByProcessKeyAndTaskKey(String processDefId, String taskKey) {
        UserTaskVarPool userTaskVarPool2 = new  UserTaskVarPool();
        userTaskVarPool2.setProcessDefId(processDefId);
        userTaskVarPool2.setType(2);
        UserTaskVarPool userTaskVarPool= userTaskVarPoolDao.getByProcessDefId(userTaskVarPool2);
        if(userTaskVarPool==null){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(userTaskVarPool.getUserTaskVarInfo());
        return jsonObject.getJSONArray(taskKey);
    }



}
