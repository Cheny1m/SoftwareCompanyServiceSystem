package cn.edu.sicnu.cs.service.impl;


import cn.edu.sicnu.cs.dao.UserMapper;
import cn.edu.sicnu.cs.dao.WorkordersMapper;
import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.model.UserExample;
import cn.edu.sicnu.cs.model.Workorders;
import cn.edu.sicnu.cs.model.WorkordersExample;
import cn.edu.sicnu.cs.pojo.WorkOrderPojo;
import cn.edu.sicnu.cs.service.WorkOrderManagerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkOrderManagerServiceimpl implements WorkOrderManagerService {

    @Resource
    WorkordersMapper workordersMapper;

    @Resource
    UserMapper userMapper;

    @Override
    public List<WorkOrderPojo> FindWorkOrderByFzid(long fzid,int pagenum,int pagesize) {
        pagenum = (pagenum-1)*pagesize;
        List<WorkOrderPojo> workOrderPojos = workordersMapper.selectWorkOrderByFzid(fzid,pagenum,pagesize);
        return workOrderPojos;
    }

    @Override
    public int CountByStatus(String status) {
        return workordersMapper.CountByStatus(status);
    }

    @Override
    public int CountAllOrders() {
        return workordersMapper.CountAllOrders();
    }

    @Override
    public List<WorkOrderPojo> findAllWorkorders(Integer pagenum,Integer pagesize) {
        List<WorkOrderPojo> workOrderPojos;
        pagenum =((pagenum-1) * pagesize);
        workOrderPojos = workordersMapper.findAllWorkorders(pagenum,pagesize);
        return workOrderPojos;
    }

    @Override
    public List<WorkOrderPojo> selectWorkordersByStatus(String status,Integer page,Integer pagenum) {
        page =((page-1) * pagenum);
        List<WorkOrderPojo> workOrderPojos = workordersMapper.selectByStatus(status,page,pagenum);
        return workOrderPojos;
    }

    @Override
    public String checkorder(Long wid,String check) {
        workordersMapper.checkUpdateStatusByWid(wid,check);
        return check;
    }

    @Override
    public Long allocateorder(Long wid, String name) {
        workordersMapper.allocateUpdateStatusByWid(wid,name);
        return wid;
    }

    @Override
    public void finishorder(Long wid) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        Date day = new Date();
        day.equals(today);

        workordersMapper.finishUpdateStatusByWid(wid,day);
    }

    @Override
    public List<Map<String, Object>> SearchOrder(String wname, long uid) {
        return workordersMapper.SearchOrder(wname,uid);
    }

    @Override
    public void Closerder(Long wid) {
        WorkordersExample workordersExample = new WorkordersExample();
        workordersExample.createCriteria().andWidEqualTo(wid);
        workordersMapper.deleteByExample(workordersExample);
    }

    @Override
    public List<String> getFzname() {
        List<String> fzname = new ArrayList<>();
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUroleIdEqualTo(16);
        List<User> users = userMapper.selectByExample(userExample);
        for (int i = 0;i < users.size();i++)
        {
            fzname.add(users.get(i).getUrealname());
        }
        return fzname;
    }

}
