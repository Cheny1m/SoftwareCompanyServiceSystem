package cn.edu.sicnu.cs.service.impl;


import cn.edu.sicnu.cs.dao.TaskMapper;
import cn.edu.sicnu.cs.dao.WorkordersMapper;
import cn.edu.sicnu.cs.model.Task;
import cn.edu.sicnu.cs.model.Workorders;
import cn.edu.sicnu.cs.service.EngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class EngineerServiceImpl implements EngineerService {


    @Resource
    TaskMapper taskMapper;

    @Resource
    WorkordersMapper workordersMapper;

    public long FindSelfTaskListByStatusCnt(long uid,String status){
        long total = taskMapper.findSelfTaskListByStatusCnt(uid,status);

        return total;
    }

    @Override
    public List<Map<String, Object>> FindAllSelfTaskList(long uid, long page, long pagenum) {
        page = (page - 1) * pagenum;
        return taskMapper.findAllSelfTaskList(uid,page,pagenum);
    }

    @Override
    public long FindAllSelfTaskListCnt(long uid) {
        return taskMapper.findAllSelfTaskListCnt(uid);
    }

    @Override
    public List<Map<String, Object>> FindAllSelfWorkOrderList(long uid, long page, long pagenum) {
        page = (page - 1) * pagenum;
        return workordersMapper.findAllSelfWorkOrderList(uid,page,pagenum);
    }

    @Override
    public long FindAllSelfWorkOrderCnt(long uid) {
        return workordersMapper.findAllSelfWorkOrderCnt(uid);
    }

    @Override
    public List<Task> FindAllTasks() {
        return taskMapper.selectAllTasks();
    }

    @Override
    public long FindSelfTaskCntByStatus(long uid, String status) {
        long cnt = taskMapper.findSelfTaskCntByStatus(uid,status);
        return cnt;
    }

    @Override
    public long FindSelfWorkOrderCntByStatus(long uid, String status) {
        long selfWorkOrderCntByStatus = workordersMapper.findSelfWorkOrderCntByStatus(uid, status);
        long cnt = selfWorkOrderCntByStatus;
        return cnt;
    }

    @Override
    public List<Map<String,Object>> FindSelfTaskListByStatus(long uid, long page, long pagenum, String status) {
        page =((page-1) * pagenum);
        List<Map<String,Object>> list = taskMapper.findSelfTaskListByStatus(uid,page,pagenum,status);

        return list;
    }



    @Override
    public List<Map<String,Object>> FindSelfWorkOrderListByStatus(long uid, long page, long pagenum,String status) {
        page =((page-1) * pagenum);
        List<Map<String,Object>> list = workordersMapper.findSelfWorkOrderListByStatus(uid,page,pagenum,status);
        return list;
    }

    //

    @Override
    public List<Task> FindWorkOrderListByStatus(String status) {
        List<Task> list = taskMapper.findWorkOrderListByStatus(status);
        return list;
    }



    @Autowired
    WorkOrdersServiceImpl workOrdersServiceImpl;

    @Override
    public Workorders FindWorkOrderByWid(long wid) {
        Workorders workorders = workOrdersServiceImpl.FindWorkOrder(wid);
        return workorders;
    }

//    @Override
//    public long FindSelfWorkOrderListByTypeCnt(long uid, String status) {
//        long total = workOrdersServiceImpl.FindSelfWorkOrderListByTypeCnt(uid,status);
//        return total;
//    }


}
