package cn.huimin.process.web.dao;

import cn.huimin.process.web.model.InformProcess;

import java.util.List;

/**
 * 知会的事项
 */
public interface InformProcessDao {

    //List<InformProcess> queryInformActPage(InformProcess informProcess);
    //添加一个知会事项
    void insert(InformProcess informProcess);
}
