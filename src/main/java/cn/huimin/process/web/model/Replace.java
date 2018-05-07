package cn.huimin.process.web.model;

/**
 *
 */
public class Replace
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    //流程定义key
    private String processKey;
    //审核人
    private String checkPerson;
    //变更后的人
    private String changePerson;
    //代理结束时间
    private String startTime;
    //代理结束时间
    private String endTime;
    //0代表取消代签 1 代表代签
    private Integer state;
    //同意的状态
    private Integer agree;

    @Override
    public String toString() {
        return "Replace{" +
                "id=" + id +
                ", processKey='" + processKey + '\'' +
                ", checkPerson='" + checkPerson + '\'' +
                ", changePerson='" + changePerson + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", state=" + state +
                ", agree=" + agree +
                '}';
    }

    public Integer getAgree() {
        return agree;
    }

    public void setAgree(Integer agree) {
        this.agree = agree;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getCheckPerson() {
        return checkPerson;
    }

    public void setCheckPerson(String checkPerson) {
        this.checkPerson = checkPerson;
    }

    public String getChangePerson() {
        return changePerson;
    }

    public void setChangePerson(String changePerson) {
        this.changePerson = changePerson;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
