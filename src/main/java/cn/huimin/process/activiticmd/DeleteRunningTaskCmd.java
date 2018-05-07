package cn.huimin.process.activiticmd;

import cn.huimin.process.createactivity.DefaultTaskFlowControlService;
import cn.huimin.process.web.dto.HandlerLog;
import cn.huimin.process.web.dto.NodeHandleType;
import cn.huimin.process.web.util.DateUtils;
import cn.huimin.process.web.util.ObjectCheckUtils;
import com.alibaba.fastjson.JSON;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.log4j.Logger;
  
public class DeleteRunningTaskCmd implements Command<Void>
{
	private TaskEntity _currentTaskEntity;
	private String  deleteReson;
	public DeleteRunningTaskCmd(TaskEntity currentTaskEntity)
	{
		_currentTaskEntity = currentTaskEntity;
	}
	public DeleteRunningTaskCmd(TaskEntity currentTaskEntity,String deleteReson)
	{
		_currentTaskEntity = currentTaskEntity;
		this.deleteReson = deleteReson;
	}
	@Override
	public Void execute(CommandContext commandContext)
	{
		//删除当前的任务
		//不能删除当前正在执行的任务，所以要先清除掉关联
		if (_currentTaskEntity != null)
		{
			Logger.getLogger(DefaultTaskFlowControlService.class).debug(
				String.format("deleting task: %s [id=%s]", _currentTaskEntity.getName(), _currentTaskEntity.getId()));
			HandlerLog handlerLog = new HandlerLog();
			String assigner =	_currentTaskEntity.getAssignee();
			if(!ObjectCheckUtils.isEmptyString(assigner)){
				//如果没人的话删除 则是初始化的任务
				handlerLog.setHandleUser(assigner);
			}
			handlerLog.setHandleTime(DateUtils.now());
			handlerLog.setType(NodeHandleType.ADD_NODE_DELETE);
			Context.getCommandContext().getTaskEntityManager()
					.deleteTask(_currentTaskEntity, JSON.toJSONString(handlerLog), false);
			//删除历史
			if(deleteReson!=null){
				Context.getCommandContext().getTaskEntityManager()
						.deleteTask(_currentTaskEntity, deleteReson, false);

			}
		}

		return null;
	}
}