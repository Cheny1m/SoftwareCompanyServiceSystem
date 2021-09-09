package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.model.Task;
import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.model.Workorders;
import cn.edu.sicnu.cs.service.EngineerService;
import cn.edu.sicnu.cs.service.FeedBackService;
import cn.edu.sicnu.cs.service.TaskService;
import cn.edu.sicnu.cs.service.UserService;

import cn.edu.sicnu.cs.utils.ResUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/engineer/")
@Api(tags = "engineer",value = "工程师")
public class EngineerCotroller {


    @Autowired
    EngineerService engineerService;

    @Autowired
    FeedBackService feedBackService;

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    @GetMapping("/getalltasks")
    @ApiOperation(value = "GetAllTasks",notes = "获取所有任务")
    @NewLog(value = "获取所有任务")
    public String GetAllTasks(){
        try {
            return ResUtil.getJsonStr(1,"成功",engineerService.FindAllTasks());
        }catch (Exception e){
            return ResUtil.getJsonStr(-1,"失败");
        }
    }


    @GetMapping("task/cnt")
    @ApiOperation(value = "FindSelfTaskCnt",notes = "查询自己的任务数量")
    @NewLog(value="查询自己的任务数量")
    public String FindSelfTaskCnt(long uid){

        Map<String,Object> map = new HashMap<>();

        User user = userService.selectUserByUid((int)uid);
        map.put("username",user.getUsername());
        map.put("untreated",engineerService.FindSelfTaskCntByStatus(uid,"0")+engineerService.FindSelfTaskCntByStatus(uid,"1"));
        map.put("finished",engineerService.FindSelfTaskCntByStatus(uid,"2"));
        map.put("wprocessing",engineerService.FindSelfWorkOrderCntByStatus(uid,"1"));

        return ResUtil.getJsonStr(1, "成功", map);

    }

    @ApiOperation(value = "FindSelfTaskList",notes = "查询根据状态自己的任务列表")
    @GetMapping("task/list")
    @NewLog(value="查询根据状态自己的任务列表")

    public String FindSelfTaskList(long uid,long page,long pagenum,String status){
        Map<String,Object> map = new HashMap<>();
        List<Map<String, Object>> list;
        long total;
        if(!status.equals("4")){
            list = engineerService.FindSelfTaskListByStatus(uid, page, pagenum, status);
            total = engineerService.FindSelfTaskListByStatusCnt(uid, status);
        }
        else{
            list = engineerService.FindAllSelfTaskList(uid, page, pagenum);
            total = engineerService.FindAllSelfTaskListCnt(uid);
        }
        map.put("list",list);
        map.put("total",total);
        return ResUtil.getJsonStr(1, "成功", map);
    }


    //统计未完善
    @ApiOperation(value = "StatisticsCnt",notes = "查询根据状态自己的任务数量")
    @GetMapping("statistics")
    @NewLog(value="查询根据状态自己的任务数量")
    public String StatisticsCnt(long uid){
        long youth =  taskService.StatisticsYouthCnt(uid);
        long week = taskService.StatisticsYouthCnt(uid);
        long total = taskService.StatisticsTotalCnt(uid);

        Map<String,Object> map = new HashMap<>();

        map.put("youth",youth);
        map.put("week",week);
        map.put("total",total);
        return ResUtil.getJsonStr(1, "成功", map);
    }
//    @ApiOperation(value = "FindSelfWorkOrderList",notes = "查询自己关联的工单列表")
//    @GetMapping("order/list")
//    public List<Task> FindSelfWorkOrderList(long uid){
//
//        List<Task> list =  engineerService.FindSelfTaskList(uid);
//        return list ;
//
//    }

//    @ApiOperation(value = "FindDoingWorkOrderList",notes = "根据状态查询工单列表")
//    @GetMapping("order/list")
//    public List<Task> FindDoingWorkOrderList(String status){
//        List<Task> list =  engineerService.FindWorkOrderListByStatus(status);
//        return list ;
//    }


    @ApiOperation(value = "FindSelfWorkOrderList",notes = "根据状态查询自己关联的工单列表")
    @GetMapping("self/order/list")
    @NewLog(value="根据状态查询自己关联的工单列表")
    public String FindSelfWorkOrderListByType(long uid,long page,long pagenum,String status){
        List<Map<String,Object>> list ;
        long total ;
        Map<String,Object> map = new HashMap<>();
        if(!status.equals("4")){
           list  =  engineerService.FindSelfWorkOrderListByStatus(uid,page,pagenum,status);
           total = engineerService.FindSelfWorkOrderCntByStatus(uid,status);
        }else{
            list  =  engineerService.FindAllSelfWorkOrderList(uid,page,pagenum);//没结果
            total = engineerService.FindAllSelfWorkOrderCnt(uid);
        }
        map.put("list",list);
        map.put("total",total);
        return ResUtil.getJsonStr(1, "成功",map);
    }

