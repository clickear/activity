package cn.huimin.process.web.service.imp;


import cn.huimin.process.web.dao.ActCreationDao;
import cn.huimin.process.web.service.ActCreationService;
import cn.huimin.process.web.model.ActCreation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 对运行时创建活动节点service
 */
@Component("actCreationService")
public class ActCreationServiceImpl extends BaseActivityServiceImpl<ActCreationDao> implements
		ActCreationService {

	public List<ActCreation> list()
	{
	 	List<ActCreation> actCreations = new ArrayList<ActCreation>();
		actCreations.addAll(mapperClass.findAll());
		return actCreations;
	}

	@Transactional
	public void removeAll()
	{
		mapperClass.deleteAll();
	}

	public void deleteById(Integer id){
		mapperClass.deleteById(id);
	}

	@Transactional
	public void save(ActCreation actCreation)
	{

		mapperClass.save(actCreation);
	}
}
