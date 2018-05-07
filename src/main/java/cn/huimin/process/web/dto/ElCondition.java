package cn.huimin.process.web.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/1.
 */
public class ElCondition implements Serializable {
    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    private boolean show;

    public String getGateName() {
        return gateName;
    }

    public void setGateName(String gateName) {
        this.gateName = gateName;
    }

    private String gateName;

    public String getElStringName() {
        return elStringName;
    }

    public void setElStringName(String elStringName) {
        this.elStringName = elStringName;
    }

    private String elStringName;

}
