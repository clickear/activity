package cn.huimin.process.web.model;

import java.io.Serializable;

/**
 * @project: HuiminPlatform
 * @version: 1.0
 * @date: 2016-09-28 05:08:58
 * @author Administrator
 * @classDescription: role实体模型
 */
public class Role implements Serializable{
    /**
	 *  
	 */ 
	private static final long serialVersionUID = 1L;
    private Integer r_id;
    private String r_name;
    private String r_remark;
    private String r_duty;
    private String r_level;
    private Integer r_istrue;
    private String createtime;
    private String updatetime;
	private String r_Did;
    public Role() {

    }

	public String getR_Did() {
		return r_Did;
	}

	public void setR_Did(String r_Did) {
		this.r_Did = r_Did;
	}

	/**
	 * @return 主键
	 */
	public Integer getR_id() {
		return r_id;
	}

	/**
	 * @param 主键
	 */
	public void setR_id(Integer r_id) {
		this.r_id = r_id;
	}
	/**
	 * @return 岗位名称
	 */
	public String getR_name() {
		return r_name;
	}

	/**
	 * @param 岗位名称
	 */
	public void setR_name(String r_name) {
		this.r_name = r_name;
	}
	/**
	 * @return 岗位描述
	 */
	public String getR_remark() {
		return r_remark;
	}

	/**
	 * @param 岗位描述
	 */
	public void setR_remark(String r_remark) {
		this.r_remark = r_remark;
	}
	/**
	 * @return 职责
	 */
	public String getR_duty() {
		return r_duty;
	}

	/**
	 * @param 职责
	 */
	public void setR_duty(String r_duty) {
		this.r_duty = r_duty;
	}
	/**
	 * @return 属性（级别）
	 */
	public String getR_level() {
		return r_level;
	}

	/**
	 * @param 属性（级别）
	 */
	public void setR_level(String r_level) {
		this.r_level = r_level;
	}
	/**
	 * @return 是否有效
	 */
	public Integer getR_istrue() {
		return r_istrue;
	}

	/**
	 * @param 是否有效
	 */
	public void setR_istrue(Integer r_istrue) {
		this.r_istrue = r_istrue;
	}
	/**
	 * @return 
	 */
	public String getCreatetime() {
		return createtime;
	}

	/**
	 * @param 
	 */
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	/**
	 * @return 
	 */
	public String getUpdatetime() {
		return updatetime;
	}

	/**
	 * @param 
	 */
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
}