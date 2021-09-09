package cn.edu.sicnu.cs.handler;

import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.pojo.AuthUserDetails;
import cn.edu.sicnu.cs.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Classname CustomLogoutHandler
 * @Description TODO
 * @Date 2020/12/15 10:06
 * @Created by Huan
 */
@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserService userService;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String uid = request.getParameter("uid");
        System.out.println(uid);
        if (uid!=null){
            User user = userService.selectUserByUid(Integer.parseInt(uid));
            stringRedisTemplate.delete("token_"+user.getUsername() );
//            if (user.getUroleId()==3){
//                stringRedisTemplate.delete("kefu:login"+user.getUid());
//            }
            return;
        }
        throw new NullPointerException("退出登录时id 不能为空");
    }
}
