package cn.huimin.process.web.model;

import java.io.Serializable;

/**
 * Created by wyp on 2017/3/17.
 */
public class UserTaskVarPool implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String processKey;
    private String processDefId;
    private String userTaskVarInfo;
    private Integer type;

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

    public String getUserTaskVarInfo() {
        return userTaskVarInfo;
    }

    public void setUserTaskVarInfo(String userTaskVarInfo) {
        this.userTaskVarInfo = userTaskVarInfo;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    @Override
    public String toString() {
        return "UserTaskVarPool{" +
                "id=" + id +
                ", processKey='" + processKey + '\'' +
                ", processDefId='" + processDefId + '\'' +
                ", userTaskVarInfo='" + userTaskVarInfo + '\'' +
                ", type=" + type +
                '}';
    }
}
