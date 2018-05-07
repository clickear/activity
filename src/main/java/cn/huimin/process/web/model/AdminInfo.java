package cn.huimin.process.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/21.
 */
public class AdminInfo implements Serializable{
    private Integer adminId;
    private String adminLogin;
    private String adminPassword;
    private Integer roleId;
    private String adminEmail;
    private String adminName;
    private String adminTelphone;
    private Integer shopId;
    private Integer adminState;
    private Integer adminManager;
    private Integer province;
    private Integer city;
    private Integer district;
    private Integer department;
    private Integer branchId;
    private Integer storeId;
    private Integer departmentId;
    private String s_MobileCheck;
    private String adminNo;
    private Integer sex;
    private Date birthDay;
    private String idCart;
    private String passprot;
    private String gangAopass;
    private String birthAdress;
    private Integer isMerried;
    private String nation;
    private String politicalFace;
    private String profileAdress;
    private Integer profileIsIn;
    private Date workStartDate;
    private Date joinDate;
    private Date leaveDate;
    private Integer isrejoin;
    private String workaddress;
    private Date fromdate;
    private Date enddate;
    private Integer hascontract;
    private String typeState;
    private Integer type;
    private Date probation;
    private Date payprobation;
    private String photoPath;
    private String address;
    private String emerginceContactor;
    private String emergincePhone;
    private Integer hasOARight;
    private String remark;
    private Date createtime;
    private Integer creatorid;

    public AdminInfo() {
    }

