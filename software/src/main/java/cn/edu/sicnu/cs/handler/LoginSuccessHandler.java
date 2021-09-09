package cn.edu.sicnu.cs.handler;

import cn.edu.sicnu.cs.anotations.Log;
import cn.edu.sicnu.cs.anotations.LogLogin;
import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.constant.ResultCode;
import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.pojo.AuthUserDetails;
import cn.edu.sicnu.cs.pojo.RoleInfo;
import cn.edu.sicnu.cs.service.RoleService;
import cn.edu.sicnu.cs.service.UserService;
import cn.edu.sicnu.cs.utils.*;
import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName LoginSuccessHandler
 * @Description 登陆认证成功处理过滤器
 * @Author huan
 * @Date 2020/11/16 16:27
 * @Version 1.0
 **/
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Override
    @NewLog(value = "登录成功",type = LogConstant.LOGIN)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        AuthUserDetails authUserDetails=(AuthUserDetails)authentication.getPrincipal();//从内存中获取当前认证用户信息
//        String userName =authentication.getName();
        String userName = authUserDetails.getUsername();
        String userName1 =userName;
        //在redis中查询用户之前是否登入
        String oldToken=stringRedisTemplate.opsForValue().get("token_"+userName );
        if(!StringUtils.isBlank(oldToken)){
            //清除旧Token
            stringRedisTemplate.delete("token_"+userName );
        }

        String roleInfosMapPermission=stringRedisTemplate.opsForValue().get("authentication:roleinfos:permissions");


        if(StringUtils.isBlank(roleInfosMapPermission)){
            //将角色与权限关系存入redis
            List<RoleInfo> roleInfos= roleService.selectAllRoleAndMetaoperations();
            stringRedisTemplate.opsForValue().set("authentication:roleinfos:permissions", JSON.toJSONString(roleInfos),480,TimeUnit.MINUTES);
        }

        //创建token
        String accessToken = tokenUtil.createAccessJwtToken(authUserDetails);

        int roleid = authUserDetails.getRoleInfo().getRid();

        // 修改最后登录时间和ip地址
        User updateUser = new User();
        User user = userService.selectUserByUsername(userName);
        updateUser.setUvisits(user.getUvisits()+1);
        updateUser.setUlasttime(new Date(System.currentTimeMillis()));
        updateUser.setUip(IpUtils.getIpAddr(request));
        try {
            userService.updateUserByUserName(userName,updateUser);
        } catch (SQLIntegrityConstraintViolationException throwables) {
            throwables.printStackTrace();
        }

        //存入redis
        stringRedisTemplate.opsForValue().set("token_"+userName ,accessToken,480,TimeUnit.MINUTES);
        User user1 = userService.selectUserByUsername(userName);
        HashMap<String,String> map=new HashMap<>();
//        String roleId = AESUtils.aesEncrypt(String.valueOf(roleid));
        map.put("roleid", String.valueOf(roleid));
//        accessToken = AESUtils.aesEncrypt(accessToken);
        map.put("accessToken",accessToken);
        Map<String,String> menu = new HashMap<>();
        if (roleid!=1){
            map.put("path","/insider");
        }else {
            map.put("path","/user");
        }
        String encryptuserName = userName;
//        encryptuserName = AESUtils.aesEncrypt(encryptuserName);
        map.put("username",encryptuserName);

//        String strUid = AESUtils.aesEncrypt(user1.getUid().toString());
        map.put("uid",user1.getUid().toString());
//        String realname = AESUtils.aesEncrypt(userService.selectUserByUsername(userName1).getUrealname());
        map.put("urealname",userService.selectUserByUsername(userName).getUrealname());

        // 判断登录用户角色: 分别存在redis数据库中,用来后续统计
        if (user1.getUroleId()==3){
            stringRedisTemplate.opsForValue().set("kefu:login:"+user1.getUid().toString(),String.valueOf(System.currentTimeMillis()),8,TimeUnit.HOURS);
        }

        ResponseUtil.out(response, ResUtil.getJsonStr(ResultCode.OK,"登录成功",map));
    }



}
