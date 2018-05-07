package cn.huimin.process.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author 张杰
 *
 */
public class FlowDiagramFormModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 112312L;
	
	// 'type' : type_value,'formid':formid,'parse_form':parse_form
	private String type;
	private Integer formid;
	private String parseform;
	private String formtype;
	private Date createtime;
	private Integer state;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getFormid() {
		return formid;
	}

	public void setFormid(Integer formid) {
		this.formid = formid;
	}

	public String getParseform() {
		return parseform;
	}

	public void setParseform(String parseform) {
		this.parseform = parseform;
	}

	public String getFormtype() {
		return formtype;
	}

	public void setFormtype(String formtype) {
		this.formtype = formtype;
	}

	@Override
	public String toString() {
		return "FormModel [ type=" + type + ", formid="
				+ formid + ", parse_form=" + parseform + ", formType="
				+ formtype + "]";
	}

	

	

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	

}