package cn.huimin.process.web.service;

/**
 * Created by wyp on 2017/4/6.
 */
public interface TaskUrgeService {

    /**
     * 任务催办
     * @param processInstanceId
     */
    public void urge(String processInstanceId);
}
