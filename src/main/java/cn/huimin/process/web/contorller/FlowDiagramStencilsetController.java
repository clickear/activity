/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.huimin.process.web.contorller;

import java.io.InputStream;

import cn.huimin.process.web.util.UUIDutils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.ActivitiException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Tijs Rademakers
 */
@RestController
public class FlowDiagramStencilsetController {
    private final static Logger logger = LoggerFactory.getLogger(FlowDiagramStencilsetController.class);

  @RequestMapping(value="/editor/stencilset", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public @ResponseBody String getStencilset() {
    InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json");
    try {
       String json = IOUtils.toString(stencilsetStream, "utf-8");
      //解析模板字符串
      JSONObject jsonObject = JSON.parseObject(json);
       JSONArray jsonArray = jsonObject.getJSONArray("propertyPackages");
        //获取流程的整体配置
       JSONObject jsonObject1 =jsonArray.getJSONObject(0);
        jsonObject1.getJSONArray("properties").getJSONObject(0).put("value","a"+UUIDutils.createUUID());
      //设置id的地方
      /*JSONObject jsonObject2 = jsonArray.getJSONObject(1);
      //设置id为uuid
      jsonObject2.getJSONArray("properties").getJSONObject(0).put("value", UUIDutils.createUUID());
        String jsoss = jsonObject.toJSONString();
        System.out.print(jsoss);*/
      return jsonObject.toString();
    } catch (Exception e) {
      throw new ActivitiException("Error while loading stencil set", e);
    }
  }
}
