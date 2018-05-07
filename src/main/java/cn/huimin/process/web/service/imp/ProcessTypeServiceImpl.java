package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dao.ProcessTypeDao;
import cn.huimin.process.web.model.ProcessType;
import cn.huimin.process.web.service.ProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wyp on 2017/4/18.
 */
@Service
public class ProcessTypeServiceImpl implements ProcessTypeService {
    @Autowired
    private ProcessTypeDao processTypeDao;


    @Override
    public List<ProcessType> queryList(ProcessType processType) {
        return processTypeDao.queryList(processType);
    }

    @Override
    public ProcessType queryById(String id,String systemId) {
        return processTypeDao.queryById(id,systemId);
    }
}
