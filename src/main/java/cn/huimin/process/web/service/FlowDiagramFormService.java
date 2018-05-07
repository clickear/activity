package cn.huimin.process.web.service;

import java.util.List;
import java.util.Map;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.model.FlowDiagramFormModel;

/**
 * 
 * @author zhangjie
 *
 */
public interface FlowDiagramFormService {

	public Object addForm(String formType, String string) ;

	public List<Map<String, Object>> findForms();

	public int deleteForm(String formIde);
	
	public FlowDiagramFormModel detail(String formIde);

	public int updateFrom(String formId,String formType, String string);

	Page findList(FlowDiagramFormModel diagramFormModel, Integer start, Integer max);
	//分页查询id和名称
	Page queryNameAndId(FlowDiagramFormModel diagramFormModel, Integer start, Integer max);



}
