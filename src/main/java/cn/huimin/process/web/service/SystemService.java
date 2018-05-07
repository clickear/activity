package cn.huimin.process.web.service;

import cn.huimin.process.web.model.SystemEntity;

import java.util.List;

/**
 * Created by wyp on 2017/10/27.
 */
public interface SystemService {

    List<SystemEntity> queryList(SystemEntity systemEntity);

}
