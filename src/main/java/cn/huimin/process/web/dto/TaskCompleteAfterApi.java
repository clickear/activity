package cn.huimin.process.web.dto;

import java.util.List;

/**
 * Created by wyp on 2017/5/18.
 */
public class TaskCompleteAfterApi {
    private static final long serialVersionUID = 1L;
    //节点是否是为最后一个节点
    private boolean processIsStop;
    private List<UserTaskInfo> afterTaskInfo;

    public TaskCompleteAfterApi() {
    }

    public TaskCompleteAfterApi(boolean processIsStop, List<UserTaskInfo> afterTaskInfo) {
        this.processIsStop = processIsStop;
        this.afterTaskInfo = afterTaskInfo;
    }

    public boolean isProcessIsStop() {
        return processIsStop;
    }

    public void setProcessIsStop(boolean processIsStop) {
        this.processIsStop = processIsStop;
    }

    public List<UserTaskInfo> getAfterTaskInfo() {
        return afterTaskInfo;
    }

    public void setAfterTaskInfo(List<UserTaskInfo> afterTaskInfo) {
        this.afterTaskInfo = afterTaskInfo;
    }


    @Override
    public String toString() {
        return "TaskCompleteAfterApi{" +
                "processIsStop=" + processIsStop +
                ", afterTaskInfo=" + afterTaskInfo +
                '}';
    }
}
