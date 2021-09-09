package cn.edu.sicnu.cs.controller;


import cn.edu.sicnu.cs.anotations.Log;
import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.dao.TaskMapper;
import cn.edu.sicnu.cs.model.Task;
import cn.edu.sicnu.cs.model.UserTeam;
import cn.edu.sicnu.cs.pojo.AddMemberpojo;
import cn.edu.sicnu.cs.pojo.UserInTeam;
import cn.edu.sicnu.cs.pojo.WorkOrderPojo;
import cn.edu.sicnu.cs.service.*;
import cn.edu.sicnu.cs.utils.IdWorker;
import cn.edu.sicnu.cs.utils.ResUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Classname EngineerLeaderController
 * @Description TODO
 * @Date 2020/11/22 10:23
 * @Created by Songyz
 */
@Controller
@Api(tags = "EngineerLeader",value = "工程师负责人")
@RequestMapping("/engleader")
public class EngineerLeaderController {
    @Resource
    UserTeamService userTeamService;

    @Resource
    TaskService taskService;

    @Resource
    WorkOrderManagerService workOrderManagerService;

    @Resource
    WorkOrdersService workOrdersService;


    @GetMapping("/index")
    @ApiOperation(value = "GetIndex",notes = "页面数据")
    @ResponseBody
    @NewLog(value="获取工程师负责人页面数据")
    public String getindex(long uid){
        try {
            List<UserInTeam> userteams = userTeamService.findTeamByName(uid);
            Map<String,Object> map = new HashMap<>();
            map.put("list",userteams);
            return ResUtil.getJsonStr(1, "成功",map);
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }

    @GetMapping("admin/index")
    @ApiOperation(value = "getinfo",notes = "admin页面数据")
    @ResponseBody
    @NewLog(value="获取admin页面数据")
    public String getinfo(){
        try {
            List<UserInTeam> userteams = userTeamService.getinfo();
            Map<String,Object> map = new HashMap<>();
            map.put("list",userteams);
            return ResUtil.getJsonStr(1, "成功",map);
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }

    @GetMapping("/index/add/member")
    @ApiOperation(value = "findmember",notes = "可添加团队的工程师")
    @ResponseBody
    @NewLog(value = "查找可添加团队的工程师")
    public String FindMember(){
        try {
            return ResUtil.getJsonStr(1,"成功",userTeamService.FindMember());
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }


//    @GetMapping("")
//    @ResponseBody
//    @NewLog(value = "查找可添加团队的工程师")
//    public String FindMember(){
//        try {
//            return ResUtil.getJsonStr(1,"成功",userTeamService.FindMember());
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResUtil.getJsonStr(-1,"失败");
//        }
//    }

//    @GetMapping("/index/add")
//    @ApiOperation(value = "AddUserToTeam",notes = "添加成员")
//    @ResponseBody
//    @NewLog(value="添加团队成员")
//    public String AddUserToTeam(String fzname,String username,int days,float hours){
//        try {
//            userTeamService.insertUserTeam(fzname, username, days, hours);
//            return ResUtil.getJsonStr(1,"成功");
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResUtil.getJsonStr(-1,"失败");
//        }
//    }


    @PostMapping("/index/add")
    @ApiOperation(value = "AddUserToTeam",notes = "添加成员")
    @ResponseBody
    @NewLog(value="添加团队成员")
    public String AddUserToTeam(@RequestBody List<AddMemberpojo> addMemberpojoList){
        try {
            userTeamService.insertUserTeam(addMemberpojoList);
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @GetMapping("/index/delete")
    @ApiOperation(value = "DeleteUserFromTeam",notes = "删除成员")
    @ResponseBody
    @NewLog(value="删除团队成员")
    public String DeleteUserFromTeam(String fzname,String username){
        try{
            userTeamService.deleteUserTeam(fzname,username);
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @Autowired
    IdWorker idWorker;

    @PostMapping("/addtask")
    @ApiOperation(value = "AddTask",notes = "添加任务")
    @ResponseBody
    @NewLog(value="添加任务")
    public String AddTask(@RequestBody List<Task> tasks) {
        try {
            for (Task task : tasks) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String today = sdf.format(new Date());
                Date day = new Date();
                day.equals(today);
                task.setTcreatedate(day);
                long m = 100000;
                long n = 100000;
                m = n * m;
                task.setTid(idWorker.nextId() % m);
            }
            taskService.AddTask(tasks);
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @GetMapping("/getorder")
    @ApiOperation(value = "getorder",notes = "负责人查看工单")
    @ResponseBody
    @NewLog(value = "负责人查看工单")
    public String GetOrder(long fzid,int pagenum,int pagesize){
        try {
           List<WorkOrderPojo> list = workOrderManagerService.FindWorkOrderByFzid(fzid,pagenum,pagesize);
           int total = list.size();
           Map<String,Object> data = new HashMap<>();
           data.put("total",total);
           data.put("list",list);
           return ResUtil.getJsonStr(1,"成功",data);
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @GetMapping("/getorderinfo")
    @ApiOperation(value = "getorderinfo",notes = "负责人查看详细工单")
    @ResponseBody
    @NewLog(value = "负责人查看详细工单")
    public String GetOrderinfo(long wid){
        try {
            return ResUtil.getJsonStr(1,"成功",workOrdersService.FindWorkOrder(wid));
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @GetMapping("/finishorder")
    @ApiOperation(value = "finishorder",notes = "负责人完成工单")
    @ResponseBody
    @NewLog(value = "负责人完成工单")
    public String FinishOrder(long wid){
        try {
            workOrderManagerService.finishorder(wid);
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @GetMapping("/getteammember")
    @ApiOperation(value = "getteammember",notes = "负责人完成工单")
    @ResponseBody
    @NewLog(value = "负责人完成工单")
    public String GetTeamMember(Integer fzid){
        try {
            return ResUtil.getJsonStr(1,"成功",userTeamService.GetTeamMember(fzid));
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

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
