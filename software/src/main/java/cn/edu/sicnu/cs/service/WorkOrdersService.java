package cn.edu.sicnu.cs.service;

import cn.edu.sicnu.cs.model.Workorders;
import cn.edu.sicnu.cs.pojo.WorkOrderPojo;

import java.util.List;
import java.util.Map;

public interface WorkOrdersService {
    void InsertWorkOrder(Workorders workOrders);

    List<Map<String,Object>> FindSelfWorkOrderSubmit(long cid, long page, long pagenum);

    Workorders FindWorkOrder(long wid);

    long FindSelfWorkOrderSubmitCnt(long cid);

    /**
     * 查询客服今天的提交表单数
     * @param cid
     * @return
     */
    long FindKefuCommitNumToday(Integer cid);

    long FindSelfWorkOrderListByTypeCnt(long uid, String status);

    List<Map<String, Object>> SearchWorkorder(String wname,long cid);
}
