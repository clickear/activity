package cn.huimin.process.web.model;

import java.io.Serializable;

/**
 * Created by wyp on 2017/10/27.
 */
public class SystemEntity implements Serializable{
    private Integer id;
    private String name;
    private String codeId;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }
}
