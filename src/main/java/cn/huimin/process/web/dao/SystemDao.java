package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.SystemEntity;

import java.util.List;

/**
 * Created by wyp on 2017/4/18.
 */
public interface SystemDao {
    List<SystemEntity> queryList(SystemEntity systemEntity);
 }
