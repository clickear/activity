package cn.huimin.process.web.service.imp;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.huimin.process.web.dao.ByteArrayDao;
import cn.huimin.process.web.dao.ProcessModelDao;
import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.model.ByteArrayEntity;
import cn.huimin.process.web.model.ProcessModel;
import cn.huimin.process.web.service.ProcessModelService;
import cn.huimin.process.web.util.UUIDutils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


/**
 * 流程模型service实现类
 */
@Service
public class ProcessModelServiceImpl implements ProcessModelService{

	@Autowired
	private RepositoryService repositoryService;

    @Autowired
    private ProcessModelDao processModelDao;
    @Autowired
    private ByteArrayDao byteArrayDao;
    @Override
    public Page queryPage(ProcessModel model, Integer start, Integer max) {
        Page page = new Page();
        PageHelper.startPage(start,max);
        List<ProcessModel> list = processModelDao.query(model);
        PageInfo<ProcessModel> pageInfo = new PageInfo<ProcessModel>(list);
        page.setRows(pageInfo.getList());
        page.setPage(pageInfo.getPrePage());
        page.setTotal(pageInfo.getTotal());
        return page;
    }

	@Override
	public ProcessModel selectOneById(String id) {
		return processModelDao.selectOneById(id);
	}

	@Override
	public boolean copyModel(String id) {
		boolean flag=false;
		ProcessModel pm = selectOneById(id);
		if (pm==null) {
			flag=false;
		}else {
			ByteArrayEntity byteArrayEntity=new ByteArrayEntity();
			byteArrayEntity.setId(pm.getEditorSourceValueId());
			String editorSourceValueId = UUIDutils.createUUID();
			byteArrayEntity.setTempId(editorSourceValueId);
			byteArrayDao.insertByteArray(byteArrayEntity);
			byteArrayEntity.setId(pm.getEditorSourceExtraValueId());
			String editorSourceExtraValueId = UUIDutils.createUUID();
			byteArrayEntity.setTempId(editorSourceExtraValueId);
			byteArrayDao.insertByteArray(byteArrayEntity);
			
			pm.setEditorSourceExtraValueId(editorSourceExtraValueId);
			pm.setEditorSourceValueId(editorSourceValueId);
			pm.setDeployId("");
			String newModelId = "b".concat(UUIDutils.createUUID());
			pm.setProcessDefKey(newModelId);
			processModelDao.insertBySelect(pm);
			try {
				String editorSource =	 new String(repositoryService.getModelEditorSource(newModelId),"utf-8");
				String newEditor =this.replaceProcessKey(editorSource);
				repositoryService.addModelEditorSource(newModelId,newEditor.getBytes("utf-8"));
			}catch (UnsupportedEncodingException e){
				e.printStackTrace();
			}
			flag=true;
		}
		
		return flag;
	}


	private String replaceProcessKey(String editorSource){
		//解析数据
		JSONObject jsonObject = JSON.parseObject(editorSource);
		//获取属性的值
		JSONObject processDef = jsonObject.getJSONObject("properties");
		//获取流程key
		String processKey = processDef.getString("process_id");
		//c代表流程key是复制过来的
		processDef.put("process_id","c".concat(UUIDutils.createUUID()));
		return jsonObject.toJSONString();
	}
}








