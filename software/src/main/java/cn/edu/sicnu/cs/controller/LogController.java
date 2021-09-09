package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.constant.ResultCode;
import cn.edu.sicnu.cs.entity.Log;
import cn.edu.sicnu.cs.query.LogQueryCriteria;
import cn.edu.sicnu.cs.service.LogService;
import cn.edu.sicnu.cs.utils.ResUtil;
import cn.edu.sicnu.cs.utils.SecurityUtils;
import cn.edu.sicnu.cs.utils.TimeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/${soft_version}/logs")
@Api(tags = "监控：日志管理")
public class LogController {

    private final Logger logger = LoggerFactory.getLogger(LogController.class);

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @NewLog(value = "导出数据", type = LogConstant.OPERATION)
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, LogQueryCriteria criteria) throws IOException {
        if ("ERROR".equals(criteria.getLogType())) {

            logService.downloadError(logService.queryAll(criteria), response);
        } else {

            logService.download(logService.queryAll(criteria), response);
        }
    }

//    @GetMapping
//    @ApiOperation("日志查询")
//    public ResponseEntity<Object> getLogs(LogQueryCriteria criteria, Pageable pageable){
//        criteria.setLogType("INFO");
//        return new ResponseEntity<>(logService.queryAll(criteria,pageable), HttpStatus.OK);
//    }

    @GetMapping
    @ApiOperation("日志查询")
    public String getLogs(LogQueryCriteria criteria, @PageableDefault Pageable pageable) {
//        criteria.setLogType("INFO");
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of((pageable.getPageNumber() == 0 ? 1 : pageable.getPageNumber()) - 1, pageable.getPageSize(),sort);
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", logService.queryAllByUser(criteria, pageable));
    }

    @GetMapping("/login/_all")
    @ApiOperation("查询系统内所有用户的登录日志")
    @NewLog(value = "查询系统内所有用户的登录日志", type = LogConstant.OPERATION)
    public String getAllLoginLogs(LogQueryCriteria criteria, @PageableDefault Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of((pageable.getPageNumber() == 0 ? 1 : pageable.getPageNumber()) - 1, pageable.getPageSize(),sort);
        criteria.setLogType("LOGIN");
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", logService.queryAllByUser(criteria, pageable));
    }

    /**
     * 查询指定角色的登录日志
     *
     * @param criteria
     * @param pageable
     * @return
     */
    @GetMapping("/login/_all/role")
    @ApiOperation("查询指定角色的登录日志")
    @NewLog(value = "查询指定角色的登录日志", type = LogConstant.OPERATION)
    public String getLoginLogs(LogQueryCriteria criteria, @PageableDefault Pageable pageable) {
        criteria.setLogType(LogConstant.LOGIN);
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of((pageable.getPageNumber() == 0 ? 1 : pageable.getPageNumber()) - 1, pageable.getPageSize(),sort);
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", logService.queryAllByUser(criteria, pageable));
    }

    @GetMapping("/operation/_all")
    @ApiOperation("查询指定角色的操作日志")
    @NewLog(value = "查询指定角色的操作日志", type = LogConstant.OPERATION)
    public String getOperationLogs(LogQueryCriteria criteria, @PageableDefault Pageable pageable) {
        criteria.setLogType(LogConstant.OPERATION);
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of((pageable.getPageNumber() == 0 ? 1 : pageable.getPageNumber()) - 1, pageable.getPageSize(),sort);
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", logService.queryAllByUser(criteria, pageable));
    }

    @GetMapping("/_all")
    @ApiOperation("通用日志接口")
    @NewLog(value = "通用日志接口", type = LogConstant.OPERATION)
    public String getuniversalLogs(LogQueryCriteria criteria, @PageableDefault Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of((pageable.getPageNumber() == 0 ? 1 : pageable.getPageNumber()) - 1, pageable.getPageSize(),sort);
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", logService.queryAllByUser(criteria, pageable));
    }


    @GetMapping(value = "/log/{id}")
    @ApiOperation("日志详情查询")
    public String getCertainLog(@PathVariable Long id) {
        Log byDetail = (Log) logService.findByDetail(id);
        if (byDetail.getCreateTime() != null) {
            return ResUtil.getJsonStr(ResultCode.OK, "查询成功", byDetail);
        } else {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "id不存在");
        }
    }

    @GetMapping(value = "/user")
    @ApiOperation("用户日志查询")
    public String getUserLogs(LogQueryCriteria criteria, @PageableDefault Pageable pageable) {
        criteria.setLogType("INFO");
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of((pageable.getPageNumber() == 0 ? 1 : pageable.getPageNumber()) - 1, pageable.getPageSize(),sort);
        logger.debug("LogQueryCriteria :" + criteria + " pageable :" + pageable);

        criteria.setBlurry(SecurityUtils.getCurrentUsername());
        Object o = logService.queryAllByUser(criteria, pageable);
//        return new ResponseEntity<>(logService.queryAllByUser(criteria, pageable), HttpStatus.OK);
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", o);
    }

    /**
     * 分时间段查询用户日志,code = 1,查询今天的日志,code = 2,查询昨天的日志,code = 3 ,查询上一周,code = 4 ,查询上一个月
     * code = 5 ,查询本周
     *
     * @param criteria
     * @param pageable
     * @param code
     * @return
     */
    @GetMapping(value = "/time/{code}")
    @ApiOperation("分时段查询用户日志")
    public String getLogsBetwwenTime(LogQueryCriteria criteria, @PageableDefault Pageable pageable, @PathVariable("code") String code) {
        logger.info("分时段查询用户" + "code = " + code);
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of((pageable.getPageNumber() == 0 ? 1 : pageable.getPageNumber()) - 1, pageable.getPageSize(),sort);
        List<Timestamp> timestamps = new ArrayList<>();
        if ("1".equals(code)) {
            timestamps = TimeUtils.today();
        } else if ("2".equals(code)) {
            timestamps = TimeUtils.yesterday();
        } else if ("3".equals(code)) {
            timestamps = TimeUtils.lastWeek();
        } else if ("4".equals(code)) {
            timestamps = TimeUtils.lastMouth();
        } else if ("5".equals(code)) {
            timestamps = TimeUtils.thisWeek();
        } else {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "code不合法");
        }

        criteria.setCreateTime(timestamps);


        Object o = logService.queryAllByUser(criteria, pageable);
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", o);
    }

    @GetMapping(value = "/error")
    @ApiOperation("错误日志查询")
    public ResponseEntity<Object> getErrorLogs(LogQueryCriteria criteria, @PageableDefault Pageable pageable) {
        criteria.setLogType("ERROR");
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of((pageable.getPageNumber() == 0 ? 1 : pageable.getPageNumber()) - 1, pageable.getPageSize(),sort);
        return new ResponseEntity<>(logService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/error/{id}")
    @ApiOperation("日志异常详情查询")
    public ResponseEntity<Object> getErrorLogs(@PathVariable Long id) {
        return new ResponseEntity<>(logService.findByErrDetail(id), HttpStatus.OK);
    }


    @DeleteMapping(value = "/del/error")
    @NewLog(value = "删除所有ERROR日志", type = LogConstant.OPERATION)
    @ApiOperation("删除所有ERROR日志")
    public ResponseEntity<Object> delAllByError() {
        logService.delAllByError();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/del/info")
    @NewLog(value = "删除所有INFO日志", type = LogConstant.OPERATION)
    @ApiOperation("删除所有INFO日志")
    public ResponseEntity<Object> delAllByInfo() {
        logService.delAllByInfo();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
