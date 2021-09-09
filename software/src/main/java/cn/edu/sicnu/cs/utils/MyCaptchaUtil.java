package cn.edu.sicnu.cs.utils;

import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Classname MyCaptchaUtil
 * @Description TODO
 * @Date 2020/12/8 22:00
 * @Created by Huan
 */
public class MyCaptchaUtil extends CaptchaUtil {
    private static final String SESSION_KEY = "captcha";
    private static final int DEFAULT_LEN = 4;  // 默认长度
    private static final int DEFAULT_WIDTH = 130;  // 默认宽度
    private static final int DEFAULT_HEIGHT = 48;  // 默认高度

    public static void out(Captcha captcha, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        setHeader(response);
//        request.getSession().setAttribute(SESSION_KEY, captcha.text().toLowerCase());

        captcha.out(response.getOutputStream());
    }

    public static boolean ver(String code, HttpServletRequest request) {
        if (code != null) {
            String captcha = (String) request.getSession().getAttribute(SESSION_KEY);
            return code.trim().toLowerCase().equals(captcha);
        }
        return false;
    }

}
