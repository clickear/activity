
package cn.huimin.process.web.service;

import cn.huimin.process.web.dto.SimpleResult;
import java.util.List;
import java.util.Map;

/**
 * 回退
 */
public interface HandleProcessService {

    /**
     * 获取已经完成的活动节点
     * @param processId
     */
    Map<String,Object> getCanGoBackActivtiys(String processId);

    /**
     * 回退到任意节点
     * @param currentTaskId
     * @param backToTaskId
     * @param doUser 处理人的id
     * @param processId
     * @param userId 回退的人的id
     * @param remark 意见
     * @return
     */
     SimpleResult dbBackTo(String currentTaskId, List<String> backToTaskId, String doUser, String processId,String userId,String doWhat,String remark);


    /**
     * 管理员执行流程终止
     * @param processId
     */
     void delete(String processId);



    /**
     * 签收功能
     * @param taskId
     * @param userId
     */
    void activityClam(String taskId,String userId);

    /**
     * 流程作废
     *
     * @param processId
     * @param taskId
     * @param userId    谁操作的
     * @param remark    作废原因
     * @param noProblem
     * @return
     */
     SimpleResult processDelete(String processId, String taskId, String userId, String remark, String noProblem);
}
