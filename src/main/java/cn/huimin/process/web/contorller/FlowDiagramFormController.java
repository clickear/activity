package cn.huimin.process.web.contorller;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.model.FlowDiagramFormModel;
import cn.huimin.process.web.service.FlowDiagramFormService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


@Controller
@RequestMapping("/flowDiagram")
public class FlowDiagramFormController {
	@Autowired
	FlowDiagramFormService formService;

	@RequestMapping(value = "/detail.do", method = RequestMethod.POST)
	@ResponseBody
	public Object detailFrom(HttpServletRequest request) {
		String formId = request.getParameter("formId");
		FlowDiagramFormModel fdfm=formService.detail(formId);
		return JSONObject.toJSON(fdfm);
	}
	
	@RequestMapping(value = "/update.do", method = RequestMethod.POST)
	@ResponseBody
	public String updateFrom(HttpServletRequest request) {
		String formId = request.getParameter("formId");
		
		String parse_form = request.getParameter("parse_form");
		String formType = request.getParameter("formType");
		System.out.println(formType);
		int index1 = parse_form.indexOf("\"template\":");
		int index2 = parse_form.indexOf("\"parse\":");
		String string = parse_form.substring(index1 + 12, index2 - 6);
		string = string.replace("\\", "");
		string = string.replace("{", "");
		string = string.replace("}", "");
		string = string.replace("|", "");
		string = string.replace("-", "");
		
		int row=formService.updateFrom(formId,formType,string);
		JSONObject json=new JSONObject();
		if(row>0){
			json.put("status", 0);
			json.put("msg", "修改成功");
		}else{
			json.put("status", 1);
			json.put("msg", "修改失败");	
		}
		return json.toJSONString();
	}
	
	@RequestMapping(value = "/queryPage.do",produces = "application/json", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
	public Object findList(FlowDiagramFormModel flowDiagramFormModel, @RequestParam(value="page",defaultValue="1")Integer page,
			@RequestParam(value="pageSize",defaultValue="10")Integer pageSize) {
		Page page1 = formService.findList(flowDiagramFormModel, page, pageSize);
		return page1;
	}
	//分页查询表单id和表单内容
	@RequestMapping(value = "/queryIdAndName.do",produces = "application/json", method = {RequestMethod.POST})
	@ResponseBody
	public Object queryIdAndName(FlowDiagramFormModel flowDiagramFormModel, @RequestParam(value="page",defaultValue="1")Integer page,
						   @RequestParam(value="pageSize",defaultValue="5")Integer pageSize) {
		Page page1 = formService.queryNameAndId(flowDiagramFormModel, page, pageSize);
		return page1;
	}


	
	@RequestMapping(value = "/findForms.do", method = RequestMethod.POST)
	@ResponseBody
	public Object findForms() {
		List<Map<String, Object>> list = formService.findForms();
		return JSON.toJSON(list);
	}

	@RequestMapping(value = "/deleteForm.do", method = RequestMethod.POST)
	@ResponseBody
	public Object deleteForm(HttpServletRequest request) {
		String formIde = request.getParameter("formId");
		int row=formService.deleteForm(formIde);
		JSONObject json=new JSONObject();
		if(row>0){
			json.put("status", 0);
			json.put("msg", "删除成功");
		}else{
			json.put("status", 1);
			json.put("mgs", "删除失败");
		}
		return json.toJSONString();
	}
	
	@RequestMapping(value = "/addForm.do", method = RequestMethod.POST)
	@ResponseBody
	public Object addForm(HttpServletRequest request) {
		// String type_value = request.getParameter("type_value");
		// String formid = request.getParameter("formid");
		String parse_form = request.getParameter("parse_form");
		String formType = request.getParameter("formType");
		System.out.println(formType);
		int index1 = parse_form.indexOf("\"template\":");
		int index2 = parse_form.indexOf("\"parse\":");
		String string = parse_form.substring(index1 + 12, index2 - 6);
		string = string.replace("\\", "");
		string = string.replace("{", "");
		string = string.replace("}", "");
		string = string.replace("|", "");
		string = string.replace("-", "");
		formService.addForm(formType, string);
		return string;
	}
}