    @ApiOperation(value = "FindWorkOrderByWid",notes = "查看工单详情")
    @GetMapping("order")
    @NewLog(value="查看工单详情")
    public String FindWorkOrderByWid(long wid){
        Workorders workorders =  engineerService.FindWorkOrderByWid(wid);
        Map<String,Object> map = new HashMap<>();
        map.put("detail",workorders);
        return ResUtil.getJsonStr(1,"成功",map) ;
    }

//    @ApiOperation(value = "FindTaskList",notes = "查询自己的任务列表")
//    @GetMapping("task/list/{uid}")
//    public List<Task> FindTaskList(@PathVariable String uid){
//        List<Task> list =  engineerService.FindTaskList(uid);
//        return list ;
//    }

//    @ApiOperation(value = "FindTaskListByType",notes = "根据状态查询自己的任务列表")
//    @GetMapping("task/list/{uid}/{type}")
//    public List<Task> FindTaskListByType(@PathVariable String uid,@PathVariable String type){
//        List<Task> list =  engineerService.FindTaskListByType(uid,type);
//        return list ;
//    }

//    @ApiOperation(value = "FindTaskByTid",notes = "根据状态查询自己的任务列表")
//    @GetMapping("task/{uid}")
//    public Task FindTaskByTid(@PathVariable String uid){
//        Task task=  engineerService.FindTaskByTid(uid);
//        return task ;
//    }



    @ApiOperation(value = "BeginTaskByTid",notes = "开始任务")
    @GetMapping("task/begin")
    @ResponseBody
    @NewLog(value="开始任务")
    public String BeginTaskByTid(long tid,double tleft){



        taskService.BeginTaskByTid(tid,tleft);
        //taskService.WriteRemark();
        return ResUtil.getJsonStr(1, "成功");
    }

    //进度
    @ApiOperation(value = "UpdateProgress",notes = "更改进度")
    @PostMapping("task/update/progress")
    @ResponseBody
    @NewLog(value="更改进度")
    public String UpdateProgress(HttpServletRequest request) throws Exception {
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        if (StringUtils.hasText(body)){

            JSONObject jsonObject = JSON.parseObject(body);
            String tid = jsonObject.getString("tid");
            String consum = jsonObject.getString("tconsumed");
            long consumed = Long.parseLong(consum);
            long id = Long.parseLong(tid);
            taskService.UpdateTaskprogress(id,consumed);

            String recontent = jsonObject.getString("recontent");
            String restate = "1";
            feedBackService.WriteRemarks("2",id,restate,recontent);

//            long fid = 20201129;
//            userform.setFid(fid);
//            //还需要一个分配id的函数、
//
//            int userid = 123;
//            userform.setFassignedtoId(userid);
//            //还需要一个随机分配给客服的函数
//
//            //插入当天的时间
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String today = sdf.format(new Date());
//            Date day = new Date();
//            day.equals(today);
//            userform.setFcreatetime(day);

            //还需要判断很多字段的插入

            return ResUtil.getJsonStr(1, "成功");
        }else{
            return ResUtil.getJsonStr(0, "失败");
        }

    }



    @ApiOperation(value = "FinishTaskByTid",notes = "完成任务")
    @PostMapping("task/finish")
    @ResponseBody
    @NewLog(value="完成任务")
    public String FinishTaskByTid(HttpServletRequest request) throws Exception {

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        if (StringUtils.hasText(body)){

            JSONObject jsonObject = JSON.parseObject(body);
            String tid = jsonObject.getString("tid");
            long id = Long.parseLong(tid);

            taskService.FinishTask(id);

//            String restate = "2";
//            String recontent = jsonObject.getString("recontent");
//            feedBackService.WriteRemarks("2",id,restate,recontent);

//            long fid = 20201129;
//            userform.setFid(fid);
//            //还需要一个分配id的函数、
//
//            int userid = 123;
//            userform.setFassignedtoId(userid);
//            //还需要一个随机分配给客服的函数
//
//            //插入当天的时间
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String today = sdf.format(new Date());
//            Date day = new Date();
//            day.equals(today);
//            userform.setFcreatetime(day);

            //还需要判断很多字段的插入

            return ResUtil.getJsonStr(1, "成功");
        }else{
            return ResUtil.getJsonStr(0, "失败");
        }
    }

//    //将前端返回来的map里面的value封入实体类中
//    public <T>T getObject(Map<String,Object> map, Class<T> c) throws Exception {
//        T t = c.getDeclaredConstructor().newInstance();//创建一个一个类型为T对象t
//        //1.拆开map
//        Set<Map.Entry<String, Object>> entries = map.entrySet();
//        for (Map.Entry<String, Object> entry : entries) {//获取集合里面的元素
//            String key = entry.getKey();//得到key的值（类T的的成员属性）
//            //2.将map中的值存入T这个类的对象属性中
//            Field f = c.getDeclaredField(key);//获取类的所有字段
//            f.setAccessible(true);//简单的理解：设置访问权限
//            f.set(t,entry.getValue());//给T对象赋值
//        }
//        return t;
//    }

}
