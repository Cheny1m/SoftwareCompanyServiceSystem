package cn.edu.sicnu.cs.dao;

import cn.edu.sicnu.cs.model.Workorders;
import cn.edu.sicnu.cs.model.WorkordersExample;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.edu.sicnu.cs.pojo.WorkOrderPojo;
import org.apache.ibatis.annotations.Param;

public interface WorkordersMapper {

    List<WorkOrderPojo> selectWorkOrderByFzid(long fzid,int pagenum,int pagesize);

    List<WorkOrderPojo> selectByStatus(String status,Integer page,Integer pagenum);

    List<WorkOrderPojo> findAllWorkorders(Integer pagenum,Integer pagesize);

    void checkUpdateStatusByWid(Long wid,String check);

    void allocateUpdateStatusByWid(Long wid,String name);

    void finishUpdateStatusByWid(Long wid, Date day);

    long countByExample(WorkordersExample example);

    int deleteByExample(WorkordersExample example);

    int deleteByPrimaryKey(Long wzid);

    int insert(Workorders record);

    int insertSelective(Workorders record);

    List<Workorders> selectByExampleWithBLOBs(WorkordersExample example);

    List<Workorders> selectByExample(WorkordersExample example);

    Workorders selectByPrimaryKey(Long wzid);

    int updateByExampleSelective(@Param("record") Workorders record, @Param("example") WorkordersExample example);

    int updateByExampleWithBLOBs(@Param("record") Workorders record, @Param("example") WorkordersExample example);

    int updateByExample(@Param("record") Workorders record, @Param("example") WorkordersExample example);

    int updateByPrimaryKeySelective(Workorders record);

    int updateByPrimaryKeyWithBLOBs(Workorders record);

    int updateByPrimaryKey(Workorders record);

    long findSelfWorkOrderCntByStatus(long uid, String status);

    List<Map<String,Object>> findSelfWorkOrderListByStatus(long uid, long page, long pagenum, String status);

    List<Map<String,Object>> findSelfWorkOrderSubmit(long cid,long page, long pagenum);

    Workorders findWorkOrder(long wid);


    long findSelfWorkOrderSubmitCnt(long cid);

    long findSelfTaskListByStatus(long uid, String status);

    long findSelfWorkOrderListByTypeCnt(long uid, String status);


    int CountAllOrders();

    int CountByStatus(String status);


    long findAllSelfWorkOrderCnt(long uid);

    List<Map<String, Object>> findAllSelfWorkOrderList(long uid, long page, long pagenum);

    List<Map<String, Object>> searchWorkorder(String wname,long cid);

    List<Map<String, Object>> SearchOrder(String wname, long uid);
}