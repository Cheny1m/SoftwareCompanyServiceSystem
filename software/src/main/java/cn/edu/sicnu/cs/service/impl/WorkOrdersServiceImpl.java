package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.dao.WorkordersMapper;
import cn.edu.sicnu.cs.model.Workorders;
import cn.edu.sicnu.cs.model.WorkordersExample;
import cn.edu.sicnu.cs.service.WorkOrdersService;
import cn.edu.sicnu.cs.utils.TimeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkOrdersServiceImpl implements WorkOrdersService {

    @Resource
    WorkordersMapper workordersMapper;

    @Override
    public void InsertWorkOrder(Workorders workOrders) {
        workordersMapper.insert(workOrders);
    }

    @Override
    public List<Map<String,Object>> FindSelfWorkOrderSubmit(long cid,long page, long pagenum) {

        page = ((page-1)*pagenum);

        List<Map<String,Object>> list = workordersMapper.findSelfWorkOrderSubmit(cid,page,pagenum);
        return list;
    }

    @Override
    public Workorders FindWorkOrder(long wid) {
        Workorders workorders = workordersMapper.findWorkOrder(wid);
        return workorders;
    }

    @Override
    public long FindSelfWorkOrderSubmitCnt(long cid) {
        return workordersMapper.findSelfWorkOrderSubmitCnt(cid);
    }

    @Override
    public long FindKefuCommitNumToday(Integer cid){
        WorkordersExample workordersExample = new WorkordersExample();
        List<Timestamp> today = TimeUtils.today();
        workordersExample.createCriteria().andWuserIdEqualTo(cid)

        .andWcreattimeBetween(new Date(today.get(0).getTime()),new Date(today.get(1).getTime()));
        List<Workorders> workorders = workordersMapper.selectByExample(workordersExample);
        return workorders.size();
    }


    @Override
    public long FindSelfWorkOrderListByTypeCnt(long uid, String status) {
        return workordersMapper.findSelfWorkOrderListByTypeCnt(uid,status);
    }

    @Override
    public List<Map<String, Object>> SearchWorkorder(String wname,long cid) {
        String name = "%" + wname + "%";
        System.out.println(name);
        return  workordersMapper.searchWorkorder(name,cid);
    }
}
