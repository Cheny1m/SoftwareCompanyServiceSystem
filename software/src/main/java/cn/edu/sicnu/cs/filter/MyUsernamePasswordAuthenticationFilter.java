package cn.edu.sicnu.cs.filter;

import cn.edu.sicnu.cs.utils.AESUtils;
import cn.edu.sicnu.cs.utils.ResUtil;
import cn.edu.sicnu.cs.utils.ResponseUtil;
import cn.edu.sicnu.cs.utils.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName MyUsernamePasswordAuthenticationFilter
 * @Description 重写用户form登陆方式  采用json方式登陆
 * @Version 1.0
 **/
public class MyUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${soft_version}")
    private static String soft_version;

    private static String login_url="/"+soft_version+"login";

    public MyUsernamePasswordAuthenticationFilter() {
        //指定登陆路径
        super(new AntPathRequestMatcher("/v1.1/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //从json中获取username和password
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String username = null, password = null,identifier = null,code = null;
        if(StringUtils.hasText(body)) {
            JSONObject jsonObj = JSON.parseObject(body);
            username = jsonObj.getString("username");
            password = jsonObj.getString("password");
            identifier = jsonObj.getString("identifier");
            code = jsonObj.getString("code");
        }

        String s = SpringUtil.getBean(StringRedisTemplate.class).opsForValue().get(identifier);
        if (!StringUtils.hasText(s)||!s.equals(code)) {
            ResponseUtil.out(response,ResUtil.getJsonStr(400,"identifier为空或code错误"));
            return null;
        }

        try {
            username = AESUtils.aesDecrypt(username);
            password = AESUtils.aesDecrypt(password);
        }catch (Exception e){

        }
        String username1 = new String(username.toString());
        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();
        //封装到security提供的用户认证接口中
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
        /*将登陆请求提交给认证 AuthenticationManager管理模块 下的authenticate方法 再由authenticate具体的实现类完成认证服务
        使用默认提供的DaoAuthenticationProvider 这个用户信息查询及存储实现类  */
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
