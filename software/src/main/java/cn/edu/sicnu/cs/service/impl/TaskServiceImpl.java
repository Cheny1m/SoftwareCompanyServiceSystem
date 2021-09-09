package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.dao.TaskMapper;
import cn.edu.sicnu.cs.model.Task;
import cn.edu.sicnu.cs.service.TaskService;
import cn.edu.sicnu.cs.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    TaskMapper taskMapper;

    @Autowired
    IdWorker idWorker;

    @Override
    public void AddTask(List<Task> tasks) {
        for (int i = 0;i < tasks.size();i++){
            Task task = tasks.get(i);
            task.setTid(idWorker.nextId());
            taskMapper.insert(task);
        }
    }

    @Override
    public void FinishTask(long tid) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        Date day = new Date();
        day.equals(today);

        taskMapper.finishTask(tid,day);
    }

    @Override
    public void UpdateTask(Task task) {

    }

    @Override
    public void BeginTaskByTid(long tid,double tleft) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        Date day = new Date();
        day.equals(today);
        taskMapper.beginTaskByTid(tid,tleft,day);

    }

    @Override
    public void WriteRemark(String s, long id, String recontent) {

    }

    @Override
    public void UpdateTaskprogress(long tid, long consumed) {
        taskMapper.updateTaskprogress(tid,consumed);
    }

    @Override
    public long StatisticsYouthCnt(long uid) {
        return 0;
    }

    @Override
    public long StatisticsTotalCnt(long uid) {
        return 0;
    }

}
