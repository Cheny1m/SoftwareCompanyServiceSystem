package cn.edu.sicnu.cs.aspect;


import cn.edu.sicnu.cs.entity.Log;
import cn.edu.sicnu.cs.service.LogService;
import cn.edu.sicnu.cs.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.CharsetEncoder;


/**
 * 日志切面
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    private final LogService logService;

    public LogAspect(LogService logService) {
        this.logService = logService;
    }

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(cn.edu.sicnu.cs.anotations.Log)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 新切入点
     */
    @Pointcut("@annotation(cn.edu.sicnu.cs.anotations.NewLog)")
    public void newLogPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知，使用在方法logPointcut()上注册的切入点
     * @param proceedingJoinPoint
     * @return
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;
        currentTime.set(System.currentTimeMillis());
        result = proceedingJoinPoint.proceed();
        Log log = new Log("INFO", System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), IpUtils.getIpAddr(request),proceedingJoinPoint, log);
        return result;
    }

    /**
     * 配置环绕通知，使用在方法logPointcut()上注册的切入点
     * @param proceedingJoinPoint
     * @return
     */
    @Around("newLogPointcut()")
    public Object newLogAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;
        currentTime.set(System.currentTimeMillis());
        result = proceedingJoinPoint.proceed();
        Log log = new Log("INFO", System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), IpUtils.getIpAddr(request),proceedingJoinPoint, log);
        return result;
    }


    /**
     * 配置登录通知，使用在方法@NewLogLogin注解的切入点
     * @param proceedingJoinPoint
     * @return
     */
    @Around("@annotation(cn.edu.sicnu.cs.anotations.LogLogin)")
    public Object logLoginAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;
        currentTime.set(System.currentTimeMillis());
        result = proceedingJoinPoint.proceed();
        Log log = new Log("LOGIN", System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), IpUtils.getIpAddr(request),proceedingJoinPoint, log);
        return result;
    }


    /**
     * 配置异常通知
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "logPointcut()",throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Log log = new Log("ERROR", System.currentTimeMillis() - currentTime.get());
        log.setExceptionDetail(ThrowableUtil.getStackTrace(e).getBytes());
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), IpUtils.getIpAddr(request), (ProceedingJoinPoint)joinPoint, log);
    }


    /**
     * 配置异常通知
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "newLogPointcut()",throwing = "e")
    public void newLogAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Log log = new Log("ERROR", System.currentTimeMillis() - currentTime.get());
        log.setExceptionDetail(ThrowableUtil.getStackTrace(e).getBytes());
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), IpUtils.getIpAddr(request), (ProceedingJoinPoint)joinPoint, log);
    }

    public String getUsername() {

        try {
            return SecurityUtils.getCurrentUsername();
        }catch (Exception e){
            return "";
        }
    }

}
