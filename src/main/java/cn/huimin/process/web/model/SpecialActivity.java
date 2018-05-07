package cn.huimin.process.web.model;

import java.io.Serializable;


/**
 * 特殊的活动节点
 */
public class SpecialActivity implements Serializable{

    private static final long serialVersionUID = 1L;

    //id 主键
    private Integer id;

    //流程实例id
    private String processId;

    //流程定义
    private String processDef;

    //活动key
    private String activityKey;
    //操作人id
    private Integer userId;
    //流程节点类型
    private Integer activityType;
    //0代表无效 1 代表对当前流程实例有效 2代表流程定义有效
    private Integer state;

    public Integer getId() {
        return id;
    }

    public String getActivityKey() {
        return activityKey;
    }

    public void setActivityKey(String activityKey) {
        this.activityKey = activityKey;
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessDef() {
        return processDef;
    }

    public void setProcessDef(String processDef) {
        this.processDef = processDef;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SpecialActivity{" +
                "id=" + id +
                ", processId='" + processId + '\'' +
                ", processDef='" + processDef + '\'' +
                ", activitiKey='" + activityKey + '\'' +
                ", userId=" + userId +
                ", type=" + activityType +
                ", state=" + state +
                '}';
    }
}
