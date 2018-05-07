package cn.huimin.process.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程知会的model
 */
public class InformProcess implements Serializable{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String processInstanceId;
    private String taskId;
    private Integer state;
    //知会人id
    private String informPersonId;
    //操作人
    private String operatePersonId;

    //操作时间
    private Date  createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }


    public String getInformPersonId() {
        return informPersonId;
    }

    public void setInformPersonId(String informPersonId) {
        this.informPersonId = informPersonId;
    }

    public String getOperatePersonId() {
        return operatePersonId;
    }

    public void setOperatePersonId(String operatePersonId) {
        this.operatePersonId = operatePersonId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "InformProcess{" +
                "id=" + id +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", state=" + state +
                ", informPersonId=" + informPersonId +
                ", operatePersonId=" + operatePersonId +
                ", createTime=" + createTime +
                '}';
    }
}
