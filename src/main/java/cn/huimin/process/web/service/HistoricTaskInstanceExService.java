package cn.huimin.process.web.service;

import cn.huimin.process.web.dto.HistoricTaskInstanceType;
import cn.huimin.process.web.model.HistoricTaskInstanceEx;

/**
 * Created by wyp on 2017/4/8.
 */
public interface HistoricTaskInstanceExService {

    public HistoricTaskInstanceType queryHistoricTaskByProcessInstanceId(HistoricTaskInstanceEx historicTaskInstanceEx);



}
