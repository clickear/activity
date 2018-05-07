package cn.huimin.process.web.service;

import cn.huimin.process.web.model.ProcessType;

import java.util.List;

/**
 * Created by wyp on 2017/4/18.
 */
public interface ProcessTypeService {

    List<ProcessType> queryList(ProcessType processType);

    ProcessType queryById(String id,String systemId);
}
