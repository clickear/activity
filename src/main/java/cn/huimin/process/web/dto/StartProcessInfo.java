package cn.huimin.process.web.dto;

import cn.huimin.process.web.model.ProcessData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wyp on 2017/6/24.
 */
public class StartProcessInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 获取流程实例详情
     */
    private ProcessData processInstanceInfo;
    /**
     *获取下个任务节点
     */
    private List<UserTaskInfo> afterTaskInfo;

    public StartProcessInfo() {
    }

    public StartProcessInfo(ProcessData processInstanceInfo, List<UserTaskInfo> afterTaskInfo) {
        this.processInstanceInfo = processInstanceInfo;
        this.afterTaskInfo = afterTaskInfo;
    }

    public ProcessData getProcessInstanceInfo() {
        return processInstanceInfo;
    }

    public void setProcessInstanceInfo(ProcessData processInstanceInfo) {
        this.processInstanceInfo = processInstanceInfo;
    }

    public List<UserTaskInfo> getAfterTaskInfo() {
        return afterTaskInfo;
    }

    public void setAfterTaskInfo(List<UserTaskInfo> afterTaskInfo) {
        this.afterTaskInfo = afterTaskInfo;
    }

    @Override
    public String toString() {
        return "StartProcessInfo{" +
                "processInstanceInfo=" + processInstanceInfo +
                ", afterTaskInfo=" + afterTaskInfo +
                '}';
    }
}
