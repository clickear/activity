package cn.huimin.process.web.dto;

import java.io.Serializable;

/**
 * Created by wyp on 2017/2/20.
 */
public class Tree implements Serializable{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;
    /**
     * 当前树节点id
     */
    private String id;
    /**
     * 当前树的父节点
     */
    private String pId;
    /**
     * 当前树节点名称
     */
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "id='" + id + '\'' +
                ", pId='" + pId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
