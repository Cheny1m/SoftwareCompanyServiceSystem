package cn.edu.sicnu.cs.utils;

import cn.edu.sicnu.cs.exception.BadRequestException;
import cn.edu.sicnu.cs.pojo.AuthUserDetails;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 获取当前登录的用户
 */
@Slf4j
public class SecurityUtils {


    /**
     * 获取当前登录的用户
     * @return
     */
    public static UserDetails getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "当前登录状态过期");
        }
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserDetailsService userDetailsService = SpringUtil.getBean(UserDetailsService.class);
            return userDetailsService.loadUserByUsername(userDetails.getUsername());
        }
        throw new BadRequestException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息");
    }



    /**
     * 获取系统用户名称
     * @return 系统用户名称
     */
    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "当前登录状态过期");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println(userDetails);
        String username = authentication.getName();
        if (StringUtils.isEmpty(userDetails.getUsername())){
            return username;
        }
        return userDetails.getUsername();
    }

    /**
     * 获取系统用户ID
     *
     * @return 系统用户ID
     */
    public static Long getCurrentUserId() {
        UserDetails userDetails = getCurrentUser();
        return new JSONObject(new JSONObject(userDetails).get("user")).get("id", Long.class);
    }

//    public static boolean checkRequestIsLegal(int requestId){
////        User user = (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
////        if (user.getId()==requestId) {
////            return true;
////        }
//        return false;
//    }

    public static boolean checkRequestIsLegal(String requestUsername){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getUsername().equals(requestUsername)) {
            return true;
        }
        return false;
    }




}
