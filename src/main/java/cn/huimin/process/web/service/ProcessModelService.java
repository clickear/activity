package cn.huimin.process.web.service;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.model.ProcessModel;

/**
 * Created by Administrator on 2017/1/18.
 */
public interface ProcessModelService {

    Page queryPage(ProcessModel model,Integer start ,Integer max);
    
    ProcessModel selectOneById(String id);

	boolean copyModel(String id);
    
}
