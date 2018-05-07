package cn.huimin.process.core;

import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class HmUserTaskParseHandler extends UserTaskParseHandler {
	protected void executeParse(BpmnParse bpmnParse, UserTask userTask) {
		super.executeParse(bpmnParse, userTask);
		Map<String, List<ExtensionAttribute>> attributes = userTask.getAttributes();
		ActivityImpl activityImpl = findActivity(bpmnParse, userTask.getId());
		activityImpl.setProperty("shareniuExt", attributes);
	}
}
