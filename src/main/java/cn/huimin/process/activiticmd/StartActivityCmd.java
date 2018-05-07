package cn.huimin.process.activiticmd;

import cn.huimin.process.core.InnerActivitiVarConstants;
import cn.huimin.process.createactivity.DefaultTaskFlowControlService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.runtime.AtomicOperation;
import org.apache.log4j.Logger;

public class StartActivityCmd implements Command<Void>
{
	private ActivityImpl _activity;

	private String _executionId;
	//启动类型
	private  int  _type;

	public final static String NODE_IS_PICK = "nodeIsPick";

	public final static String NODE_HANDLE = "nodeHandle";

	private String _userId;
	//审核意见保留
	private String _checkInfo;

	public StartActivityCmd(String executionId,int type, ActivityImpl activity,String userId){
		_activity = activity;
		_executionId = executionId;
		_type = type;
		_userId = userId;
	}

	public StartActivityCmd(String executionId, ActivityImpl activity)
	{
		_activity = activity;
		_executionId = executionId;
	}
	public StartActivityCmd(String executionId, ActivityImpl activity,String checkInfo)
	{
		_activity = activity;
		_executionId = executionId;
		_checkInfo = checkInfo;
	}
	@Override
	public Void execute(CommandContext commandContext)
	{
		//创建新任务
		Logger.getLogger(DefaultTaskFlowControlService.class).debug(
			String.format("executing activity: %s", _activity.getId()));

		ExecutionEntity execution = commandContext.getExecutionEntityManager().findExecutionById(_executionId);
		execution.setActivity(_activity);
		if(_checkInfo!=null){
			execution.setVariable(InnerActivitiVarConstants.HM_ACITVITI_CHECK_ADVICE_INFO,_checkInfo);
		}
		if(_type ==1){
			//放入变量
			execution.setVariable(NODE_IS_PICK,true);
			execution.setVariable(NODE_HANDLE,_userId);
		}
		execution.performOperation(AtomicOperation.ACTIVITY_START);
		return null;
	}  
}