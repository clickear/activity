package cn.huimin.process.web.model;


import java.io.Serializable;
import java.util.Date;

/**
 * 一个流程所需要的信息
 */
public class ProcessData implements Serializable{

    private static final long serialVersionUID = 1L;
    //申请人
    private String startUserId;
    private String processDefName;
/*    #流转中 1
            #已挂体 2
            #待处理 3
            #已完成 4*/
    private Integer state;

    private Date applyTime;
    private Date stopTime;
    private String processInstanceId;
    //候选人
    private String nextCheck;
    //当前活动节点的名称
    private String currentTaskName;
    //任务id
    private String taskId;
    //流程key
    private String processKey;
    //系统id
    private String systemId;

    private String processInstanceName;

    private Integer processCategory;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Integer getProcessCategory() {
        return processCategory;
    }

    public void setProcessCategory(Integer processCategory) {
        this.processCategory = processCategory;
    }

    //审批结果
    private Boolean result;

    public String getProcessDefName() {
        return processDefName;
    }

    public void setProcessDefName(String processDefName) {
        this.processDefName = processDefName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getNextCheck() {
        return nextCheck;
    }

    public void setNextCheck(String nextCheck) {
        this.nextCheck = nextCheck;
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }


    public String getCurrentTaskName() {
        return currentTaskName;
    }

    public void setCurrentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    @Override
    public String toString() {
        return "ProcessData{" +
                "startUserId='" + startUserId + '\'' +
                ", processDefName='" + processDefName + '\'' +
                ", state=" + state +
                ", applyTime=" + applyTime +
                ", stopTime=" + stopTime +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", nextCheck='" + nextCheck + '\'' +
                ", currentTaskName='" + currentTaskName + '\'' +
                ", taskId='" + taskId + '\'' +
                ", processKey='" + processKey + '\'' +
                ", systemId='" + systemId + '\'' +
                ", processCategory=" + processCategory +
                ", result=" + result +
                '}';
    }
}
