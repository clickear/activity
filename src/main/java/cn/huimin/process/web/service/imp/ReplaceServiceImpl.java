package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dao.ReplaceDao;
import cn.huimin.process.web.dto.Page;
import cn.huimin.process.web.model.Replace;
import cn.huimin.process.web.service.ReplaceService;
import cn.huimin.process.web.util.EhrUserDataHandleUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */
@Service
public class ReplaceServiceImpl implements ReplaceService {

    @Autowired
    private ReplaceDao replaceDao;

    @Autowired
    private RepositoryService repositoryService;

    @Value("${getRoleByUserId}")
    private String getRoleByUserId;


    @Override
    @Transactional
    public void insert(Replace replace) {
        //这里添加多个的
         String[] processKeys = replace.getProcessKey().split("\\|");
        for(int i=0;i<processKeys.length;i++){
            replace.setProcessKey(processKeys[i]);
            replaceDao.insert(replace);
        }

    }

    @Override
    @Transactional
    public void deleteById(List<String> id) {
        replaceDao.deleteById(id);
    }

    /**
     * 更新同意的状态
     * @param agree
     * @param id
     */
    @Transactional
    public void updateAgree( Integer agree, Integer id){
        replaceDao.updateAgree(agree,id);
    }





    @Override
    public List<Replace> query(Replace replace) {
        return replaceDao.query(replace);
    }
    //分页
    @Override
    public Page queryPage(Replace replace,Integer start,Integer max) {
        Page page = new Page();
        PageHelper.startPage(start,max);
        List<Replace> list = replaceDao.query(replace);
        //查询所有的
        PageInfo<Replace> pageInfo = new PageInfo<Replace>(list);
        page.setRows(list);
        for(Replace replace1 :list){
            replace1.setChangePerson(EhrUserDataHandleUtils.employeeInfo(replace1.getChangePerson(),getRoleByUserId));
            replace1.setCheckPerson( EhrUserDataHandleUtils.employeeInfo(replace1.getCheckPerson(),getRoleByUserId));
           ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(replace1.getProcessKey()).latestVersion().active().singleResult();
           replace1.setProcessKey(processDefinition.getName());
        }


        page.setTotal(pageInfo.getTotal());
        page.setPage(start);
        return page;
    }

}
