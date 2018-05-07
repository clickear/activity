package cn.huimin.process.web.dao;


import cn.huimin.process.web.model.Replace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 */
public interface ReplaceDao {
    public void insert(Replace replace);

    public void deleteById(List<String> id);

    /**
     * 更新同意的状态
     * @param agree
     * @param id
     */
    public void updateAgree(@Param("agree") Integer agree,@Param("id") Integer id);

    public List<Replace> query(Replace replace);


}
