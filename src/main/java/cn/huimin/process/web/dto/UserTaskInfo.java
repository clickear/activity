package cn.huimin.process.web.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by wyp on 2017/5/11
 * 任务节点详情
 */
public class UserTaskInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String taskId;
    private String processInstancId;
    private String taskName;
    private List<String> handlers;
    private List<String> roleIds;
    //部门id
    private List<String> departmentId;

    private List<String> specificRoleIds;
    //1代表总部 2代表分公司
    private Integer nodeBelong;

    private String processKey;
    private Date handleTime;
    private Integer priority;
    private String businessState;
    //任务类别
    private String taskType;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstancId() {
        return processInstancId;
    }

    public void setProcessInstancId(String processInstancId) {
        this.processInstancId = processInstancId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<String> handlers) {
        this.handlers = handlers;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public Integer getNodeBelong() {
        return nodeBelong;
    }

    public void setNodeBelong(Integer nodeBelong) {
        this.nodeBelong = nodeBelong;
    }

    public List<String> getSpecificRoleIds() {
        return specificRoleIds;
    }

    public void setSpecificRoleIds(List<String> specificRoleIds) {
        this.specificRoleIds = specificRoleIds;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public String getBusinessState() {
        return businessState;
    }

    public void setBusinessState(String businessState) {
        this.businessState = businessState;
    }


    public List<String> getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(List<String> departmentId) {
        this.departmentId = departmentId;
    }


    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    @Override
    public String toString() {
        return "UserTaskInfo{" +
                "taskId='" + taskId + '\'' +
                ", processInstancId='" + processInstancId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", handlers=" + handlers +
                ", roleIds=" + roleIds +
                ", departmentId=" + departmentId +
                ", processKey='" + processKey + '\'' +
                ", handleTime=" + handleTime +
                ", priority=" + priority +
                ", businessState='" + businessState + '\'' +
                ", taskType='" + taskType + '\'' +
                '}';
    }
}
