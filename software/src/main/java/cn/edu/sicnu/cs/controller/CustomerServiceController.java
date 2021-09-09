package cn.edu.sicnu.cs.controller;


import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.ResultCode;
import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.model.Userform;
import cn.edu.sicnu.cs.model.Workorders;
import cn.edu.sicnu.cs.service.FeedBackService;
import cn.edu.sicnu.cs.service.UserService;
import cn.edu.sicnu.cs.service.impl.CustomersServiceServiceImpl;
import cn.edu.sicnu.cs.service.impl.WorkOrdersServiceImpl;
import cn.edu.sicnu.cs.utils.IdWorker;
import cn.edu.sicnu.cs.utils.ResUtil;
import cn.edu.sicnu.cs.utils.UserOfNumUtils;
import cn.edu.sicnu.cs.vo.StatisticsFormKefuMg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Api(tags = "CustomersService", value = "客服")
@RequestMapping("/customer/")
public class CustomerServiceController {

    @Autowired
    private CustomersServiceServiceImpl customersServiceService;

    @Autowired
    FeedBackService feedBackService;

    @Autowired
    UserService userService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("index/userform/cnt")
    @ResponseBody
    @ApiOperation(value = "TodayUserFormCnt", notes = "查询对应状态的任务数量")
    @NewLog(value = "查询对应状态的任务数量", type = "String")
    public String TodayUserFormCnt(long cid) {

        Map<String, Object> map = new HashMap<>();

        User user = userService.selectUserByUid((int) cid);
        map.put("username", user.getUsername());

        map.put("untreated", customersServiceService.TodayUserFormCnt(cid, "0"));
        map.put("processing", customersServiceService.TodayUserFormCnt(cid, "1"));
        map.put("finished", customersServiceService.TodayUserFormCnt(cid, "2"));
        return ResUtil.getJsonStr(1, "成功", map);
    }

    //

    //统计未完善
    @ApiOperation(value = "StatisticsCnt",notes = "查询根据状态自己的任务列表")
    @GetMapping("statistics")
    @NewLog(value="查询根据状态自己的任务列表")
    @ResponseBody
    public String StatisticsCnt(long uid){
        long youth =  feedBackService.StatisticsWeekCnt(uid);
        long week = feedBackService.StatisticsYouthCnt(uid);
        //long total = feedBackService.StatisticsTotalCnt(uid);

        Map<String, Object> map = new HashMap<>();


        map.put("youth", youth);
        map.put("week", week);
        //map.put("total", total);
        return ResUtil.getJsonStr(1, "成功", map);
    }
    //

