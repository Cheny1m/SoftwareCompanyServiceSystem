package cn.edu.sicnu.cs.controller;


import cn.edu.sicnu.cs.anotations.Log;
import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.pojo.WorkOrderPojo;
import cn.edu.sicnu.cs.service.UserService;
import cn.edu.sicnu.cs.service.WorkOrderManagerService;
import cn.edu.sicnu.cs.utils.ResUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname WorkOrderManagerController
 * @Description TODO
 * @Date 2020/11/16 18:06
 * @Created by Songyz
 */
@Controller
@RequestMapping("/wordermanager")
@Api(tags = "WorkOrderManager",value = "工单管理员")
public class WorkorderManagerController {

    @Resource
    WorkOrderManagerService workOrderManagerService;
    @Resource
    UserService userService;


    @GetMapping("/getorder")
    @ApiOperation(value = "WorkOrder",notes = "获取工单")
    @ResponseBody
    @NewLog(value = "工单管理员查询工单",type = LogConstant.OPERATION)
    public String GetOrder(String select,Integer pagenum,Integer pagesize){
        List<WorkOrderPojo> workOrderPojos;
        HashMap<String,Object> result = new HashMap<>();
        int total;
        if(select.equals("5")){
            workOrderPojos = workOrderManagerService.findAllWorkorders(pagenum,pagesize);
            total = workOrderManagerService.CountAllOrders();
        }else {
            workOrderPojos = workOrderManagerService.selectWorkordersByStatus(select,pagenum,pagesize);
            total = workOrderManagerService.CountByStatus(select);
        }
        for (WorkOrderPojo orderPojo : workOrderPojos) {
            HashMap<String, String> op = new HashMap<String, String>() {
                {
                    put("isActive_0", "false");//审核
                    put("isActive_1", "false");//分配
                    put("isActive_2", "false");//完成
                }
            };
            if(orderPojo.getWprincipal_id() != null){
                orderPojo.setWprincipal_id(userService.selectUserNameByUid(Integer.parseInt(orderPojo.getWprincipal_id())));
            }
            if (orderPojo.getWstatus().equals("0")) {
                op.put("isActive_0", "true");
                orderPojo.setWstatus("未审核");
            } else if (orderPojo.getWstatus().equals("1")) {
                op.put("isActive_1", "true");
                orderPojo.setWstatus("未分配");
            }else if (orderPojo.getWstatus().equals("2")){
                op.put("isActive_1", "true");
                orderPojo.setWstatus("已分配");
            }
            else if (orderPojo.getWstatus().equals("3") || orderPojo.getWstatus().equals("4")) {
                op.put("isActive_2", "true");
                if(orderPojo.getWstatus().equals("3")){
                    orderPojo.setWstatus("已完成");
                }else {
                    orderPojo.setWstatus("未通过审核");
                }
            } else if(orderPojo.getWstatus().equals("2")){
                    orderPojo.setWstatus("处理中");
            }
            orderPojo.setOperations(op);
        }
        result.put("WorkOrders",workOrderPojos);
        result.put("total",total);
        return ResUtil.getJsonStr(1,"成功",result);
    }

    @GetMapping("/check")
    @ApiOperation(value = "CheckOrder",notes = "审核工单")
    @ResponseBody
    @NewLog(value = "工单管理员审核工单",type = LogConstant.OPERATION)
    public String CheckOrder(Long wid,int check){
        try {
            String checkd = Integer.toString(check);
            workOrderManagerService.checkorder(wid,checkd);
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }



    @GetMapping("/allocate")
    @ApiOperation(value = "AllocateOrder",notes = "分配工单")
    @ResponseBody
    @NewLog(value = "工单管理员分配工单",type = LogConstant.OPERATION)
    public String AllocateOrder(Long wid,String name){
        try {
            workOrderManagerService.allocateorder(wid,name);
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }

//    @GetMapping("/finish")
//    @ApiOperation(value = "FinishOrder",notes = "完成工单")
//    @ResponseBody
//    @NewLog(value = "工单管理员完成工单",type = LogConstant.OPERATION)
//    public String FinishOrder(Long wid){
//        try {
//            workOrderManagerService.finishorder(wid);
//            return ResUtil.getJsonStr(1,"成功");
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResUtil.getJsonStr(-1,"失败");
//        }
//
//    }

    @GetMapping("/close")
    @ApiOperation(value = "Closerder",notes = "关闭工单")
    @ResponseBody
    @NewLog(value = "工单管理员关闭工单",type = LogConstant.OPERATION)
    public String Closerder(Long wid){
        try {
            workOrderManagerService.Closerder(wid);
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }

    @GetMapping("/search")
    @ApiOperation(value = "SearchOrder",notes = "搜索工单by名字")
    @ResponseBody
    @NewLog(value = "搜索工单by名字",type = LogConstant.OPERATION)
    public String SearchOrder(String wname,long uid){
        try {
            List<Map<String,Object>> list = workOrderManagerService.SearchOrder(wname,uid);
            Map<String,Object> map = new HashMap<>();
            map.put("list",list);
            return ResUtil.getJsonStr(1,"成功",map);
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }

    @GetMapping("/getfzname")
    @ApiOperation(value = "getfzname",notes = "获取负责人名单")
    @ResponseBody
    @NewLog(value = "获取负责人名单",type = LogConstant.OPERATION)
    public String GetFzname(){
        try {
            return ResUtil.getJsonStr(1,"成功",workOrderManagerService.getFzname());
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

}
