package cn.huimin.process.web.service.imp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.huimin.process.web.dao.FlowDiagramFormModelDao;
import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.model.FlowDiagramFormModel;
import cn.huimin.process.web.service.FlowDiagramFormService;

/**
 * 
 * @author zhangjie
 *
 */
@Component("flowDiagramFormService")
public class FlowDiagramFormServiceImpl implements FlowDiagramFormService{

	@Autowired
	private FlowDiagramFormModelDao flowDiagramFormModelDao;
	/**
	 * 
	 * 新增表单
	 * @author：tuzongxun
	 * @Title: addForm
	 * @Description: TODO
	 * @param @param formType
	 * @param @param string
	 * @param @return
	 * @date Mar 28, 2016 4:30:18 PM
	 * @throws
	 */
	public Object addForm(String formType, String string) {
		FlowDiagramFormModel flowDiagramFormModel=new FlowDiagramFormModel();
		flowDiagramFormModel.setFormtype(formType);
		flowDiagramFormModel.setParseform(string);
		flowDiagramFormModel.setState(0);
		flowDiagramFormModel.setCreatetime(new Date());
		flowDiagramFormModelDao.insertSelective(flowDiagramFormModel);
		return string;
	}
	
	
	public List<Map<String, Object>> findForms(){
		return flowDiagramFormModelDao.getAll();
	}

	@Override
	public Page findList(FlowDiagramFormModel diagramFormModel, Integer start, Integer max){
		Page page = new Page();
        PageHelper.startPage(start,max);
        diagramFormModel.setState(0);
        List<FlowDiagramFormModel> list = flowDiagramFormModelDao.selectListByModel(diagramFormModel);
        PageInfo<FlowDiagramFormModel> pageInfo = new PageInfo<FlowDiagramFormModel>(list);
        page.setPage(pageInfo.getPrePage());
        page.setRows(list);
        page.setTotal(pageInfo.getTotal());
        return page;
	}

	/**
	 * 分页只查询i
	 * @param diagramFormModel
	 * @param start
	 * @param max
     * @return
     */
	@Override
	public Page queryNameAndId(FlowDiagramFormModel diagramFormModel, Integer start, Integer max) {
		Page page = new Page();
		PageHelper.startPage(start,max);
		diagramFormModel.setState(0);
		List<FlowDiagramFormModel> list = flowDiagramFormModelDao.queryNameAndId(diagramFormModel);
		PageInfo<FlowDiagramFormModel> pageInfo = new PageInfo<FlowDiagramFormModel>(list);
		page.setPage(start);
		page.setRows(list);
		page.setTotalPage(pageInfo.getPages());
		page.setTotal(pageInfo.getTotal());
		return page;
	}


	@Override
	public int deleteForm(String formIde) {
		FlowDiagramFormModel formModel=new FlowDiagramFormModel();
		formModel.setFormid(Integer.parseInt(formIde));
		formModel.setState(1);
		return flowDiagramFormModelDao.updateByPrimaryKeySelective(formModel);
	}


	@Override
	public FlowDiagramFormModel detail(String formIde) {
		FlowDiagramFormModel fdfm=flowDiagramFormModelDao.selectByPrimaryKey(Integer.valueOf(formIde));
		return fdfm;
	}


	@Override
	public int updateFrom(String formId, String formType, String string) {
		FlowDiagramFormModel flowDiagramFormModel=new FlowDiagramFormModel();
		flowDiagramFormModel.setFormtype(formType);
		flowDiagramFormModel.setParseform(string);
		flowDiagramFormModel.setFormid(Integer.parseInt(formId));
		return flowDiagramFormModelDao.updateByPrimaryKeySelective(flowDiagramFormModel);
	}
	
	
}
