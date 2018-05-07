package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.ActCreation;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ActCreationDao
{
	@Delete(" DELETE from hm_act_creation")
	void deleteAll();
	@Select("SELECT * FROM hm_act_creation  WHERE STATE_ = 0")
	@Results(value = { @Result(property = "factoryName", column = "FACTORY_NAME"),
			@Result(property = "processDefinitionId", column = "PROCESS_DEFINITION_ID"),
			@Result(property = "processInstanceId", column = "PROCESS_INSTANCE_ID"),
			@Result(property = "propertiesText", column = "PROPERTIES_TEXT") })
	List<ActCreation> findAll();

	@Insert("INSERT INTO hm_act_creation (FACTORY_NAME,PROCESS_DEFINITION_ID,PROCESS_INSTANCE_ID,ACT_ID,DOUSERID,PROPERTIES_TEXT) values (#{factoryName},#{processDefinitionId},#{processInstanceId},#{actId},#{doUserId},#{propertiesText})")
	void save(ActCreation actCreation);
	@Delete("DELETE from hm_act_creation WHERE ID = #{id}")
	void deleteById(Integer id);
}
