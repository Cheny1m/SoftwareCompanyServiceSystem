package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.model.Userform;
import cn.edu.sicnu.cs.service.FeedBackService;
import cn.edu.sicnu.cs.service.UserService;
import cn.edu.sicnu.cs.utils.IdWorker;
import cn.edu.sicnu.cs.utils.ResUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "FeedBack",value = "反馈")
@RequestMapping("/feedback/")
public class FeedBackController {

    @Autowired
    FeedBackService feedBackService;

    @GetMapping("addfviews")
    @ApiOperation(value = "addfviews",notes = "增加点击数")
    @ResponseBody
    public String addfviews(long fid){
        feedBackService.updateFviewsByFid(fid);
        return ResUtil.getJsonStr(1,"成功");
    }

    @GetMapping("browse/hot/userform/list")
    @ApiOperation(value = "FindHotUserFormList",notes = "查看热门问题列表,page是第几页")
    @ResponseBody
    @NewLog(value = "查看热门问题列表,page是第几页",type = LogConstant.OPERATION)
    public String FindHotUserFormList(long page,long pagenum){
        List<Map<String,Object>> list = feedBackService.FindHotUserFormList(page,pagenum);

        for (Map<String, Object> map : list) {
            String  decs = ((String)map.get("fcontent")).substring(0,5);
            map.put("decs",decs);
        }

        long total = feedBackService.FindHotUserFormListCnt();
        Map<String,Object> map = new HashMap<>();
        map.put("list",list);
        map.put("total",total);
        return ResUtil.getJsonStr(1, "成功",map);
    }

//    @GetMapping("browse/hot/userform/{fid}")
//    @ResponseBody
//    @ApiOperation(value = "FindSelfUserFormByFid",notes = "问题详情")
//    @NewLog(value = "问题详情",type = LogConstant.OPERATION)
//    public Userform FindUserFormByFid(@PathVariable long fid){
//        Userform userform = feedBackService.FindUserFormByFid(fid);
//        return userform;
//    }


    @GetMapping("browse/self/userform/list")
    @ResponseBody
    @ApiOperation(value = "FindSelfUserFormList",notes = "自己已提交的问题列表")
    @NewLog(value = "自己已提交的问题列表",type = LogConstant.OPERATION)
    public String FindSelfUserFormList(long page,long pagenum,long uid){
        List<Map<String,Object>> list = feedBackService.FindSelfUserFormList(page,pagenum,uid);
        Map<String,Object> map = new HashMap<>();
        long total = feedBackService.FindAllSelfUserFormCnt(uid);
        map.put("list",list);
        map.put("total",total);
        return ResUtil.getJsonStr(1, "成功",map);
    }



    @GetMapping("search/userform")
    @ResponseBody
    @ApiOperation(value = "SearchUserFormt",notes = "搜索用户表单")
    @NewLog(value = "搜索用户表单")
    public String SearchUserFormt(long fid,long uid){
        List<Map<String,Object>> list = feedBackService.SearchUserFormt(fid,uid);
        Map<String,Object> map = new HashMap<>();
        map.put("list",list);
        return ResUtil.getJsonStr(1, "成功",map);
    }


//    @GetMapping("browse/self/userform/{fid}")
//    @ResponseBody
//    @ApiOperation(value = "FindSelfUserFormByFid",notes = "自己已提交的问题详情")
//    @NewLog(value = "自己已提交的问题详情",type = LogConstant.OPERATION)
//    public Userform FindSelfUserFormByFid(@PathVariable long fid){
//        Userform userform = feedBackService.FindUserFormByFid(fid);
//        return userform;
//    }


    @Autowired
    IdWorker idWorker;

    @Autowired
    UserService userService;

    @GetMapping("submit/info")
    @ResponseBody
    @ApiOperation(value = "SubmitInfo",notes = "提交问题前返回信息")
    @NewLog(value = "提交问题前返回信息",type = LogConstant.OPERATION)
    public String SubmitInfo(long uid){
        Map<String,Object> map = new HashMap<>();
        map.put("info",userService.GetSubmitInfo(uid));
        //map.put("",);
        return ResUtil.getJsonStr(1, "成功",map);
    }


    @GetMapping("delete/userform")
    @ResponseBody
    @ApiOperation(value = "DeleteUserform",notes = "删除用户表")
    @NewLog(value="提交问题前返回信息")
    public String DeleteUserForm(long fid){
        feedBackService.DeleteUserForm(fid);
        return ResUtil.getJsonStr(1, "成功");
    }

    @PostMapping("sumbmit/userform")
    @ResponseBody
    @ApiOperation(value = "SumbmitUserForm",notes = "用户提交问题")
    @NewLog(value = "用户提交问题",type = LogConstant.OPERATION)
    public String SumbmitUserForm(HttpServletRequest request) throws Exception {

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.hasText(body)){
            Userform userform = JSON.parseObject(body,Userform.class);
            System.out.println(body);
            System.out.println(userform.toString());


            long n =1000000;
            long m = 1000000;
            m = n * m;
            long fid = idWorker.nextId() / m;
            userform.setFid(fid);
            //还需要一个分配id的函数、

            int userid = 8;
            userform.setFassignedtoId(userid);
            //还需要一个随机分配给客服的函数

            //插入当天的时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            Date day = new Date();
            day.equals(today);
            userform.setFcreatetime(day);

            //还需要判断很多字段的插入
            userform.setFstatus("0");

            userform.setFcontactname(userService.selectUserByUid(userform.getFuserId()).getUrealname());
            feedBackService.SubmitUserForm(userform);
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
