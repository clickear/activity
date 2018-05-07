package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dao.ActCreationDaoEx;
import cn.huimin.process.web.model.ActCreationEx;
import cn.huimin.process.web.service.ActCreationServiceEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wyp on 2017/4/13.
 */
@Service
public class ActCreationServiceExImpl implements ActCreationServiceEx {
    @Autowired
    private ActCreationDaoEx actCreationDaoEx;

    @Override
    public List<ActCreationEx> query(ActCreationEx actCreationEx) {
        return  actCreationDaoEx.query(actCreationEx);
    }

    @Override
    @Transactional
    public void updateByProcessInstanceId(String processInstanceId) {
        actCreationDaoEx.update(processInstanceId);
    }


}
