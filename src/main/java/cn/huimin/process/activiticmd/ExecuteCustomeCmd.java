package cn.huimin.process.activiticmd;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;

/**
 * Created by wyp on 2017/5/5.
 */
public class ExecuteCustomeCmd {

    private ExecuteCustomeCmd(){

    }

    /**
     * 执行自定义命令器
     * @param runtimeService
     * @param command
     */
    public static void execute(RuntimeService runtimeService,Command<Void> command){
        RuntimeServiceImpl runtimeServiceImpl = (RuntimeServiceImpl) runtimeService ;
        runtimeServiceImpl.getCommandExecutor().execute(command);

    }
}
