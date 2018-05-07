package cn.huimin.process.web.contorller;


import cn.huimin.process.web.model.SystemEntity;
import cn.huimin.process.web.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 跳转页面
 */
@Controller
@RequestMapping("page")
public class PageContorller {

	@Autowired
	SystemService systemService;
	//跳转页面
	@RequestMapping("/{page}")
	public String pageReturn( @PathVariable String page){
		return "jsp/"+page;
	}


	@RequestMapping("/processManager/{page}")
	public String page(@PathVariable String page, HttpServletRequest request){
		List<SystemEntity> systemEntities = systemService.queryList(null);
		request.setAttribute("systemEntities",systemEntities);
		return "jsp/processManager/"+page;
	}

	@RequestMapping("/formManager/{page}")
	public String formpage( @PathVariable String page){
		return "jsp/formManager/"+page;
	}



	@RequestMapping("/from/{page}")
	public String formPage( @PathVariable String page){
		return "jsp/form/"+page;
	}

}
