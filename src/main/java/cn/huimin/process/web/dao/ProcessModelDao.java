package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.ProcessModel;

import java.util.List;

/**
 * 流程模型查詢
 */
public interface ProcessModelDao {

    List<ProcessModel>  query(ProcessModel processModel);

	ProcessModel selectOneById(String id);
	int insertBySelect(ProcessModel processModel);
}
