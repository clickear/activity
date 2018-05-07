package cn.huimin.process.web.contorller;

import cn.huimin.process.web.dto.SimpleResult;
import cn.huimin.process.web.util.UUIDutils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/1/18.
 */
@Controller
@RequestMapping("produceNeedParam")
public class ProduceNeedParamController {

    /**
     * 生成uuid
     * @return
     */
    @RequestMapping("produceUUID")
    @ResponseBody
    public SimpleResult produceUUID(){
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.setMessage(UUIDutils.createUUID());
        simpleResult.setSuccess(true);
        return simpleResult;
    }
}
