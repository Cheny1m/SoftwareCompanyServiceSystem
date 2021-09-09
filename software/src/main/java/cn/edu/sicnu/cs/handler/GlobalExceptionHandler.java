package cn.edu.sicnu.cs.handler;

import cn.edu.sicnu.cs.constant.ResultCode;
import cn.edu.sicnu.cs.exception.CommonException;
import cn.edu.sicnu.cs.utils.ResUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.rest.RestUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.NestedServletException;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

/**
 * @Classname GlobalExceptionHandler
 * @Description TODO
 * @Date 2020/12/17 11:31
 * @Created by Huan
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


        /**
         * 处理空指针的异常
         * @param req
         * @param e
         * @return
         */
        @ExceptionHandler(value =NullPointerException.class)
        @ResponseBody
        public String exceptionHandler(HttpServletRequest req, NullPointerException e){
            log.error("空指针异常！原因是:",e);
            return ResUtil.getErrDes("服务器错误!错误详情:"+e.getMessage());
        }

    @ExceptionHandler(value = ValidationException.class)
    @ResponseBody
    public String exceptionHandler(HttpServletRequest req, ValidationException e){
        log.info("传输参数格式错误！原因是:",e.getMessage());
        return ResUtil.getJsonStr(ResultCode.BAD_REQUEST,"传入参数格式错误!错误详情:"+e.getMessage());
    }

    @ExceptionHandler(value = CommonException.class)
    @ResponseBody
    public String exceptionHandler(HttpServletRequest req, CommonException e){
        log.info("token过期或者失效！原因是:",e.getMessage());
        return ResUtil.getJsonStr(1000,"token过期或者失效!错误详情:"+e.getMessage());
    }

    @ExceptionHandler(value = NestedServletException.class)
    @ResponseBody
    public void exceptionHandler(HttpServletRequest req, NestedServletException e){
//        log.info("！原因是:",e.getMessage());
    }
    @ExceptionHandler(value = BadPaddingException.class)
    @ResponseBody
    public void exceptionHandler(HttpServletRequest req, BadPaddingException e){
        log.info("！原因是:",e.getMessage());
    }

    }
