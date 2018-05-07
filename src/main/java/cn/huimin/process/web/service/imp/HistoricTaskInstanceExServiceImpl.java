package cn.huimin.process.web.service.imp;

import cn.huimin.process.web.dao.HistoricTaskInstanceExDao;
import cn.huimin.process.web.dto.HandlerLog;
import cn.huimin.process.web.dto.HistoricTaskInstanceType;
import cn.huimin.process.web.dto.NodeHandleType;
import cn.huimin.process.web.model.ActCreationEx;
import cn.huimin.process.web.model.HistoricTaskInstanceEx;
import cn.huimin.process.web.service.ActCreationServiceEx;
import cn.huimin.process.web.service.HistoricTaskInstanceExService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wyp on 2017/4/10.
 */
@Component
public class HistoricTaskInstanceExServiceImpl implements HistoricTaskInstanceExService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoricTaskInstanceExDao historicTaskInstanceExDao;

    @Autowired
    private ActCreationServiceEx actCreationServiceEx;

    @Autowired
    private TaskService taskService;

    /**
     * 通过流程实例id查询所有已经完成的节点信息
     * 查询流程正常运行的
     *
     * @param historicTaskInstanceEx
     * @return
     */
    @Override
    public HistoricTaskInstanceType queryHistoricTaskByProcessInstanceId(HistoricTaskInstanceEx historicTaskInstanceEx) {
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceId(historicTaskInstanceEx.getProcessInstanceId()).list();
        HistoricTaskInstanceType historicTaskInstanceType = new HistoricTaskInstanceType();
        //true代表完成
        boolean isStop;
        if (processInstanceList != null && processInstanceList.size() > 0) {
            isStop = false;
        } else {
            isStop = true;
        }
        List<HistoricTaskInstanceEx> list = historicTaskInstanceExDao.queryHistoricTaskByProcessInstanceId(historicTaskInstanceEx);
        Iterator<HistoricTaskInstanceEx> iterator = list.iterator();
        while (iterator.hasNext()) {
            HistoricTaskInstanceEx historicTaskInstanceEx1 = iterator.next();
            if (historicTaskInstanceEx1.getDeleteReason() != null) {
                HandlerLog handlerLog = JSON.parseObject(historicTaskInstanceEx1.getDeleteReason(), HandlerLog.class);
                String logType = handlerLog.getType();
                switch (logType) {
                    //添加
                    case NodeHandleType.NODE_NO_PASS:
                        isStop = true;
                        break;
                    case NodeHandleType.NODE_SUBMIT:
                        iterator.remove();
                        break;
                    case NodeHandleType.ADD_NODE_DELETE:
                        iterator.remove();
                        break;
                    case NodeHandleType.NODE_BACK:
                        iterator.remove();
                        break;
                    case NodeHandleType.PROCESS_DELETE:
                        isStop = true;
                        break;
                    case NodeHandleType.NODE_REPEART:
                        break;
                    case NodeHandleType.NODE_PASS:
                        break;
                    case NodeHandleType.NODE_PARALLEL_DELETE:
                        iterator.remove();
                        break;
                    default:
                        break;
                }
            }
        }
        //list处理完成
        if (isStop) {
            historicTaskInstanceType.setType(1);
        } else {
            //未完成需要查询数据库另一张临时表格
            historicTaskInstanceType.setType(0);
            ActCreationEx actCreationEx = new ActCreationEx();
            actCreationEx.setState(0);
            actCreationEx.setProcessInstanceId(historicTaskInstanceEx.getProcessInstanceId());
           //查询所有的加过的节点
            List<ActCreationEx> list1 = actCreationServiceEx.query(actCreationEx);
            //如果查询list1为0的话后面就不用了
            ActCreationEx ss=this.queryTempNodeId(list1,list.get(list.size()-1).getTaskKey());
            //查询
            if(ss==null){
                historicTaskInstanceType.setCurrentId(list.get(list.size()-1).getTaskKey());
            }
            else {
                historicTaskInstanceType.setCurrentId(ss.getActId());
               this.handelTempAddActivity(list1,list);
            }
           List<String> list2 = this.queryHignNodeIds(historicTaskInstanceEx.getProcessInstanceId());
            historicTaskInstanceType.setHignNodeIds(list2);
        }
        historicTaskInstanceType.setHistoricTaskInstanceEx(list);
        return historicTaskInstanceType;
    }


    /**
     * 处理临时添加的未完成的节点
     * @param list
     * @return
     */
    private List<HistoricTaskInstanceEx> handelTempAddActivity(List<ActCreationEx> list1,List<HistoricTaskInstanceEx> list){
        if (list1 != null && list1.size() > 0) {
            for (ActCreationEx actCreationEx1 : list1) {
                String processText = actCreationEx1.getProcessText();
                JSONArray jsonArray = JSON.parseObject(processText).getJSONArray("cloneActivityIds");
                JSONArray jsonArray1 =JSON.parseObject(processText).getJSONArray("activityNames");
                for (int i = 0; i < jsonArray.size(); i++) {
                    HistoricTaskInstanceEx historicTaskInstanceEx1 = new HistoricTaskInstanceEx();
                    historicTaskInstanceEx1.setTaskKey(jsonArray.getString(i));
                    historicTaskInstanceEx1.setName(jsonArray1.getString(i));
                    if (!list.contains(historicTaskInstanceEx1)) {
                        //没有就添加
                        list.add(historicTaskInstanceEx1);
                    }
                }
            }

        }
        return list;
    }


    private List<String> queryHignNodeIds(String processInstanceId){
       List<Task> list =  taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        List<String> list1 = new ArrayList<>();
        for(Task task : list){
            list1.add(task.getTaskDefinitionKey());
        }
        return list1;
    }


    /**
     * 查询当前yu运行的节点是否在添加的临时节点中
     * @param list
   * @return
     */
    private ActCreationEx queryTempNodeId(List<ActCreationEx> list,String activeActId){
        //找到那个临时节点之后
        ActCreationEx creationEx = null;
        for(ActCreationEx actCreationEx :list){
            String processText = actCreationEx.getProcessText();
            JSONArray jsonArray =JSON.parseObject(processText).getJSONArray("cloneActivityIds");
            for(int i= 0 ;i<jsonArray.size();i++){
                String nodes =jsonArray.getString(i);
                if(nodes.equals(activeActId)){
                    //相等代表这个之前的肯定执行过了
                    creationEx = actCreationEx;
                    break;
                }
            }
        }
        return creationEx;
    }








}
