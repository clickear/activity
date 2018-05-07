package cn.huimin.process.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @project: HuiminPlatform
 * @version: 1.0
 * @date: 2016-09-28 04:00:42
 * @author Administrator
 * @classDescription: employeerole实体模型
 */
public class ProcessRole implements Serializable{

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdminid() {
        return adminid;
    }

    public void setAdminid(Integer adminid) {
        this.adminid = adminid;
    }

    public Integer getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(Integer departmentid) {
        this.departmentid = departmentid;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public Integer getBranchid() {
        return branchid;
    }

    public void setBranchid(Integer branchid) {
        this.branchid = branchid;
    }


    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(Integer creatorid) {
        this.creatorid = creatorid;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessDefKey() {
        return processDefKey;
    }

    public void setProcessDefKey(String processDefKey) {
        this.processDefKey = processDefKey;
    }

    private Integer id;
    private Integer adminid;
    private Integer departmentid;
    private Integer roleid;
    private Integer branchid;
    private Integer state;
    private Integer creatorid;
    private String processId;
    private String taskId;
    private String processDefKey;

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    private Date createtime;
    private Date updatetime;

    @Override
    public String toString() {
        return "ProcessRole{" +
                "id=" + id +
                ", adminid=" + adminid +
                ", departmentid=" + departmentid +
                ", roleid=" + roleid +
                ", branchid=" + branchid +
                ", state=" + state +
                ", creatorid=" + creatorid +
                ", processId='" + processId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", processDefKey='" + processDefKey + '\'' +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                '}';
    }
}