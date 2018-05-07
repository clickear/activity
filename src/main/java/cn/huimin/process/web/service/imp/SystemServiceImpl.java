package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dao.SystemDao;
import cn.huimin.process.web.model.SystemEntity;
import cn.huimin.process.web.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wyp on 2017/10/27.
 */
@Service
public class SystemServiceImpl implements SystemService {
    @Autowired
    private SystemDao systemDao;
    @Override
    public List<SystemEntity> queryList(SystemEntity systemEntity) {
        return systemDao.queryList(systemEntity);
    }
}