    public AdminInfo(Integer adminId, String adminLogin, String adminPassword, Integer roleId, String adminEmail, String adminName, String adminTelphone, Integer shopId, Integer adminState, Integer adminManager, Integer province, Integer city, Integer district, Integer department, Integer branchId, Integer storeId, Integer departmentId, String s_MobileCheck, String adminNo, Integer sex, Date birthDay, String idCart, String passprot, String gangAopass, String birthAdress, Integer isMerried, String nation, String politicalFace, String profileAdress, Integer profileIsIn, Date workStartDate, Date joinDate, Date leaveDate, Integer isrejoin, String workaddress, Date fromdate, Date enddate, Integer hascontract, String typeState, Integer type, Date probation, Date payprobation, String photoPath, String address, String emerginceContactor, String emergincePhone, Integer hasOARight, String remark, Date createtime, Integer creatorid) {
        this.adminId = adminId;
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
        this.roleId = roleId;
        this.adminEmail = adminEmail;
        this.adminName = adminName;
        this.adminTelphone = adminTelphone;
        this.shopId = shopId;
        this.adminState = adminState;
        this.adminManager = adminManager;
        this.province = province;
        this.city = city;
        this.district = district;
        this.department = department;
        this.branchId = branchId;
        this.storeId = storeId;
        this.departmentId = departmentId;
        this.s_MobileCheck = s_MobileCheck;
        this.adminNo = adminNo;
        this.sex = sex;
        this.birthDay = birthDay;
        this.idCart = idCart;
        this.passprot = passprot;
        this.gangAopass = gangAopass;
        this.birthAdress = birthAdress;
        this.isMerried = isMerried;
        this.nation = nation;
        this.politicalFace = politicalFace;
        this.profileAdress = profileAdress;
        this.profileIsIn = profileIsIn;
        this.workStartDate = workStartDate;
        this.joinDate = joinDate;
        this.leaveDate = leaveDate;
        this.isrejoin = isrejoin;
        this.workaddress = workaddress;
        this.fromdate = fromdate;
        this.enddate = enddate;
        this.hascontract = hascontract;
        this.typeState = typeState;
        this.type = type;
        this.probation = probation;
        this.payprobation = payprobation;
        this.photoPath = photoPath;
        this.address = address;
        this.emerginceContactor = emerginceContactor;
        this.emergincePhone = emergincePhone;
        this.hasOARight = hasOARight;
        this.remark = remark;
        this.createtime = createtime;
        this.creatorid = creatorid;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminLogin() {
        return adminLogin;
    }

    public void setAdminLogin(String adminLogin) {
        this.adminLogin = adminLogin;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminTelphone() {
        return adminTelphone;
    }

    public void setAdminTelphone(String adminTelphone) {
        this.adminTelphone = adminTelphone;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getAdminState() {
        return adminState;
    }

    public void setAdminState(Integer adminState) {
        this.adminState = adminState;
    }

    public Integer getAdminManager() {
        return adminManager;
    }

    public void setAdminManager(Integer adminManager) {
        this.adminManager = adminManager;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getDistrict() {
        return district;
    }

    public void setDistrict(Integer district) {
        this.district = district;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getS_MobileCheck() {
        return s_MobileCheck;
    }

    public void setS_MobileCheck(String s_MobileCheck) {
        this.s_MobileCheck = s_MobileCheck;
    }

    public String getAdminNo() {
        return adminNo;
    }

    public void setAdminNo(String adminNo) {
        this.adminNo = adminNo;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getIdCart() {
        return idCart;
    }

    public void setIdCart(String idCart) {
        this.idCart = idCart;
    }

    public String getPassprot() {
        return passprot;
    }

    public void setPassprot(String passprot) {
        this.passprot = passprot;
    }

    public String getGangAopass() {
        return gangAopass;
    }

    public void setGangAopass(String gangAopass) {
        this.gangAopass = gangAopass;
    }

    public String getBirthAdress() {
        return birthAdress;
    }

    public void setBirthAdress(String birthAdress) {
        this.birthAdress = birthAdress;
    }

    public Integer getIsMerried() {
        return isMerried;
    }

    public void setIsMerried(Integer isMerried) {
        this.isMerried = isMerried;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getPoliticalFace() {
        return politicalFace;
    }

    public void setPoliticalFace(String politicalFace) {
        this.politicalFace = politicalFace;
    }

    public String getProfileAdress() {
        return profileAdress;
    }

    public void setProfileAdress(String profileAdress) {
        this.profileAdress = profileAdress;
    }

    public Integer getProfileIsIn() {
        return profileIsIn;
    }

    public void setProfileIsIn(Integer profileIsIn) {
        this.profileIsIn = profileIsIn;
    }

    public Date getWorkStartDate() {
        return workStartDate;
    }

    public void setWorkStartDate(Date workStartDate) {
        this.workStartDate = workStartDate;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public Integer getIsrejoin() {
        return isrejoin;
    }

    public void setIsrejoin(Integer isrejoin) {
        this.isrejoin = isrejoin;
    }

    public String getWorkaddress() {
        return workaddress;
    }

    public void setWorkaddress(String workaddress) {
        this.workaddress = workaddress;
    }

    public Date getFromdate() {
        return fromdate;
    }

    public void setFromdate(Date fromdate) {
        this.fromdate = fromdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public Integer getHascontract() {
        return hascontract;
    }

    public void setHascontract(Integer hascontract) {
        this.hascontract = hascontract;
    }

    public String getTypeState() {
        return typeState;
    }

    public void setTypeState(String typeState) {
        this.typeState = typeState;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getProbation() {
        return probation;
    }

    public void setProbation(Date probation) {
        this.probation = probation;
    }

    public Date getPayprobation() {
        return payprobation;
    }

    public void setPayprobation(Date payprobation) {
        this.payprobation = payprobation;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmerginceContactor() {
        return emerginceContactor;
    }

    public void setEmerginceContactor(String emerginceContactor) {
        this.emerginceContactor = emerginceContactor;
    }

    public String getEmergincePhone() {
        return emergincePhone;
    }

    public void setEmergincePhone(String emergincePhone) {
        this.emergincePhone = emergincePhone;
    }

    public Integer getHasOARight() {
        return hasOARight;
    }

    public void setHasOARight(Integer hasOARight) {
        this.hasOARight = hasOARight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(Integer creatorid) {
        this.creatorid = creatorid;
    }
}
