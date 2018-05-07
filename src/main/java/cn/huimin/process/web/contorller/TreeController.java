package cn.huimin.process.web.contorller;

import cn.huimin.process.web.util.EhrApiConstants;
import cn.huimin.process.web.util.HttpClientUtils;
import cn.huimin.web.jwt.sign.SHA256Sign;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wyp on 2017/2/20.
 */
@RequestMapping("tree")
@Controller
public class TreeController {

     private final static Logger logger = LoggerFactory.getLogger(TreeController.class);

    @Value("${organzationAll}")
    private  String organzationAll;

    /**
     * 创建对象
     * @return
     */
    @RequestMapping(value = "listTree",method = RequestMethod.POST)
    @ResponseBody
    public JSONArray listTree(HttpServletRequest request) {
        //获取树的数据
       Object object = request.getSession().getAttribute(EhrApiConstants.TREE_LIST_DATA);
        if(object!=null){
            JSONArray jsonArray = (JSONArray) object;
            logger.info("树的结构 {}",jsonArray);
            return jsonArray;
        }
        //参数准备
        Map<String,String> params = new HashMap<String,String>(0);
        JSONObject jsonObject = new JSONObject();
        params = SHA256Sign.sign(params, "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N");
        String ctime = params.get("ctime");
        String nonce = params.get("nonce");
        String sign = params.get("sign");
        //参数类型提供
        jsonObject.put("ctime",ctime);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        String json =HttpClientUtils.getHttpParamsWithOutEncode(organzationAll,jsonObject);
        logger.info("请求所有公司和部门信息: {} ",json);
        //获取所有公司部门信息
        JSONObject allJson = JSONObject.parseObject(json);
        JSONArray jsonArray= allJson.getJSONArray("data");
        jsonArray.getJSONObject(0).put("open",true);
        request.getSession().setAttribute(EhrApiConstants.TREE_LIST_DATA,jsonArray);
        request.getSession().setMaxInactiveInterval(900);
        if (logger.isInfoEnabled()) {
        	 logger.info("树的结构 {}",jsonArray);
		}
       
        return jsonArray;
    }


}
