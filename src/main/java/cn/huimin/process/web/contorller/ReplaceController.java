package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.model.Replace;
import cn.huimin.process.web.model.UserInfo;
import cn.huimin.process.web.service.ReplaceService;
import cn.huimin.process.web.util.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */
@Controller
@RequestMapping("replace")
public class ReplaceController {


    //日志打印
    private static  final transient Logger log = Logger.getLogger(ReplaceController .class);
    @Autowired
    private ReplaceService replaceService;


    /**
     * 分页查询当前人需要代签的列表
     * @param page
     * @param pageSize
     * @param replace
     * @param request
     * @return
     */
    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public Page queryPage(Integer page, Integer pageSize, Replace replace, HttpServletRequest request){
       UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        replace.setCheckPerson(String.valueOf(userInfo.getAdminid()));
        Page pageJson = replaceService.queryPage(replace,page,pageSize);
        log.info(pageJson);
        return pageJson;
    }

    /**
     * 查询当前用户的需要回复代签的列表
     * @param page
     * @param pageSize
     * @param replace
     * @param request
     * @return
     */
    @RequestMapping(value = "queryAgreePage",method = RequestMethod.POST)
    @ResponseBody
    public Page queryAgreePage(Integer page, Integer pageSize, Replace replace, HttpServletRequest request){
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        replace.setChangePerson(String.valueOf(userInfo.getAdminid()));
        Page pageJson = replaceService.queryPage(replace,page,pageSize);
        log.info(pageJson);
        return pageJson;
    }






    @RequestMapping("gotoAdd")
    public String gotoAdd(){
        return "jsp/addReplace";
    }



    /**
     * 更新同意的状态
     * @param agree
     * @param id
     */
    @RequestMapping("updateAgree")
    @ResponseBody
    public SimpleResult updateAgree( Integer agree, Integer id){
        SimpleResult simpleResult = new SimpleResult();
        try{
            simpleResult.setSuccess(true);
            replaceService.updateAgree(agree,id);
        }catch (Exception e){
            simpleResult.setSuccess(false);
            log.error(e);
        }finally {
             return simpleResult;
        }
    }

    @RequestMapping("insert")
    @ResponseBody
    public SimpleResult insert(Replace replace,HttpServletRequest request) {
        SimpleResult simpleResult = new SimpleResult();
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Constants.userInfo);
        replace.setCheckPerson(String.valueOf(userInfo.getAdminid()));
        try {
            replaceService.insert(replace);
            simpleResult.setMessage("添加成功");
            simpleResult.setSuccess(true);
        } catch (Exception e) {
            simpleResult.setMessage("添加失败");
            simpleResult.setSuccess(false);
            log.error(e);
        } finally {
            return simpleResult;
        }
    }

    @RequestMapping("deleteByIds")
    @ResponseBody
    public SimpleResult deleteByIds(String ids){
        SimpleResult simpleResult = new SimpleResult();
        String[] id = ids.split(",");
        List<String> list = Arrays.asList(id);
        try {
            replaceService.deleteById(list);
            simpleResult.setMessage("删除成功");
            simpleResult.setSuccess(true);
        }catch (Exception e){
            simpleResult.setMessage("删除失败");
            simpleResult.setSuccess(false);
            log.error(e);

        }finally {
            return simpleResult;
        }

    }




}
