package cn.huimin.process.web.service;

import cn.huimin.process.web.model.InformProcess;

/**
 * 知会节点信息
 */
public interface InformProcessService {

    //public Page queryInformPage(Integer adminId,Integer start,Integer max);
    void insertInformPage(InformProcess informProcess, String informPersonIds);
}
