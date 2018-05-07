package cn.huimin.process.web.service;

import cn.huimin.process.web.dto.HistoryProcessData;

/**
 * Created by Administrator on 2016/12/14.
 */
public interface HistroyProcessQueryService {
    public HistoryProcessData queryHistoryProcessByProcessId(String processId);
}
