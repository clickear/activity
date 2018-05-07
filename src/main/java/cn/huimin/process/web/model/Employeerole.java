package cn.huimin.process.web.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-7-19.
 */
public class Employeerole  implements Serializable {
    private Integer id;
    private Integer adminid;
    private String adminname;
    private Integer departmentid;
    private String departmentname;
    private Integer roleid;
    private Integer ismain;
    private String specrolename;
    private Integer orderbyid;
    private Integer branchid;
    private String branchname;
    private String createtime;
    private String updatetime;
    private Integer state;
    private Integer creatorid;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    private String remark;


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

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname;
    }

    public Integer getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(Integer departmentid) {
        this.departmentid = departmentid;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public Integer getIsmain() {
        return ismain;
    }

    public void setIsmain(Integer ismain) {
        this.ismain = ismain;
    }

    public String getSpecrolename() {
        return specrolename;
    }

    public void setSpecrolename(String specrolename) {
        this.specrolename = specrolename;
    }

    public Integer getOrderbyid() {
        return orderbyid;
    }

    public void setOrderbyid(Integer orderbyid) {
        this.orderbyid = orderbyid;
    }

    public Integer getBranchid() {
        return branchid;
    }

    public void setBranchid(Integer branchid) {
        this.branchid = branchid;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employeerole that = (Employeerole) o;

        return adminid.equals(that.adminid);

    }

    @Override
    public int hashCode() {
        return adminid.hashCode();
    }
}
