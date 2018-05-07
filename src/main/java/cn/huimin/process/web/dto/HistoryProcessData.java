package cn.huimin.process.web.dto;


import java.io.Serializable;
import java.util.Date;

/**
 * 审批数据记录model
 */
public class HistoryProcessData implements Serializable{

    private static final long serialVersionUID = 1L;
    private String processName;
    //状态是完成
    //1是未完成 2.是正常流程结束 3.中途结束
    private Integer state;
    private Date startTime;
    private Date endTime;
    private String processId;
    //下一步审批人
    private String nextCheck;
    //审批结果
    private String result;
    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getNextCheck() {
        return nextCheck;
    }

    public void setNextCheck(String nextCheck) {
        this.nextCheck = nextCheck;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


}
