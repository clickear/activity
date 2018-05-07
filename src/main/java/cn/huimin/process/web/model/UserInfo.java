package cn.huimin.process.web.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/14.
 */
public class UserInfo implements Serializable{
    private Integer id;
    private Integer adminid;
    private String adminLogin;
    private String adminname;
    private String adminPassword;
    private Integer branchid;
    private String branchname;
    private Integer departmentid;
    private String departmentname;
    private Integer roleid;
    private String specrolename;
    //公司部门岗位的id拼接|
    public  String getEmployeegroup() {
        return employeegroup;
    }public void   setEmployeegroup(String employeegroup) {
        this.employeegroup = employeegroup;
    }private String employeegroup;





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

    public String getAdminLogin() {
        return adminLogin;
    }

    public void setAdminLogin(String adminLogin) {
        this.adminLogin = adminLogin;
    }

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
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

    public String getSpecrolename() {
        return specrolename;
    }

    public void setSpecrolename(String specrolename) {
        this.specrolename = specrolename;
    }
}
