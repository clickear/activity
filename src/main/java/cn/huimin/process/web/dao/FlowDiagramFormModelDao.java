package cn.huimin.process.web.dao;

import java.util.List;
import java.util.Map;

import cn.huimin.process.web.model.FlowDiagramFormModel;

/**
 * 表单
 * @author 张杰
 *
 */
public interface FlowDiagramFormModelDao {
	
	int delete(Integer id);
	
	List<Map<String,Object>> getAll();
	
	/**
     * 插入对象
     *
     * @param FlowDiagramFormMode 对象
     */
    int insertSelective(FlowDiagramFormModel flowDiagramFormModel);

    /**
     * 更新对象
     *
     * @param FlowDiagramFormMode 对象
     */
    int updateByPrimaryKeySelective(FlowDiagramFormModel FlowDiagramFormMode);

    /**
     * 通过主键, 删除对象
     *
     * @param idsList 主键ID集合
     */
    int deleteByPrimaryKey(List<String> idsList);

    /**
     * 通过主键, 查询对象
     *
     * @param id 主键
     * @return
     */
    FlowDiagramFormModel selectByPrimaryKey(Integer id);
    
    /**
     * 按查询条件查询列表
     * @param FlowDiagramFormMode 查询条件
     * @return
     */
    List<FlowDiagramFormModel> selectListByModel(FlowDiagramFormModel flowDiagramFormModel);
    
    /**
     * 分页
     * @param flowDiagramFormModel
     * @return
     */
    List<FlowDiagramFormModel>  queryPage(FlowDiagramFormModel flowDiagramFormModel);

    /**
     * 分页
     * @param flowDiagramFormModel
     * @return
     */
    List<FlowDiagramFormModel>  queryNameAndId(FlowDiagramFormModel flowDiagramFormModel);
}
