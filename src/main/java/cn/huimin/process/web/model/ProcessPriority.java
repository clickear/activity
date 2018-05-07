package cn.huimin.process.web.model;

import java.io.Serializable;
import java.util.Date;

public class ProcessPriority implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3980459169916253656L;
	private Integer id;
	private Integer priority;
	private String procInstId;
	private Date createTime;
	private Integer handTime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getHandTime() {
		return handTime;
	}

	public void setHandTime(Integer handTime) {
		this.handTime = handTime;
	}

	@Override
	public String toString() {
		return "ProcessPriority{" +
				"id=" + id +
				", priority=" + priority +
				", procInstId='" + procInstId + '\'' +
				", createTime=" + createTime +
				", handTime=" + handTime +
				'}';
	}
}
