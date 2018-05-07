package cn.huimin.process.core.rule;

import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;


import java.util.List;

/**
 * Created by wyp on 2017/5/2.
 */
public interface UserRule {
    /**
     * 获取人员的规则
     * @return
     */
    List<String> getUsersId(PvmActivity pvmActivity, ActivityExecution execution);



}
