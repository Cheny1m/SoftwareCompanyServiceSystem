package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.pojo.AuthUserDetails;
import cn.edu.sicnu.cs.pojo.UserPojo;
import cn.edu.sicnu.cs.repository.LogRepository;
import cn.edu.sicnu.cs.datamapper.LogErrorMapper;
import cn.edu.sicnu.cs.datamapper.LogSmallMapper;
import cn.edu.sicnu.cs.entity.Log;

import cn.edu.sicnu.cs.query.LogQueryCriteria;
import cn.edu.sicnu.cs.service.LogService;
import cn.edu.sicnu.cs.service.UserService;
import cn.edu.sicnu.cs.utils.*;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Huan
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LogServiceImpl implements LogService {


    private final LogRepository logRepository;
//    private final logRepository logRepository;

    private final LogErrorMapper logErrorMapper;

    private final LogSmallMapper logSmallMapper;

    @Autowired
    UserService userService;


    public LogServiceImpl(LogRepository logRepository, LogErrorMapper logErrorMapper, LogSmallMapper logSmallMapper) {
        this.logRepository = logRepository;
        this.logErrorMapper = logErrorMapper;
        this.logSmallMapper = logSmallMapper;
    }

    @Override
    public Object queryAll(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)), pageable);

        System.out.println("page = " + page.getTotalElements());

        String status = "ERROR";
        if (status.equals(criteria.getLogType())) {
            return PageUtilLog.toPage(page.map(logErrorMapper::toDto));
        }
        return page;
    }

    @Override
    public List<Log> queryAll(LogQueryCriteria criteria) {
        return logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)));
    }

    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable) {
        Page<Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)), pageable);
        return PageUtilLog.toPage(page.map(logSmallMapper::toDto));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log log) {

        Signature sig = joinPoint.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = joinPoint.getTarget();
        Method method = null;
        try {
            method = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        // 错误,不能拿到实际执行的方法,拿到是接口的方法签名
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();

//        cn.edu.sicnu.cs.anotations.Log aopLog = method.getAnnotation(cn.edu.sicnu.cs.anotations.Log.class);
        cn.edu.sicnu.cs.anotations.NewLog aopLog = method.getAnnotation(cn.edu.sicnu.cs.anotations.NewLog.class);
        cn.edu.sicnu.cs.anotations.LogLogin aopLoglogin = method.getAnnotation(cn.edu.sicnu.cs.anotations.LogLogin.class);
        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + sig.getName() + "()";

        StringBuilder params = new StringBuilder("{");
        //参数值
        Object[] argValues = joinPoint.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        if (argValues != null && argNames != null) {
            for (int i = 0; i < argValues.length; i++) {
                params.append(" ").append(argNames[i]).append(": ").append(argValues[i]);
            }
        }

        // 描述
        if (log != null) {
            if (aopLog == null && aopLoglogin != null) {
                log.setDescription(aopLoglogin.value());
            } else if (aopLog != null) {
                log.setDescription(aopLog.value());
            }

        }
        assert log != null;
        log.setRequestIp(ip);

//        String loginPath = "login";
//        if(loginPath.equals(signature.getName())){
//            try {
//                assert argValues != null;
//                username = new JSONObject(argValues[0]).get("username").toString();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        String loginPath = "onAuthenticationFailure";
        System.out.println(sig.getName());

        if (loginPath.equals(sig.getName())) {
            try {
                assert argValues != null;
                username = ((Authentication) argValues[2]).getName();
                if (StrUtil.isBlank(username)){
                    return;
                }
            } catch (Exception e) {
//                e.printStackTrace();
                return;
            }
        }
        assert aopLog != null;
        if (StringUtils.isNotBlank(aopLog.type())) {
            log.setLogType(aopLog.type());
        } else {
            if (StringUtils.isBlank(log.getLogType())) {
                log.setLogType("INFO");
            }
        }

        log.setAddress(AddressUtils.getRealAddressByIP(log.getRequestIp()));
        log.setMethod(methodName);
        User user = userService.selectUserByUsername(username);
        if (!(user ==null) &&!(user.getUrealname()==null)){
            log.setUsername(user.getUrealname());
        }

        UserPojo userPojo = userService.selectUserPojoByUsername(username);
        log.setRolename(userPojo.getRoleInfo().getRname());
        log.setParams(params.toString() + " }");
        log.setBrowser(browser);
        logRepository.save(log);
    }


    @Override
    public Object findByErrDetail(Long id) {
        Log log = logRepository.findById(id).orElseGet(Log::new);
        ValidationUtil.isNull(log.getId(), "Log", "id", id);
        byte[] details = log.getExceptionDetail();
        return Dict.create().set("cn/edu/sicnu/cs/exception", new String(ObjectUtil.isNotNull(details) ? details : "".getBytes()));
    }

    @Override
    public Object findByDetail(Long id) {
        return logRepository.findById(id).orElseGet(Log::new);
    }

    @Override
    public void download(List<Log> logs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Log log : logs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", log.getUsername());
            map.put("IP", log.getRequestIp());
            map.put("IP来源", log.getAddress());
            map.put("描述", log.getDescription());
            map.put("请求方法", log.getMethod());
            map.put("请求参数", log.getParams());
            map.put("浏览器", log.getBrowser());
            map.put("请求耗时/毫秒", log.getTime());
            map.put("创建日期", log.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadError(List<Log> logs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Log log : logs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", log.getUsername());
            map.put("IP", log.getRequestIp());
            map.put("IP来源", log.getAddress());
            map.put("描述", log.getDescription());
            map.put("请求方法", log.getMethod());
            map.put("请求参数", log.getParams());
            map.put("浏览器", log.getBrowser());
            map.put("请求耗时/毫秒", log.getTime());
            map.put("异常详情", new String(ObjectUtil.isNotNull(log.getExceptionDetail()) ? log.getExceptionDetail() : "".getBytes()));
            map.put("创建日期", log.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByError() {
        logRepository.deleteByLogType("ERROR");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByInfo() {
        logRepository.deleteByLogType("INFO");
    }
}
