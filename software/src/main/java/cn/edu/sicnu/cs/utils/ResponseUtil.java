package cn.edu.sicnu.cs.utils;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Exrickx
 */
@Slf4j
public class ResponseUtil {

    /**
     * 使用response输出JSON
     *
     * @param response
     * @param res
     */
    public static void out(HttpServletResponse response, String res) {

        response.setContentType("application/json;charset=UTF-8");

        response.setHeader("Cache-Control","no-store, max-age=0, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setStatus(JSONObject.parseObject(res).getInteger("code")==1000||JSONObject.parseObject(res).getInteger("code")
        ==999?200:JSONObject.parseObject(res).getInteger("code"));
//        JSONObject.parseObject(res).getInteger("code")==1000?;
//        JSONObject.parseObject(res).getInteger("code");
        /*//统一用过滤器设置跨域

        response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization");
        response.setHeader("Access-Control-Allow-Credentials","true");*/
        try {

            PrintWriter out = response.getWriter();
            out.write(res);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.info("response 已经被输出了");
//            e.printStackTrace();
        }

    }


}
