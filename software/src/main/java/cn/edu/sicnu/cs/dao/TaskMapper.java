package cn.edu.sicnu.cs.dao;

import cn.edu.sicnu.cs.model.Task;
import cn.edu.sicnu.cs.model.TaskExample;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TaskMapper {

    List<Task> selectAllTasks();

    long countByExample(TaskExample example);

    int deleteByExample(TaskExample example);

    int deleteByPrimaryKey(Long tzid);

    int insert(Task record);

    int insertSelective(Task record);

    List<Task> selectByExampleWithBLOBs(TaskExample example);

    List<Task> selectByExample(TaskExample example);

    Task selectByPrimaryKey(Long tzid);

    int updateByExampleSelective(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByExampleWithBLOBs(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByExample(@Param("record") Task record, @Param("example") TaskExample example);

    int updateByPrimaryKeySelective(Task record);

    int updateByPrimaryKeyWithBLOBs(Task record);

    int updateByPrimaryKey(Task record);

    long findSelfTaskCntByStatus(long uid, String status);



    List<Map<String,Object>> findSelfTaskListByStatus(long uid, long page, long pagenum, String status);


    List<Task> findWorkOrderListByStatus(String status);

    long findSelfTaskListByStatusCnt(long uid, String status);


    List<Map<String, Object>> findAllSelfTaskList(long uid, long page, long pagenum);

    long findAllSelfTaskListCnt(long uid);

    void beginTaskByTid(long tid,double tleft,Date day);

    void finishTask(long tid,Date day);

    void updateTaskprogress(long tid, long consumed);
}