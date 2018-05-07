package cn.huimin.process.common.logger;

public class TaskException extends BaseLoggerException {

	/**
	* 
	*/
	private static final long serialVersionUID = 7101602721443794174L;
	public static TaskException TASK_ID_HAS_EXISTED = new TaskException("taskId has existed.");

	public TaskException(String defineCode) {
		super(defineCode);
	}

/*	public static void main(String[] args)  {
		throw TaskException.newException(TaskException.TASK_ID_HAS_EXISTED, "Task Id is not existed.num {0}",
				new Object[] { 3 });
	}*/

}
