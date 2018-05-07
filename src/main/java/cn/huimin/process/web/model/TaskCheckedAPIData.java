package cn.huimin.process.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 已办事项的流程信息
 */
public class TaskCheckedAPIData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String taskId;
    //流程处理key
    private String taskKey;
    //流程节点名称
    private String taskName;

    //流程开始时间
    private Date startTime;

    private String processInstanceId;
    //流程处理时间
    private Date handleTime;
    //发起人id
    private String startUserId;

    //流程定义名称
    private String processDefName;
    //任务节点类型 1.代表审核节点 2代表知会节点
    private String taskType;
    //流程定义key
    private String processDefId;
    //处理结果
    private Boolean result;



    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }



    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefName() {
        return processDefName;
    }

    public void setProcessDefName(String processDefName) {
        this.processDefName = processDefName;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }


    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }


    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    @Override
    public String toString() {
        return "TaskCheckedAPIData{" +
                "taskId='" + taskId + '\'' +
                ", taskKey='" + taskKey + '\'' +
                ", taskName='" + taskName + '\'' +
                ", startTime=" + startTime +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", handleTime=" + handleTime +
                ", startUserId='" + startUserId + '\'' +
                ", processDefName='" + processDefName + '\'' +
                ", taskType='" + taskType + '\'' +
                ", processDefId='" + processDefId + '\'' +
                ", result=" + result +
                '}';
    }
}
