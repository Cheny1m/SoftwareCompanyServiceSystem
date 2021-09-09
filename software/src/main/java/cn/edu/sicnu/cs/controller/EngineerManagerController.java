package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.pojo.EngInfoPojo;
import cn.edu.sicnu.cs.service.UserTeamService;
import cn.edu.sicnu.cs.service.impl.EngineerManagerServiceimpl;
import cn.edu.sicnu.cs.utils.ResUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Classname EngineerManagerController
 * @Description TODO
 * @Date 2020/11/19 13:54
 * @Created by Songyz
 */
@Controller
@RequestMapping("/engmanager")
@Api(tags = "EngineerManager",value = "工程师管理员")
public class EngineerManagerController {

    @Autowired
    EngineerManagerServiceimpl engineerManagerService;

    @Resource
    UserTeamService userTeamService;


    @GetMapping("/index")
    @ApiOperation(value = "GetIndex",notes = "界面数据")
    @ResponseBody
    @NewLog(value="获取工程师管理员数据")
    public String GetIndex(int pagenum,int pagesize,int status){
        try {
            return ResUtil.getJsonStr(1,"成功",engineerManagerService.FindEngineer(pagenum,pagesize,status));
        } catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }

    @PostMapping("/index/add")
    @ApiOperation(value = "AddEngineer",notes = "添加工程师")
    @ResponseBody
    @NewLog(value = "添加工程师",type = LogConstant.OPERATION)
    public String AddEngineer(@RequestBody List<User> username){
        try {
            engineerManagerService.updateRolesByEngManager(username,"工程师");
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @GetMapping("/index/add/data")
    @ApiOperation(value = "adddata",notes = "普通用户数据")
    @ResponseBody
    @NewLog(value = "普通用户数据",type = LogConstant.OPERATION)
    public String AddData(){
        try {
            return ResUtil.getJsonStr(1,"成功",engineerManagerService.FindUserMap());
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @GetMapping("/index/setrole")
    @ApiOperation(value = "SetRole",notes = "改变工程师职务")
    @ResponseBody
    @NewLog(value="改变工程师职务")
    public String SetRole(String username,String rolename,Integer uid){
        try {
            engineerManagerService.updateRoleByEngManager(username,rolename);
            if(rolename.equals("工程师")){
                //删除团队以及成员信息
                userTeamService.deleteTeam(uid);
            }
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }
    }

    @PostMapping("/index/changeinfo")
    @ApiOperation(value = "changeinfo",notes = "更改信息")
    @ResponseBody
    @NewLog(value = "更改信息")
    public String changeinfo(HttpServletRequest request) {

        try {
            engineerManagerService.updateEngInfo(request);
            return ResUtil.getJsonStr(1,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }


    @GetMapping("/index/checkinfos")
    @ApiOperation(value = "checkinfos",notes = "查看信息")
    @ResponseBody
    @NewLog(value = "查看信息")
    public String checkinfos(int uid){
        try {
            EngInfoPojo engInfoPojo = engineerManagerService.FindEngineerById(uid);
            return ResUtil.getJsonStr(1,"成功",engInfoPojo);
        }catch (Exception e){
            e.printStackTrace();
            return ResUtil.getJsonStr(-1,"失败");
        }

    }
}
