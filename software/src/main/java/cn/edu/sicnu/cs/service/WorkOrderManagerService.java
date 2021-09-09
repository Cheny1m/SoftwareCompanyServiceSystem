package cn.edu.sicnu.cs.service;

import cn.edu.sicnu.cs.model.Workorders;
import cn.edu.sicnu.cs.pojo.WorkOrderPojo;

import java.util.List;
import java.util.Map;

public interface WorkOrderManagerService {

    List<WorkOrderPojo> FindWorkOrderByFzid(long fzid,int pagenum,int pagesize);

    int CountByStatus(String status);

    int CountAllOrders();

    List<WorkOrderPojo> findAllWorkorders(Integer page,Integer pagenum);

    List<WorkOrderPojo> selectWorkordersByStatus(String status,Integer page,Integer pagenum);

    String checkorder(Long woid,String check);

    Long allocateorder(Long woid,String name);

    void finishorder(Long wid);

    List<Map<String, Object>> SearchOrder(String wname, long uid);

    void Closerder(Long wid);

    List<String> getFzname();
}