    @GetMapping("search/userform")
    @ResponseBody
    @ApiOperation(value = "SearchUserForm", notes = "根据id搜索用户表单")
    @NewLog(value = "根据id搜索用户表单")
    public String SearchUserForm(long fid, long uid) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = feedBackService.SearchUserForm(fid, uid);
        map.put("list", list);
        return ResUtil.getJsonStr(1, "成功", map);
    }

    @GetMapping("index/today/untreated/userform/list")
    @ResponseBody
    @ApiOperation(value = "FindTodayUntreatedUserFormListByCid", notes = "查询对应状态的表单列表")
    @NewLog(value = "查询对应状态的表单列表")
    public String FindUserFormList(@RequestParam("cid") long cid,@RequestParam("page") long page,@RequestParam("pagenum") long pagenum,@RequestParam("status") String status) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list;
        long total;

        if(cid == 1){
            list = customersServiceService.AdminFindUserFormList(page, pagenum);
            total = customersServiceService.AdminFindUserFormCnt();
            map.put("total", total);
            map.put("list", list);
        }else{
            if (!status.equals("3")) {
                list = customersServiceService.FindUserFormList(cid, page, pagenum, status);
                total = customersServiceService.FindUserFormCnt(cid, status);
                map.put("total", total);
                map.put("list", list);
            } else {
                map.put("total", customersServiceService.FindAllUserFormCnt(cid));
                map.put("list", customersServiceService.FindAllUserFormList(cid, page, pagenum));
            }
        }
        //System.out.println("111111111111111    " + status);

        return ResUtil.getJsonStr(1, "成功", map);
    }

    //
    @GetMapping("form/userform")
    @ResponseBody
    @ApiOperation(value = "FindUserFormByFid", notes = "查看表单详情")
    @NewLog(value = "查看表单详情")
    public String FindUserFormByFid(long fid) {

        Map<String, Object> map = new HashMap<>();
        map.put("details", customersServiceService.FindUserFormByFid(fid));
        return ResUtil.getJsonStr(1, "成功", map);
    }

    //
    @Autowired
    WorkOrdersServiceImpl workOrdersService;

    @Autowired
    IdWorker idWorker;

    @PostMapping("form/submit/workorder")
    @ApiOperation(value = "WorkOrderSubmit", notes = "提交工单")
    @NewLog(value = "提交工单")
    @ResponseBody
    public String WorkOrderSubmit(HttpServletRequest request) throws Exception {
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.hasText(body)) {
            Workorders workorders = JSON.parseObject(body, Workorders.class);
            System.out.println("body:");
            System.out.println(body);

            long n =1000000;
            long m = 1000000;
            m = n * m;
            long wid = idWorker.nextId() / m;
            workorders.setWid(wid);
            //还需要一个分配id的函数、
//
            int userid = 8;
            workorders.setWuserId(userid);

//            userform.setFassignedtoId(userid);
//            //还需要一个随机分配给客服的函数
//
//            //插入当天的时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR,3);
            Date time = cal.getTime();
            Date day = new Date();

            day.equals(today);
            workorders.setWddl(time);
            workorders.setWcreattime(day);
//            userform.setFcreatetime(day);

            //还需要判断很多字段的插入

            workOrdersService.InsertWorkOrder(workorders);
            return ResUtil.getJsonStr(1, "成功");
        } else {
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

    @GetMapping("self/workorder/list")
    @ResponseBody
    @ApiOperation(value = "FindSelfWorkOrderSubmit", notes = "查看自己提交工单列表")
    @NewLog(value = "查看自己提交工单列表")
    public String FindSelfWorkOrderSubmit(long cid, long page, long pagenum) {


        List<Map<String, Object>> list = workOrdersService.FindSelfWorkOrderSubmit(cid, page, pagenum);
        long total = workOrdersService.FindSelfWorkOrderSubmitCnt(cid);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);
        return ResUtil.getJsonStr(1, "成功", map);
    }

    @GetMapping("search/workorder")
    @ResponseBody
    @ApiOperation(value = "SearchWorkorder", notes = "搜索工单")
    @NewLog(value = "搜索工单")
    public String SearchWorkorder(String wname, long cid) {

        List<Map<String, Object>> list = workOrdersService.SearchWorkorder(wname, cid);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);


        return ResUtil.getJsonStr(1, "成功", map);
    }


    @PostMapping("self/userform/finish")
    @ResponseBody
    @ApiOperation(value = "FinishUserForm", notes = "完成用户表单")
    @NewLog(value = "完成用户表单")
    public String FinishUserForm(HttpServletRequest request) throws IOException {
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(body);
        String fid = jsonObject.getString("fid");
        long id = Long.parseLong(fid);
        feedBackService.FinishUserForm(id);

        String recontent = jsonObject.getString("recontent");
        String restate = "1";
        feedBackService.WriteRemarks("1", id, restate, recontent);
        return ResUtil.getJsonStr(1, "成功");
    }

    @GetMapping("self/workorder")
    @ResponseBody
    @ApiOperation(value = "FindSelfWorkOrder", notes = "查看工单详情")
    @NewLog(value = "查看工单详情")
    public String FindSelfWorkOrder(long wid) {
        Workorders workorders = workOrdersService.FindWorkOrder(wid);
        Map<String, Object> map = new HashMap<>();
        map.put("details", workorders);
        return ResUtil.getJsonStr(1, "成功", map);
    }

    @ResponseBody
    @GetMapping("statistics/today")
    public String findStatisticsToday() {
        // 客服总数
        int kefuZhongshu = userService.selectUserNumByRoleId(3);
        // 平均在线时常
        float averageTime = UserOfNumUtils.selectAverageLoginedTime();
        // 登录用户数量
        int loginedUserNum = UserOfNumUtils.selectLoginedUserNum("kefu:login:*");

        List<Userform> userforms1 = feedBackService.todayUserForm("0");
        List<Userform> userforms2 = feedBackService.todayUserForm("1");
        List<Userform> userforms3 = feedBackService.todayUserForm("2");
        // 处理表单比率
        int handelerRatio = 0;
        if (userforms3.size() > 0 || userforms2.size() > 0 || userforms1.size() > 0) {
            handelerRatio = (userforms1.size() + userforms2.size()) / ((userforms1.size() + userforms2.size()) + userforms3.size());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("kefuZhongshu", kefuZhongshu);
        map.put("averageTime", averageTime);
        map.put("loginedUserNum", loginedUserNum);
        map.put("handelerRatio", handelerRatio);
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", map);
    }

    /**
     * 查询客服管理员统计表单
     * @return
     */
    @ResponseBody
    @GetMapping("statistics/form")
    public String getStatisticsForMmus(@RequestParam("page") int page, @RequestParam("size") int size){
        String[] kefuIds = UserOfNumUtils.selectLoginedUserId("kefu:login:*");
        List<StatisticsFormKefuMg> statisticsForms = new ArrayList<>();
        for (String kefuId : kefuIds) {
            User kefu = userService.selectUserByUid(Integer.parseInt(kefuId.split(":")[2]));
            String timeStamp = stringRedisTemplate.opsForValue().get("kefu:login:" + kefuId.split(":")[2]);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 客服登录时间
            System.out.println(timeStamp.hashCode());
            if (timeStamp==null){
                System.out.println("timeStamp == null");
            }
            Date date = new Date(Long.parseLong(timeStamp));
            String loginTime = sdf.format(date);
            // 客服今天正在处理表单数
            int number1 = feedBackService.TodayUserFormCnt(kefu.getUid(),"1");
            // 客服今天处理完成表单数
            int number2 = feedBackService.TodayUserFormCnt(kefu.getUid(), "2");
            // 在线时长
            String loginedTimeLength = UserOfNumUtils.selectLoginedTime(String.valueOf(kefu.getUid()));
            // 提交工单数
            long commitOrderNum = workOrdersService.FindKefuCommitNumToday(kefu.getUid());
            int sum = number1+number2;
            if (sum==0){
                statisticsForms.add(new StatisticsFormKefuMg(kefu.getUrealname(),loginTime,0, (int) commitOrderNum,
                        0,loginedTimeLength));
            }else {
                statisticsForms.add(new StatisticsFormKefuMg(kefu.getUrealname(),loginTime,number1+number2, (int) commitOrderNum,
                        number1/(float)(number2+number1),loginedTimeLength));
            }
        }
        try {
            statisticsForms.subList(Math.min((page - 1) * size, statisticsForms.size())
                    ,((page-1)*size+size)<=statisticsForms.size()?((page-1)*size+size):statisticsForms.size()-1);
        }catch (Exception e){
            statisticsForms = null;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("content",statisticsForms);
        map.put("totalElementNum",statisticsForms==null?0:statisticsForms.size());

        return ResUtil.getJsonStr(ResultCode.OK,"查询成功",map);
    }
}
