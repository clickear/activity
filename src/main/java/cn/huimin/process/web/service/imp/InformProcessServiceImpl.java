package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dao.InformProcessDao;
import cn.huimin.process.web.model.InformProcess;
import cn.huimin.process.web.service.InformProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知流程的实现
 */
@Service
public class InformProcessServiceImpl implements InformProcessService {

    @Autowired
    private InformProcessDao informProcessDao;

    @Override
    @Transactional
    public void insertInformPage(InformProcess informProcess, String informPersonId) {
         String[] informIds = informPersonId.split(",");
        for(String informId:informIds){
            informProcess.setInformPersonId(informId);
            informProcessDao.insert(informProcess);
        }
    }

}
