package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.ProcessType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wyp on 2017/4/18.
 */
public interface ProcessTypeDao {

    List<ProcessType> queryList(ProcessType processType);

    ProcessType queryById(@Param("id") String codeId, @Param("systemId") String systemId);

 }
