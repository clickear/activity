package cn.huimin.process.core;
/**
 * 防止混淆，所有可以使用的变量放置在这里
 *
 */
public interface HmXMLConstants {

	public static final String NEMESPACE_PREFIX = "shareniu";

	public static final String NEMESPACE= "http://www.shareniu.com/";

	//发起人的候选人
	public static final String  CANDIDATE_STARTER_USERS= "candidatestarterusers";
	public static final String ADMINS = "admins";
	public static final String ID = "id";

	//发起人的候选组
	public static final String  CANDIDATE_STARTER_GROUP= "candidatestartergroups";





	public static final String TASK_JSON_MAP_KEY= "taskJsonMapKey";
	//扩展属性的key
	public static final String EXTENSIONATTRIBUTE_KEY = "shareniuExt";

	//流程类别
	public static final String PROPERTY_PROCESS_TYPE = "processtype";



	//获取具体岗位
	public static final String PROPERTY_USERTASK_DEPNAME = "departmentname";
	//相对规则
	public static final String PROPERTY_USERTASK_RULE = "specialcheckperson";

	//岗位设置
	public static final String PROPERTY_ROLE_SET = "roleset";
	//部门设置
	public static  final String PROPERTY_DEPARTMENT_SET = "departmentset";

	//部门类别设置
	public static final String  DEPARTMENT_TYPE_SET ="departmenttypeset";


	//任务类型
	public static final String PROPERTY_USERTASK_TYPE = "prioritydefinition";
	//任务变量
	public static final String TASK__WITH_VAR = "taskwithvar";


	//设置任务节点绑定的表单属性字段
	public static final String  FORM_WITH_VAR = "formwithvar";
	//绘制时节点id
	public static final String  ACTIVITY_ID = "overrideid";
	//formkeydefinition表单定义key设置为特殊的节点
	public static final String  FORM_KEY_DEFINITION = "formkeydefinition";
	//duedateset设置节点处理期限 默认是12个小时
	public static  final  String DUE_DATE_SET = "duedateset";

	//节点归属
	public static  final  String NODE_BELONG= "nodebelong";





	//流程实例相关
	public static final String PROPERTY_USERTASK_ASSIGN = "setcheckperson";
	//multiinstance_cardinality 设置为多实例的长度
	public static final String PROPERTY_MULTIINSTANCE_CARDINALITY = "multiinstance_cardinality";
	//如果单个人的话就没必要的
	public static final String  PROPERTY_MULIINSTANCE_CONDITION = "multiinstance_condition";
	//multiinstance_type实例类型
	public static final String PROPERTY_MULIINSTANCE_TYPE = "multiinstance_type";
	//多实例类型
	//并行
	public static final String  MULIINSTANCE_PARALLEL = "Parallel";
	//串行
	public static final String  MULIINSTANCE_SEQUENTIAL = "Sequential";

	//节点绑定业务
	public static final String  BUSINESS_STATE = "businessstate";


}
