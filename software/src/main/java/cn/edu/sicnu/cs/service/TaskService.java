package cn.edu.sicnu.cs.service;

import cn.edu.sicnu.cs.model.Task;

import java.util.Date;
import java.util.List;

public interface TaskService {

    void AddTask(List<Task> tasks) throws Exception;

    void FinishTask(long tid);

    void UpdateTask(Task task);

    void BeginTaskByTid(long tid,double left);

    void WriteRemark(String s, long id, String recontent);

    void UpdateTaskprogress(long id, long consumed);

    long StatisticsYouthCnt(long uid);

    long StatisticsTotalCnt(long uid);
}
