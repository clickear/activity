package cn.huimin.process.web.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 表单意见填写
 */
public class FormCheckAdvice implements Serializable{

    private static final long serialVersionUID = 1L;
    //节点名称
    private String taskName;
    //审批人
    private String checkUser;
    //审批意见
    private Date checkDate;
    //审批的结果
    private Boolean checkResult;
    //审批意见
    private String remark;

    @Override
    public String toString() {
        return "FormCheckAdvice{" +
                "taskName='" + taskName + '\'' +
                ", checkUser='" + checkUser + '\'' +
                ", checkDate=" + checkDate +
                ", checkResult='" + checkResult + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormCheckAdvice that = (FormCheckAdvice) o;

        if (!taskName.equals(that.taskName)) return false;
        return checkUser.equals(that.checkUser);

    }

    @Override
    public int hashCode() {
        int result = taskName.hashCode();
        result = 31 * result + checkUser.hashCode();
        return result;
    }

    public Boolean getCheckResult() {

        return checkResult;
    }

    public void setCheckResult(Boolean checkResult) {
        this.checkResult = checkResult;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
