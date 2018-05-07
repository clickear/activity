package cn.huimin.process.web.util;

/**
 * Created by Administrator on 2016-7-20.
 */
public class Constants {
    public static final String adminid="adminid";
    public static final String detailUrl = "detailUrl";
    public static final String processType = "processType";
    public static final String callbackUrl = "callbackUrl";
    public static final String maillist="maillist";
    public static final String START_PERSON_ID="START_PERSON_ID";//发起人的id

    /**Web JSON response 标准参数**/
    public static final String succss = "success";  //处理结果：true/false
    public static final String message = "message"; //错误信息

    /**其他标准参数名称定义**/
    public static final String processId="processId"; //流程ID
    public static final String needCEOreview = "needCEOreview";
    public static final  String needCenterManager = "needCenterManager";

    /**特殊流程Key**/
    public static final String dpt_task="dpt_task"; //部门内任务(前缀)
    public static final String dpt_center_task="dpt_center_task"; //归口中心任务(前缀)
    public  static  final  String start_person = "start_person";//知会发起人任务


    public static  final  String form1 = "form1";


    /**部门任务相关变量名**/
    public static final String branchid="branchid";
    public static final String departmentid="departmentid";
    public static final String departmentname="departmentname";
    public static final String roleid="roleid";
    public static final String level="level";
    public static final String hasInited = "hasInited";
    public static final String hasParentDpt = "hasParentDpt";
    public static final String chargLeaderInRole = "chargLeaderInRole";
    public static final String hasSkipToParent = "hasSkipToParent";
    //是否是负责人岗
    public static final String isLeadrole = "isLeadrole";



    /**流程处理结果参数**/

    public static final String result="result";
    //操作事情
    public static final String doWhat ="doWhat";
    //最终的处理结果
    public static  final String lastResult = "lastResult";
    //每次审批的备注
    public static final String  remark ="remark";
    //获取的次数
    public static final String  count ="count";
    //是否是回退的任务
    public static final  String isBacked="isBacked";
    //是否是委托的任务
    public static final String isdelegation = "isdelegation";
    //委托任务的查办情况
    public static  final  String delegationTask = "delegationTask";
    /**请假流程特殊变量**/
    public static final String leaveTime = "x";
    public static final String prefix_attendance = "attendance";
    public static final String attendance_dpt_task="attendance_dpt_task";

    //登录用户信息的key
    public static final  String userInfo ="userInfo";
    //登录用户的所有岗位
    public static final String userInfos ="userInfos";

    public static final String menus ="rmsMenus";






}
