package cn.huimin.process.web.service;


import cn.huimin.process.web.dto.SimpleResult;

/**
 * 减签功能
 */
public interface CutActivityService {

    /**
     * 加签功能
     * @param taskId
     * @param processId
     * @return
     */
    public SimpleResult cutActivity(String taskId, String processId, String user) throws Exception;

}
