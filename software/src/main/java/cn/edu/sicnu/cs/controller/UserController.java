package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.anotations.Log;
import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.AliyunConstant;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.constant.ResultCode;
import cn.edu.sicnu.cs.dto.*;
import cn.edu.sicnu.cs.service.*;
import cn.edu.sicnu.cs.utils.*;
import cn.edu.sicnu.cs.validation.Create;
import cn.edu.sicnu.cs.validation.Select;
import cn.edu.sicnu.cs.validation.Update;
import cn.edu.sicnu.cs.vo.NavigationBarVo;
import cn.edu.sicnu.cs.vo.NavigationBarChilrenVo;
import cn.edu.sicnu.cs.model.Metaoperation;
import cn.edu.sicnu.cs.model.Prigroup;
import cn.edu.sicnu.cs.model.Role;
import cn.edu.sicnu.cs.model.User;
import cn.edu.sicnu.cs.pojo.*;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.licoy.encryptbody.annotation.encrypt.AESEncryptBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.rest.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.CharArrayReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Classname User
 * @Description TODO
 * @Date 2020/11/15 16:45
 * @Created by Huan
 */
@RestController
@Api(value = "用户操作", tags = "用户操作", description = "系统用户接口")
@Slf4j
//@AESEncryptBody
@Validated
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PrigroupService prigroupService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    MetaOperationService metaOperationService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RolePrivService rolePrivService;


    /**
     * 更改用户密码 要做 ------------------------------->>>>>>>>>>>>>>>>>>>>>
     *
     * @param uid
     * @param request
     * @return
     * @throws IOException
     */
    @NewLog(value = "更新用户信息", type = LogConstant.OPERATION)
    @PutMapping("${soft_version}/user/{uid}")
    @ApiOperation(value = "更新用户信息")
    public String updateUser(@PathVariable Integer uid, HttpServletRequest request) throws IOException {

        User user1 = userService.selectUserByUid(uid);


        if (!SecurityUtils.checkRequestIsLegal(user1.getUsername())) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "请求非法");
        }

        /**
         * 更改用户密码
         */

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String username = null, urealname = null, ugender = null,
                uemail = null, umobile = null, oldpassword = null, newpassword = null;
        if (StringUtils.hasText(body)) {
            JSONObject jsonObject = JSON.parseObject(body);
            oldpassword = jsonObject.getString("oldpassword");
            newpassword = jsonObject.getString("newpassword");
        }
        // 判断新旧密码正确性
        if (!StringUtils.hasText(oldpassword) || !StringUtils.hasText("newpassword")) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "新旧密码不能为空");
        }
        if (!ReexParamUtils.IsPassword(oldpassword) || !ReexParamUtils.IsPassword(newpassword)) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "密码格式错误");
        }

        User user = userService.selectUserByUid(uid);
        if (user != null) {
            if (passwordEncoder.matches(oldpassword, user.getPassword())) {
                int i = 0;
                try {
                    i = userService.updatePasswordByUid(uid, oldpassword, newpassword);
                } catch (SQLIntegrityConstraintViolationException throwables) {
                    throwables.printStackTrace();
                    return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "更新密码失败");
                }
                if (i >= 1) {
                    return ResUtil.getJsonStr(ResultCode.OK, "修改密码成功");
                } else {
                    return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "更新密码失败");
                }
            } else {
                return ResUtil.getJsonStr(250, "旧密码不正确");
            }
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户不存在");

    }

    /**
     * 管理员更改其他用户信息   需要管理员密码
     *
     * @param uid
     * @param request
     * @return
     * @throws IOException
     */
    @NewLog(value = "管理员修改系统用户信息", type = LogConstant.OPERATION)
    @PutMapping("${soft_version}/admin/{uid}")
    @ApiOperation(value = "管理员修改系统用户信息")
    public String updateUserByAdmin(@PathVariable Integer uid, HttpServletRequest request) throws IOException {

        /**
         * 更改用户密码
         */

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String username = null, urealname = null, ugender = null,
                uemail = null, umobile = null, adminPassword = null;
        if (StringUtils.hasText(body)) {
            JSONObject jsonObject = JSON.parseObject(body);
            username = jsonObject.getString("username");
            urealname = jsonObject.getString("urealname");
            ugender = jsonObject.getString("ugender");
            uemail = jsonObject.getString("uemail");
            umobile = jsonObject.getString("umobile");
            adminPassword = jsonObject.getString("adminpassword");
        }

//        if (StringUtils.hasText(oldpassword)&&StringUtils.hasText("newpassword")){
//            oldpassword=passwordEncoder.encode(oldpassword);
//            newpassword=passwordEncoder.encode(newpassword);
//            try {
//                int i = userService.updatePasswordByUid(uid, oldpassword, newpassword);
//                if (i>=1){
//                    return ResUtil.getJsonStr(ResultCode.OK,"修改密码成功");
//                }else return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "旧密码不正确");
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "旧密码不正确");
//            }
//
//        }else if (StringUtils.hasText(oldpassword)||StringUtils.hasText(newpassword)||
//                (StringUtils.isEmpty(oldpassword)&&StringUtils.hasText(newpassword))||
//                (StringUtils.isEmpty(newpassword)&&StringUtils.hasText(oldpassword))){
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR,"修改密码时字段必须有新旧密码");
//        }else{
//            oldpassword=null;
//            newpassword=null;
//        }

        if (passwordEncoder.matches(adminPassword, userService.selectUserByUsername("admin").getPassword())) {
            if (!StringUtils.isEmpty(username) || !StringUtils.isEmpty(urealname)
                    || !StringUtils.isEmpty(ugender) || !StringUtils.isEmpty(uemail)
                    || !StringUtils.isEmpty(umobile)) {
                User user = new User(username, urealname, ugender, uemail, umobile);
                user.setUid(uid);
                try {
                    int i = userService.updateUserByUid(uid, user);
                    if (i == 0) {
                        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
                    } else {
                        return ResUtil.getJsonStr(ResultCode.OK, "更新成功");
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                    ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
                }

            }
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");

        } else {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "管理员密码错误");
        }


    }

//    /**
//     * 编辑用户信息(用户自己修改)
//     *
//     * @param uid
//     * @param request
//     * @return
//     * @throws IOException
//     */
//    @NewLog(value = "编辑用户信息(用户自己修改)", type = LogConstant.OPERATION)
//    @PutMapping("${soft_version}/user/self/{uid}")
//    @ApiOperation(value = "编辑用户信息", notes = "用户自己修改")
//    public String updateUserByUid(@PathVariable Integer uid, HttpServletRequest request) throws IOException {
//
//        User user1 = userService.selectUserByUid(uid);
//
//        if (!SecurityUtils.checkRequestIsLegal(user1.getUsername())) {
//            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "请求非法");
//        }
//
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        String username = null, urealname = null, ugender = null,
//                uemail = null, umobile = null, adminPassword = null;
//        if (StringUtils.hasText(body)) {
//            JSONObject jsonObject = JSON.parseObject(body);
//            username = jsonObject.getString("username");
//            urealname = jsonObject.getString("urealname");
//            ugender = jsonObject.getString("ugender");
//            uemail = jsonObject.getString("uemail");
//            umobile = jsonObject.getString("umobile");
//        }
//        username = StringUtils.hasText(username) ? username : null;
//        urealname = StringUtils.hasText(urealname) ? urealname : null;
//        ugender = StringUtils.hasText(ugender) ? ugender : null;
//        uemail = StringUtils.hasText(uemail) ? uemail : null;
//        umobile = StringUtils.hasText(umobile) ? umobile : null;
//        if (SecurityUtils.getCurrentUserId() == (long) uid) {
//            if (!StringUtils.hasText(username) || !StringUtils.isEmpty(urealname)
//                    || !StringUtils.isEmpty(ugender) || !StringUtils.isEmpty(uemail)
//                    || !StringUtils.isEmpty(umobile)) {
//                User user = new User(username, urealname, ugender, uemail, umobile);
//                user.setUid(uid);
//                try {
//                    int i = userService.updateUserByUid(uid, user);
//                    if (i == 0) {
//                        log.info(uid.toString() + "更新失败,传入参数错误");
//                        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
//                    } else {
//                        return ResUtil.getJsonStr(ResultCode.OK, "更新成功");
//                    }
//                } catch (Exception e) {
//                    log.info(uid.toString() + "更新失败,传入参数错误");
//                    ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
//                }
//
//            }
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
//        }
//        return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "非法操作");
//    }

    /**
     * 编辑用户信息(用户自己修改)
     *
     * @param uid
     * @param user
     * @return
     * @throws IOException
     */
    @NewLog(value = "编辑用户信息(用户自己修改)", type = LogConstant.OPERATION)
    @PostMapping("${soft_version}/user/self/{uid}")
    @ApiOperation(value = "编辑用户信息", notes = "用户自己修改")
    public String updateUserByUid(@PathVariable Integer uid,@RequestBody @Validated(Update.class) UserDto user,BindingResult bindingResult) throws IOException {

        validData(bindingResult);

        User user1 = userService.selectUserByUid(uid);

        if (!SecurityUtils.checkRequestIsLegal(user1.getUsername())) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "请求非法");
        }
        if (!passwordEncoder.matches(user.getPassword(),user1.getPassword())) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "用户密码不正确");
        }
        if (user.getPassword_new() != null && user.getPassword_new_two() != null){
            if (Objects.equals(user.getPassword_new(),user.getPassword_new_two())){
                user1.setPassword(passwordEncoder.encode(user.getPassword_new()));
                user1.setUrealname(user.getUrealname());
                user1.setUgender(user.getUgender());
                user1.setUsername(user.getUsername());
                user1.setUmobile(user.getMobile());
                user1.setUemail(user.getEmail());
                try {
                    int i = userService.updateUserByUid(uid, user1);
                    if (i == 0) {
                        log.info(uid.toString() + "更新失败,传入参数错误");
                        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
                    } else {
                        return ResUtil.getJsonStr(201, "更新成功");
                    }
                } catch (Exception e) {
                    log.info(uid.toString() + "更新失败,传入参数错误");
                    ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
                }
            }
        }
        user1.setUrealname(user.getUrealname());
        user1.setUgender(user.getUgender());
        user1.setUsername(user.getUsername());
        user1.setUmobile(user.getMobile());
        user1.setUemail(user.getEmail());
        try {
            int i = userService.updateUserByUid(uid, user1);
            if (i == 0) {
                log.info(uid.toString() + "更新失败,传入参数错误");
                return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
            } else {
                return ResUtil.getJsonStr(ResultCode.OK, "更新成功");
            }
        } catch (Exception e) {
            log.info(uid.toString() + "更新失败,传入参数错误");
            ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
        }

    return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
}


//    @NewLog(value = "新增外部用户",type = LogConstant.OPERATION)
//    @PostMapping("${soft_version}/user")
//    @ApiOperation(value = "add_user",tags = "user",notes = "添加用户")
//    public String addUser(HttpServletRequest request) throws IOException {
//
//        /**
//         * 唯一主键的都需要判定
//         */
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        String username = null,urealname = null,ugender = null,
//                uemail = null,umobile = null,password = null,
//                uroleId = null;
//        if (StringUtils.hasText(body)){
//            JSONObject jsonObject = JSON.parseObject(body);
//            username = jsonObject.getString("username");
//            urealname = jsonObject.getString("urealname");
//            ugender = jsonObject.getString("ugender");
//            uemail = jsonObject.getString("uemail");
//            umobile = jsonObject.getString("umobile");
//            password = jsonObject.getString("password");
//            uroleId = jsonObject.getString("uroleId");
//        }
//        ugender="f";
//        uroleId="1";
//        if (StringUtils.isEmpty(username)||StringUtils.isEmpty(urealname)||
//                StringUtils.isEmpty(ugender)||StringUtils.isEmpty(uemail)||
//                StringUtils.isEmpty(umobile)||StringUtils.isEmpty(password)||
//                StringUtils.isEmpty(uroleId)){
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING,"创建用户时必要参数不能为空");
//        }
//
//        if (StringUtils.hasText(ugender)&&!(ugender.toLowerCase().equals("f")||ugender.toLowerCase().equals("m"))){
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "性别必须为f或者m");
//        }
//        if (StringUtils.hasText(ugender)){
//            ugender=ugender.toLowerCase();
//        }
//        if (StringUtils.hasText(username)){
//            User user = userService.selectUserByUsername(username);
//            if (user!=null){
//                return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "用户名已经存在");
//            }
//        }
//        if (umobile.toCharArray().length!=11){
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "手机号格式不正确");
//        }
//        Role role = roleService.selectByPrimaryKey(Integer.parseInt(uroleId));
//        if (role==null){
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "赋予用户的角色不存在");
//        }
//
//        User user = new User(username,urealname,ugender,uemail,umobile);
//        user.setUroleId(Integer.valueOf(uroleId));
//        user.setPassword(passwordEncoder.encode(password));
//        user.setUvisits(0);
//        user.setUlocked("0");
//        user.setUdeleted("0");
//
//        System.out.println("--------------->"+user);
//
//        try{
//            int i = userService.insertUser(user);
//            if (i == 0){
//                return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "创建用户,传入参数错误");
//            }else{
//                User user1 = userService.selectUserByUsername(username);
//                user1.setPassword(null);
//                return ResUtil.getJsonStr(ResultCode.OK, "创建用户成功",user1);
//            }
//        }catch (Exception e){
////            logger
//            return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "用户名或者手机号已经存在");
//        }
//
//    }
//

//    /**
//     * 新增外部用户
//     *
//     * @param request
//     * @return
//     * @throws IOException
//     */
//    @NewLog(value = "新增外部用户", type = LogConstant.OPERATION)
//    @PostMapping("/${soft_version}/user/create")
//    @ApiOperation(value = "新增外部用户")
//    public String addUser(HttpServletRequest request) throws IOException {
//
//        /**
//         * 唯一主键的都需要判定
//         */
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        String username = null,
//                uemail = null, umobile = null, password = null,
//                companyId = null;
//        if (StringUtils.hasText(body)) {
//            JSONObject jsonObject = JSON.parseObject(body);
//            username = jsonObject.getString("username");
//            companyId = jsonObject.getString("cateinfo");
//            uemail = jsonObject.getString("email");
//            umobile = jsonObject.getString("phone");
//            password = jsonObject.getString("password");
//            String code = jsonObject.getString("code");
//            System.out.println(username + "  " + companyId + "  " + uemail + "  " + umobile + "  " + password + "  " + code);
//            if (code == null ||
//                    !Objects.equals(code, stringRedisTemplate.opsForValue().get("register" + umobile))) {
//                System.out.println(stringRedisTemplate.opsForValue().get("register" + umobile));
//                return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "验证码不能为空!");
//            }
//        }
//        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(uemail) ||
//                StringUtils.isEmpty(umobile) || StringUtils.isEmpty(password)) {
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "创建用户时必要参数不能为空");
//        }
////
////        if (StringUtils.hasText(ugender)&&!("f".equals(ugender.toLowerCase())|| "m".equals(ugender.toLowerCase()))){
////            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "性别必须为f或者m");
////        }
////        if (StringUtils.hasText(ugender)){
////            ugender=ugender.toLowerCase();
////        }
//        if (StringUtils.hasText(username)) {
//            User user = userService.selectUserByUsername(username);
//            if (user != null) {
//                return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "用户名已经存在");
//            }
//        }
//        if (!PhoneFormatCheckUtils.isPhoneLegal(umobile)) {
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "手机号格式不正确");
//        }
//
//        User user = new User();
//        user.setUsername(username);
//        user.setUmobile(umobile);
//        user.setUemail(uemail);
//        user.setUcompanyId(Integer.valueOf(companyId));
//        user.setUroleId(1);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setUvisits(0);
//        user.setUlocked("0");
//        user.setUdeleted("0");
//
//        System.out.println("--------------->" + user);
//
//        try {
//            int i = userService.insertUser(user);
//            if (i == 0) {
//                return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "创建用户,传入参数错误");
//            } else {
//                User user1 = userService.selectUserByUsername(username);
//                user1.setPassword(null);
//                return ResUtil.getJsonStr(ResultCode.OK, "创建用户成功", user1);
//            }
//        } catch (Exception e) {
////            logger
//            return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "用户名或者手机号已经存在");
//        }
//
//    }

    /**
     * 新增外部用户
     *
     * @param euser
     * @return
     * @throws IOException
     */
    @NewLog(value = "新增外部用户", type = LogConstant.OPERATION)
    @PostMapping("/${soft_version}/user/create")
    @ApiOperation(value = "新增外部用户")
    public String addUser(@RequestBody @Validated(Create.class) ExternalUser euser) throws IOException {

        User user = userService.selectUserByUsername(euser.getUsername());
        if (user != null) {
            return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "用户名已经存在");
        }
        User user1 = new User(euser.getUsername(),euser.getPhone(),euser.getEmail(), euser.getCateinfo(),
                1,passwordEncoder.encode(euser.getPassword()),0,"0","0");
        try {
            int i = userService.insertUser(user);
            if (i == 0) {
                return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "创建用户,传入参数错误");
            } else {
                User user2 = userService.selectUserByUsername(user1.getUsername());
                user2.setPassword(null);
                return ResUtil.getJsonStr(ResultCode.OK, "创建用户成功", user1);
            }
        } catch (Exception e) {
            return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "创建错误");
        }

    }


    /**
     * 新增系统内部用户
     *
     * @param request
     * @return
     * @throws IOException
     */
    @NewLog(value = "新增系统内部用户", type = LogConstant.OPERATION)
    @PostMapping("/${soft_version}/user/createSysUser")
    @ApiOperation(value = "新增系统内部用户")
    public String addSysUser(HttpServletRequest request) throws IOException {

        // 唯一主键的都需要判定
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String username = null,
                uemail = null, umobile = null, password = null,
                companyId = null, adminpassword = null, ugender = null;
        String realname = null, repassword = null, rid = null;
        if (StringUtils.hasText(body)) {
            JSONObject jsonObject = JSON.parseObject(body);
            username = jsonObject.getString("username");
            password = jsonObject.getString("password");
            realname = jsonObject.getString("realname");
            repassword = jsonObject.getString("repassword");
            rid = jsonObject.getString("id");
            uemail = jsonObject.getString("uemail");
            ugender = jsonObject.getString("ugender");
            umobile = jsonObject.getString("umobile");
            adminpassword = jsonObject.getString("adminpassword");
        }

        System.out.println("password:" + password + "email: " + uemail);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(realname) || StringUtils.isEmpty(repassword)
                || StringUtils.isEmpty(rid) || StringUtils.isEmpty(uemail) ||
                StringUtils.isEmpty(ugender) || StringUtils.isEmpty(umobile) ||
                StringUtils.isEmpty(adminpassword)) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "创建用户时必要参数不能为空");
        }

        if (StringUtils.hasText(username)) {
            User user = userService.selectUserByUsername(username);
            if (user != null) {
                return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "用户名已经存在");
            }
        }
        if (!PhoneFormatCheckUtils.isPhoneLegal(umobile)) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "手机号格式不正确");
        }
        if (!Objects.equals(password, repassword)) {
            return ResUtil.getJsonStr(250, "两次密码不相同");
        }
        if (!ReexParamUtils.IsPassword(password)) {
            return ResUtil.getJsonStr(251, "密码不符合规则!");
        }

        if (!ReexParamUtils.isEmail(uemail)) {
            return ResUtil.getJsonStr(252, "邮箱不符合规则");
        }

        User user2 = userService.selectUserByUid(1);
        if (!passwordEncoder.matches(adminpassword, user2.getPassword())) {
            return ResUtil.getJsonStr(253, "管理员密码错误!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setUrealname(realname);
        user.setUroleId(Integer.valueOf(rid));
        user.setUmobile(umobile);
        user.setUemail(uemail);
        user.setUgender(ugender);

        user.setUvisits(0);
        user.setUlocked("0");
        user.setUdeleted("0");

        System.out.println("--------------->" + user);

        try {
            int i = userService.insertUser(user);
            System.out.println(i);
            if (i == 0) {
                return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "创建用户,传入参数错误");
            } else if (i == 1) {
                User user1 = userService.selectUserByUsername(username);
                user1.setPassword(null);
                return ResUtil.getJsonStr(ResultCode.OK, "创建用户成功", user1);
            }
        } catch (Exception e) {
            return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "用户名或者手机号已经存在");
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "创建用户,传入参数错误");
    }


    /**
     * 删除用户
     *
     * @param uid
     * @return
     * @throws IOException
     */
    @NewLog(value = "删除用户", type = LogConstant.OPERATION)
    @DeleteMapping("${soft_version}/user/{uid}")
    @ApiOperation(value = "删除用户")
    public String deleteUser(@PathVariable("uid") @NotNull @Min(1) Integer uid) throws IOException {
        if (uid == null || uid == 0) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id参数不正确");
        }
        User user = userService.selectUserByUid(uid);
        if (user == null) {
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 " + uid + " 的用户");
        }
        int i = 0;
        try {
            i = userService.deleteUserByUid(uid);
        } catch (SQLIntegrityConstraintViolationException throwables) {
            throwables.printStackTrace();
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "删除失败");
        }
        if (i == 0) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "删除失败");
        } else {
            return ResUtil.getJsonStr(ResultCode.OK, "删除用户成功");
        }
    }

    /**
     * 查询用户
     *
     * @param uid
     * @return
     * @throws IOException
     */
    @NewLog(value = "查询用户", type = LogConstant.OPERATION)
    @GetMapping("${soft_version}/user/{uid}")
    @ApiOperation(value = "查询用户")
    public String selectUser(@PathVariable("uid") @NotNull @Min(1) Integer uid) throws IOException {

        User user1 = userService.selectUserByUid(uid);
        if (!SecurityUtils.checkRequestIsLegal(user1.getUsername())) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "请求非法");
        }
        User user = userService.selectUserByUid(uid);
        if (user == null) {
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 " + uid + " 的用户");
        }
        user.setPassword(null);
        UserDto userDto = new UserDto(user, roleService.selectByPrimaryKey(user.getUroleId()).getRdesc());
        return ResUtil.getJsonStr(ResultCode.OK, "查询用户成功", userDto);
    }

    /**
     * 查询所有系统用户
     *
     * @return
     * @throws IOException
     */
//    @NewLog(value = "查询所有用户",type = LogConstant.OPERATION)
    @NewLog(value = "查询所有系统用户", type = LogConstant.OPERATION)
    @GetMapping("${soft_version}/_users")
    @ApiOperation(value = "查询所有系统用户")
    public String selectAllsysUser() throws IOException {
        List<User> users = userService.selectAllSysUser();
        List<UserDto> userDtos = new ArrayList<>();
        if (users.isEmpty()) {
            return ResUtil.getJsonStr(ResultCode.OK, "查询用户成功", null);
        }
        for (User user : users) {
            user.setPassword(null);
            userDtos.add(new UserDto(user, roleService.selectByPrimaryKey(user.getUroleId()).getRdesc()));
        }
        return ResUtil.getJsonStr(ResultCode.OK, "查询用户成功", userDtos);
    }

    /**
     * 查询用户权限
     *
     * @param uid
     * @return
     * @throws IOException
     */
    @NewLog(value = "查询用户的所有权限", type = LogConstant.OPERATION)
    @GetMapping("${soft_version}/user/{uid}/_privs")
    @ApiOperation(value = "查询用户权限")
    public String selectUserPrivs(@PathVariable("uid") @NotNull @Min(1) Integer uid) throws IOException {
        if (uid == null || uid == 0) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id参数不正确");
        }
        User user = userService.selectUserByUid(uid);
        if (user == null) {
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 " + uid + " 的用户");
        }
        List<Metaoperation> metaoperations = roleService.selectPrivilegesByRid(user.getUroleId());

        return ResUtil.getJsonStr(ResultCode.OK, "查询用户成功", metaoperations);
    }

//    /**
//     * 改变用户角色
//     * @param uid
//     * @param request
//     * @return
//     * @throws IOException
//     */
//    @NewLog(value = "改变用户角色",type = LogConstant.OPERATION)
//    @PutMapping("${soft_version}/user/{uid}/role")
//    @ApiOperation(value = "改变用户角色")
//    public String updateUserRole(@PathVariable Integer uid, HttpServletRequest request) throws IOException {
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        String uroleId = null;
//        if (StringUtils.hasText(body)){
//            JSONObject jsonObject = JSON.parseObject(body);
//            uroleId = jsonObject.getString("rid");
//        }
//        User user = new User();
//
//        if (uroleId==null||uroleId.isEmpty()){
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id不正确");
//        }
//        user.setUroleId(Integer.valueOf(uroleId));
//
//        User user1 = userService.selectUserByUid(uid);
//        if (user1==null){
//            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 "+uid+" 的用户");
//        }
//        try {
//            int i = userService.updateUserByUid(uid, user);
//            if (i == 0){
//                return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
//            }else{
//                User user2 = userService.selectUserByUid(uid);
//                user2.setPassword(null);
//                return ResUtil.getJsonStr(ResultCode.OK, "更新成功",user2);
//            }
//        }catch (Exception e){
//            return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "更新失败,更新内容与其他用户重复");
//        }
//    }


    /**
     * 改变用户角色
     *
     * @param uid
     * @param role
     * @return
     * @throws IOException
     */
    @NewLog(value = "改变用户角色", type = LogConstant.OPERATION)
    @PutMapping("${soft_version}/user/{uid}/role")
    @ApiOperation(value = "改变用户角色")
    public String updateUserRole(@PathVariable @NotNull @Min(1)  Integer uid, @RequestBody @Validated Role role,BindingResult bindingResult) throws IOException {
        validData(bindingResult);
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        String uroleId = null;
//        if (StringUtils.hasText(body)){
//            JSONObject jsonObject = JSON.parseObject(body);
//            uroleId = jsonObject.getString("rid");
//        }
        User user = new User();
        String rid = String.valueOf(role.getRid());
        if (rid == null || rid.isEmpty()) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id不正确");
        }
        user.setUroleId(Integer.valueOf(rid));

        User user1 = userService.selectUserByUid(uid);
        if (user1 == null) {
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 " + uid + " 的用户");
        }
        try {
            int i = userService.updateUserByUid(uid, user);
            if (i == 0) {
                return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "更新失败,传入参数错误");
            } else {
                User user2 = userService.selectUserByUid(uid);
                user2.setPassword(null);
                return ResUtil.getJsonStr(ResultCode.OK, "更新成功", user2);
            }
        } catch (Exception e) {
            return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "更新失败,更新内容与其他用户重复");
        }
    }

    /**
     * 查询用户的一级导航栏
     * @param uid
     * @return
     * @throws IOException
     */
    @GetMapping("${soft_version}/user/{uid}/_navbar")
    @ApiOperation(value = "select_user_privs",tags = {"user","privs"},notes = "查询用户的所有权限")
    @Cacheable(value = "navigationbar",key = "#root.methodName.toString().toString()+'--'+#uid.toString()")
    public String selectUserBar(@PathVariable("uid") @NotNull @Min(1)  Integer uid) throws IOException {
        if (uid==null||uid==0){
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id参数不正确");
        }
        User user = userService.selectUserByUid(uid);
        if (user==null){
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 "+uid+" 的用户");
        }
        List<NavigationBarVo> navigationBarVos = userService.selectNavigationBarByUsername(user.getUsername());

        return ResUtil.getJsonStr(ResultCode.OK, "查询用户成功", navigationBarVos);
    }

//    /**
//     * 查询用户的一级导航栏
//     *
//     * @param uid
//     * @return
//     * @throws IOException
//     */
//    @NewLog(value = "查询用户的一级导航栏", type = LogConstant.OPERATION)
//    @GetMapping("${soft_version}/user/{uid}/_navbar")
//    @ApiOperation(value = "查询用户的一级导航栏")
//    public String selectUserBar(@PathVariable("uid") Integer uid, @RequestParam String addr) throws IOException {
//        // 当传入是二级导航栏的时候,拆为一级导航栏url
//        if (StringUtils.hasText(addr) && StringUtils.startsWithIgnoreCase(addr, "__")) {
//            addr = StrUtil.sub(addr, 1, addr.length() - 1);
//            String[] split = addr.split("/");
//            StringBuilder sb = new StringBuilder("");
//            for (int i = 0; i < split.length - 1; i++) {
//                sb.append("/").append(split[i]);
//            }
//            addr = sb.toString();
//        }
//        if (uid == null || uid == 0) {
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id参数不正确");
//        }
//        User user = userService.selectUserByUid(uid);
//        if (user == null) {
//            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 " + uid + " 的用户");
//        }
//        List<NavigationBarVo> navigationBarVos = userService.selectNavigationBarByUsername(user.getUsername());
//        System.out.println(navigationBarVos);
//        for (NavigationBarVo navigationBarVo : navigationBarVos) {
//            // addr传输过程中为了传输安全,将/变为了.
//            navigationBarVo.setActive(navigationBarVo.getAddr().equals(addr.replace(".", "/")));
//        }
//        return ResUtil.getJsonStr(ResultCode.OK, "查询用户成功", navigationBarVos);
//    }

    /**
     * 查询用户的二级导航栏
     *
     * @param uid
     * @param privid
     * @return
     * @throws IOException
     */
//    @NewLog(value = "查询用户的二级导航栏",type = LogConstant.OPERATION)
    @GetMapping("${soft_version}/user/{uid}/_navbarchildren")
    @ApiOperation(value = "查询用户的二级导航栏")
    @Cacheable(value = "navigationbar",key = "#root.methodName.toString().toString()+'--'+#uid.toString()+'--'+#privid")
    public String selectUserBarChildren(@PathVariable("uid") @NotNull @Min(1)  Integer uid, @RequestParam("privid") @NotBlank String privid) throws IOException {

        /**
         * 唯一主键的都需要判定
         */
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        String privid = null;
//        if (StringUtils.hasText(body)){
//            JSONObject jsonObject = JSON.parseObject(body);
//            privid = jsonObject.getString("privid");
//        }

        if (privid == null || privid.isEmpty()) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "导航栏id不能为空");
        }
        if (uid == null || uid == 0) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id参数不正确");
        }
        User user = userService.selectUserByUid(uid);
        if (user == null) {
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 " + uid + " 的用户");
        }
        List<NavigationBarChilrenVo> navigationBarChilrenVos = userService.selectNavigationBarChildrenByUsername(user.getUroleId(), Integer.valueOf(privid));

        return ResUtil.getJsonStr(ResultCode.OK, "查询二级导航栏成功", navigationBarChilrenVos);
    }

    /**
     * 下一跳url地址
     * @param uid
     * @param addr
     * @return
     */
    @ApiOperation("下一跳url地址")
    @GetMapping("${soft_version}/user/{uid}/_nexturl")
    @Cacheable(value = "navigationbar",key = "#root.methodName.toString().toString()+'--'+#uid.toString()+'--'+#addr")
    public String findNextUrl(@PathVariable("uid") @NotNull @Min(1)  Integer uid, @RequestParam @NotBlank String addr) {
        // 将安全的url转化为正常url
        addr = addr.replace(".", "/");
        // 记录下最开始的导航栏
        System.out.println("-------=="+addr);
        addr = addr.replace("\"","");
        addr = addr.trim();
        String record = addr;
        if (uid == null || uid == 0) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id参数不正确");
        }
        User user = userService.selectUserByUid(uid);
        if (user == null) {
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 " + uid + " 的用户");
        }
        Metaoperation metaoperation = metaOperationService.selectByUrl(addr);
        if (metaoperation != null) {
            List<NavigationBarChilrenVo> navigationBarChilrenVos = rolePrivService.selectNavBarChildrenByRole(user.getUroleId(), metaoperation.getModesc());
            if (!navigationBarChilrenVos.isEmpty()){
                return ResUtil.getJsonStr(ResultCode.OK, "查询二级导航栏成功", navigationBarChilrenVos.get(0).getAddr());
            }
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "一级导航栏url不存在");
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "一级导航栏url不存在");
    }

//    /**
//     * 查询用户的二级导航栏
//     * @param uid
//     * @return
//     * @throws IOException
//     */  //@RequestParam("privid") String privid,
//    @NewLog(value = "查询用户的二级导航栏",type = LogConstant.OPERATION)
//    @GetMapping("${soft_version}/user/{uid}/_navbarchildren")
//    @ApiOperation(value = "查询用户的二级导航栏")
//    public String selectUserBarChildren(@PathVariable("uid") Integer uid,@RequestParam("addr") String addr) throws IOException {
//        // 将安全的url转化为正常url
//        addr =addr.replace(".","/");
//        // 记录下最开始的导航栏
//        String record = addr;
//
//        // 当传入是二级导航栏的时候,拆为一级导航栏
//        if(StringUtils.hasText(addr)&&StringUtils.startsWithIgnoreCase(addr,"__")){
//            addr = StrUtil.sub(addr,1,addr.length()-1);
//            String[] split = addr.split("/");
//            StringBuilder sb = new StringBuilder("");
//            for (int i = 0; i < split.length-1; i++) {
//                sb.append("/").append(split[i]);
//            }
//            addr = sb.toString();
//        }
//
//        if (uid==null||uid==0){
//            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户id参数不正确");
//        }
//        User user = userService.selectUserByUid(uid);
//        if (user==null){
//            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "没有id为 "+uid+" 的用户");
//        }
//        // 标志位,假如有二级导航栏的url匹配上,此时标志位为1,标识所有的active需要重新修改
//        int flag = 0;
//
//        Metaoperation metaoperation = metaOperationService.selectByUrl(addr);
//        if (metaoperation!=null){
//            List<NavigationBarChilrenVo> navigationBarChilrenVos = rolePrivService.selectNavBarChildrenByRole(user.getUroleId(), metaoperation.getModesc());
//            // 修改active
//            if (navigationBarChilrenVos != null&&!navigationBarChilrenVos.isEmpty()) {
//                for (NavigationBarChilrenVo navigationBarChilrenVo : navigationBarChilrenVos) {
//                    if (navigationBarChilrenVo.getAddr().equals(record)) {
//                        if (navigationBarChilrenVos.get(0).getAddr().equals(record)) {
//                        } else {
//                            navigationBarChilrenVos.get(0).setActive(false);
//                            navigationBarChilrenVo.setActive(true);
//                        }
//                    }
//                }
//            }
//            return ResUtil.getJsonStr(ResultCode.OK, "查询二级导航栏成功", navigationBarChilrenVos);
//        }
//        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "一级导航栏url不存在");
////        int flag = 1;
////        for (NavigationBarChilrenVo navigationBarChilrenVo : navigationBarChilrenVos) {
////            navigationBarChilrenVo.setActive(navigationBarChilrenVo.getAddr().startsWith(addr.replace(".","/"))&&flag==1);
////            flag = navigationBarChilrenVo.isActive()?++flag:flag;
////        }
//
//    }

    /**
     * 分页查询所有用户
     *
     * @param pageSize
     * @param pageNum
     * @return
     */
    @ApiOperation("分页查询所有用户")
    @NewLog(value = "分页查询用户", type = LogConstant.OPERATION)
    @GetMapping(value = "/${soft_version}/_users/findpage")
    public Object findPage(@RequestParam("pageSize") @NotNull @Min(0) Integer pageSize, @RequestParam("pageNum") @NotNull @Min(0) Integer pageNum, @RequestParam("blurry") String blurry) {

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (!StringUtils.hasText(blurry)) {
            blurry = "";
        }

        PageResult page = userService.findPage(pageRequest, blurry);
        List<UserDto> userDtos = new ArrayList<>();
        if (page != null) {

            for (Object o : page.getContent()) {
                if (o != null) {
                    o = (User) o;
                    ((User) o).setPassword(null);
                    userDtos.add(new UserDto((User) o, roleService.selectByPrimaryKey(((User) o).getUroleId()).getRdesc()));
                }
            }
        }
        if (page != null) {
            page.setContent(userDtos);
            page.setCode(200);
            return page;
        }
        page = new PageResult();
        page.setCode(400);
        return page;
    }

    /**
     * 按权限组查询所有用户的权限
     *
     * @return
     */
    @ApiOperation("分权限组查询所有用户的权限")
    @NewLog(value = "分权限组查询所有用户的权限", type = LogConstant.OPERATION)
    @GetMapping("/${soft_version}/_users/_privs")
    public String get_All_User_Privs_By_PrivGroup() {

        List<Role> roles = roleService.selectAllRoles();
        for (Role role : roles) {
            String name = role.getRname();
            role.setRname(role.getRdesc());
            role.setRdesc(name);
        }
        List<PrivGroupWithPrivDto> privGroupWithPrivDtos = new ArrayList<>();
        List<RoleWithprivsgroupDto> roleWithprivsgroupDtos = new ArrayList<>();
        List<PrivDto> privDtos = new ArrayList<>();
        List<Prigroup> prigroups = prigroupService.selectAll();

        for (Role role : roles) {
            for (Prigroup prigroup : prigroups) {
                if (prigroup != null && role != null) {
                    List<Metaoperation> metaoperations = prigroupService.selectinaprivgoupprivsbyrole(prigroup.getPgid(), role.getRid());
                    privGroupWithPrivDtos.add(new PrivGroupWithPrivDto(prigroup, metaoperations));
                }
            }
            if (role != null) {
                System.out.println("----------->" + privGroupWithPrivDtos);
                roleWithprivsgroupDtos.add(new RoleWithprivsgroupDto(role, privGroupWithPrivDtos));

            }
            privGroupWithPrivDtos.clear();
        }
        System.out.println("------------->" + roleWithprivsgroupDtos);
        return ResUtil.getJsonStr(ResultCode.OK, "请求成功", roleWithprivsgroupDtos);
    }

    //    @GetMapping("/{soft_version}/_privgroups/_privs")
//    public String get_All_Privgroup_Privs_By_PrivGroup(){
//        List<ReturningPrivGroupWithPriv> returningPrivGroupWithPrivs = new ArrayList<>();
//        List<Prigroup> prigroups = prigroupService.selectAll();
//        for (Prigroup prigroup : prigroups) {
//            if (prigroup!=null){
//                List<Metaoperation> metaoperations = prigroupService.selectPrivilegesByPrimaryKey(prigroup.getPgid());
//                returningPrivGroupWithPrivs.add(new ReturningPrivGroupWithPriv(prigroup,metaoperations));
//            }
//        }
//
//        return ResUtil.getJsonStr(ResultCode.OK, "请求成功",returningPrivGroupWithPrivs);
//    }

    /**
     * 按权限组查询每个权限组拥有的所有权限
     *
     * @return
     */
    @ApiOperation("按权限组查询每个权限组拥有的所有权限")
    @NewLog(value = "按权限组查询每个权限组拥有的所有权限", type = LogConstant.OPERATION)
    @GetMapping("/${soft_version}/_privgroups/_privs")
    public String get_All_Privgroup_Privs_By_PrivGroup() {
        List<PrivGroupWithPrivsGradationalDto> returningPrivGroupWithPrivs = new ArrayList<>();
        List<Prigroup> prigroups = prigroupService.selectAll();
        for (Prigroup prigroup : prigroups) {
            if (prigroup != null) {
                List<PrivGradationalDto> privGradationalDtos = prigroupService.selectAllFourLever(prigroup);
                returningPrivGroupWithPrivs.add(new PrivGroupWithPrivsGradationalDto(prigroup.getPgid(), prigroup.getPrigroupname(), prigroup.getPrigroupdesc(), privGradationalDtos));
            }
        }
        return ResUtil.getJsonStr(ResultCode.OK, "请求成功", returningPrivGroupWithPrivs);
    }

    /**
     * 按角色查询每个角色的权限
     *
     * @return
     */
    @ApiOperation("按角色查询每个角色的权限")
    @NewLog(value = "按角色查询每个角色的权限", type = LogConstant.OPERATION)
    @GetMapping("/${soft_version}/_roles/_groupprivs")
    public String get_all_Privs_Four_Lever_By_PrivGroup() {

        List<Role> roles = roleService.selectAllRoles();
        for (Role role : roles) {
            String name = role.getRname();
            role.setRname(role.getRdesc());
            role.setRdesc(name);
        }
//        List<ReturningPrivGroupWithPrivsFourLever> returningPrivGroupWithPrivs = new ArrayList<>();
        List<RoleWithPrivgroupGradationalDto> returningRoleWithprivsgroups = new ArrayList<>();
        List<PrivGradationalDto> privFourLevels = new ArrayList<>();
        List<PrivDto> privDtos = new ArrayList<>();
        List<Prigroup> prigroups = prigroupService.selectAll();

        for (Role role : roles) {
            List<PrivGroupWithPrivsGradationalDto> returningPrivGroupWithPrivs = new ArrayList<>();
            for (Prigroup prigroup : prigroups) {
                if (prigroup != null && role != null) {
                    List<PrivGradationalDto> privGradationalDtos = metaOperationService.selectPrivFourLeverByRoleAndPrivgroup(role, prigroup);
                    returningPrivGroupWithPrivs.add(new PrivGroupWithPrivsGradationalDto(prigroup, privGradationalDtos));
                }
            }
            if (role != null) {
                System.out.println("----------->" + returningPrivGroupWithPrivs);
                System.out.println(">>--------" + returningRoleWithprivsgroups);
                returningRoleWithprivsgroups.add(new RoleWithPrivgroupGradationalDto(role, returningPrivGroupWithPrivs));
                System.out.println(">>--------" + returningRoleWithprivsgroups);
            }
//            returningPrivGroupWithPrivs.clear();
        }
        System.out.println("------------->" + returningRoleWithprivsgroups);
//
//       return ResUtil.getJsonStr(ResultCode.OK, "请求成功",returningRoleWithprivsgroups);
        if (returningRoleWithprivsgroups != null || !returningRoleWithprivsgroups.isEmpty()) {
            returningRoleWithprivsgroups.remove(0);
        }
        return ResUtil.getJsonStrJackon(ResultCode.OK, "请求成功", returningRoleWithprivsgroups);
    }

    /**
     * 查询用户在某个一级导航栏下的某个二级导航栏页面中的所有权限
     *
     * @param userid
     * @param groupid
     * @param zibiaotiid
     * @return
     */
    @ApiOperation("查询用户在某一级导航栏下的某二级导航栏页面中的所有权限")
    @NewLog(value = "查询用户在某个一级导航栏下的某个二级导航栏页面中的所有权限", type = LogConstant.OPERATION)
    @GetMapping("/${soft_version}/user/{userid}/{groupid}/{zibiaotiid}")
    public String select_user_group_zibiaoti(@PathVariable("userid") @NotNull @Min(1) Integer userid,
                                             @PathVariable("groupid") @NotNull @Min(10000)  Integer groupid,
                                             @PathVariable("zibiaotiid") @NotNull @Min(1)  Integer zibiaotiid) {

        User user = userService.selectUserByUid(userid);

        List<Metaoperation> metaoperations = prigroupService.selectInAPrivGoupprivsByRoleAndFourlever(groupid, user.getUroleId(), zibiaotiid);
        Map<String, Boolean> map = new HashMap<>();
        for (Metaoperation metaoperation : metaoperations) {
            map.put(metaoperation.getModesc(), true);
        }
        return ResUtil.getJsonStr(ResultCode.OK, "查询成功", map);
    }

    /**
     * 获取验证码图片,
     * @param request
     * @param response
     * @param identifier 用户随机标识id
     * @throws Exception
     */
//    @GetMapping("/{soft_version}/login/captcha")
//    public void captcha(HttpServletRequest request, HttpServletResponse response,@RequestParam String identifier) throws Exception {
//
//        // 使用gif验证码
//
//        GifCaptcha gifCaptcha = new GifCaptcha(130,48,6);
//        System.out.println(gifCaptcha.textChar());
//        System.out.println(gifCaptcha.text());
////        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130,48,4);
////        ChineseCaptcha chineseCaptcha = new ChineseCaptcha(130,48,4);
////        ChineseGifCaptcha chineseGifCaptcha = new ChineseGifCaptcha(130,48,4);
//        SpecCaptcha specCaptcha = new SpecCaptcha(130,48,6);
//        if(StringUtils.hasText(identifier)){
//            if (stringRedisTemplate.opsForValue().getOperations().hasKey(identifier)){
//                stringRedisTemplate.delete(identifier);
//            }
//            stringRedisTemplate.opsForValue().set(identifier,gifCaptcha.text().toLowerCase(),120,TimeUnit.SECONDS);
//            CaptchaUtil.out(specCaptcha, request, response);
//        }
//    }

//    @ResponseBody
//    @GetMapping("/{soft_version}/login/captcha")
//    public Object captcha(HttpServletRequest request, HttpServletResponse response,@RequestParam String identifier) throws Exception {
//
//        // 使用gif验证码
////        GifCaptcha gifCaptcha = new GifCaptcha(130,48,6);
////        System.out.println(gifCaptcha.textChar());
////        System.out.println(gifCaptcha.text());
////        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130,48,4);
////        ChineseCaptcha chineseCaptcha = new ChineseCaptcha(130,48,4);
////        ChineseGifCaptcha chineseGifCaptcha = new ChineseGifCaptcha(130,48,4);
//        SpecCaptcha specCaptcha = new SpecCaptcha(130,48,6);
//        System.out.println(specCaptcha.text());
//        if(StringUtils.hasText(identifier)){
//            if (stringRedisTemplate.opsForValue().getOperations().hasKey(identifier)){
//                stringRedisTemplate.delete(identifier);
//            }
//            stringRedisTemplate.opsForValue().set(identifier,specCaptcha.text().toLowerCase(),120,TimeUnit.SECONDS);
//            System.out.println(specCaptcha.toBase64().toString());
//            System.out.println(specCaptcha.toBase64().toString());
//            Map<String,Object> map = new HashMap<>();
//            map.put("code","200");
//            map.put("data",specCaptcha.toBase64().toString());
//            return map;
////            return ResUtil.getJsonStr(ResultCode.OK,"获取验证码成功",specCaptcha.toBase64().toString());
//        }
//        return ResUtil.getJsonStr(ResultCode.BAD_REQUEST,"获取验证码失败");
//    }

    /**
     * 获取图片验证码图片
     * @param identifier 用户唯一标识,前端自动生成发送过来
     * @return 验证码图片base64编码
     * @throws Exception
     */
//    @NewLog(value = "获取图片验证码图片",type = LogConstant.OPERATION)
    @ApiOperation("获取图片验证码图片")
    @GetMapping("/${soft_version}/login1/captcha")
    public String captcha(@RequestParam @NotBlank String identifier) throws Exception {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        String verCode = specCaptcha.text().toLowerCase();
        stringRedisTemplate.opsForValue().set(identifier, specCaptcha.text().toLowerCase(), 125, TimeUnit.SECONDS);
        // 将key和base64返回给前端
        return ResUtil.getJsonStr(200, "image", specCaptcha.toBase64());
    }

//    /**
//     * 验证用户验证码是否正确
//     *
//     * @param request
//     * @return
//     * @throws Exception
//     */
//    @ApiOperation("验证用户验证码是否正确")
//    @PostMapping("/${soft_version}/login1/captcha")
//    public String verify(HttpServletRequest request) throws Exception {
//
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        String identifier = null, code = null;
//        if (StringUtils.hasText(body)) {
//            JSONObject jsonObject = JSON.parseObject(body);
//            identifier = jsonObject.getString("identifier");
//            code = jsonObject.getString("code");
//        }
//        if (!StringUtils.hasText(identifier)||!StringUtils.hasText(code)){
//            return ResUtil.getJson(ResultCode.BAD_REQUEST, "验证码错误");
//        }
//        System.out.println(identifier + "---------" + code);
//
//        Long expire = stringRedisTemplate.opsForValue().getOperations().getExpire(identifier);
//        if (expire == null || expire < 1) {
//            return ResUtil.getJson(700, "验证码超时");
//        }
//        String s = stringRedisTemplate.opsForValue().get(identifier);
//        if (s == null || !Objects.equals(code.toLowerCase(), s)) {
//            return ResUtil.getJson(ResultCode.BAD_REQUEST, "验证码错误");
//        }
//        return ResUtil.getJson(ResultCode.OK, "验证码正确");
//
//    }

    /**
     * 验证用户验证码是否正确
     *
     * @param chapchaDTO
     * @return
     * @throws Exception
     */
    @ApiOperation("验证用户验证码是否正确")
    @PostMapping("/${soft_version}/login1/captcha")
    public String verify(@RequestBody @Validated(Select.class) ChapchaDTO chapchaDTO,BindingResult bindingResult) throws Exception {
        validData(bindingResult);
        String identifier = chapchaDTO.getIdentifier(), code = chapchaDTO.getCode();

        if (!StringUtils.hasText(identifier)||!StringUtils.hasText(code)){
            return ResUtil.getJson(ResultCode.BAD_REQUEST, "验证码错误");
        }
        System.out.println(identifier + "---------" + code);

        Long expire = stringRedisTemplate.opsForValue().getOperations().getExpire(identifier);
        if (expire == null || expire < 1) {
            return ResUtil.getJson(700, "验证码超时");
        }
        String s = stringRedisTemplate.opsForValue().get(identifier);
        if (s == null || !Objects.equals(code.toLowerCase(), s)) {
            return ResUtil.getJson(ResultCode.BAD_REQUEST, "验证码错误");
        }
        return ResUtil.getJson(ResultCode.OK, "验证码正确");

    }

//    /**
//     * 发送短信验证码
//     *
//     * @param request
//     * @return
//     * @throws IOException
//     */
//    @ApiOperation("发送短信验证码")
//    @PostMapping("/${soft_version}/register/captcha")
//    public String registerMobileMessage(HttpServletRequest request) {
//        String body = null;
//        try {
//            body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String mobile = null;
//        if (StringUtils.hasText(body)) {
//            JSONObject jsonObject = JSON.parseObject(body);
//            mobile = jsonObject.getString("mobile");
//        }
//        if (PhoneFormatCheckUtils.isPhoneLegal(mobile)) {
//            String code = String.valueOf(RandomUtil.randomInt(100000, 1000000));
//
//            int i = 0;
//            // 循环发送,发送三次失败则不发送
//            while (i < 3 && MessageUtils.sendMessage(mobile, code) == 0) {
//                i++;
//            }
//            if (i == 3 || i > 3) {
//                return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "发送短信失败");
//            }
//            stringRedisTemplate.opsForValue().set("register:" + mobile, code, 3, TimeUnit.MINUTES);
//            return ResUtil.getJsonStr(ResultCode.OK, "发送短信成功");
//        }
//        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "手机号码不符合标准");
//
//    }

    /**
     * 发送短信验证码
     *
     * @param registerDTO
     * @return
     * @throws IOException
     */
    @ApiOperation("发送短信验证码")
    @PostMapping("/${soft_version}/register/captcha")
    public String registerMobileMessage(@RequestBody @Validated(Create.class) RegisterDTO registerDTO, BindingResult bindingResult) {
        validData(bindingResult);
        String mobile = registerDTO.getMobile();

        if (PhoneFormatCheckUtils.isPhoneLegal(mobile)) {
            String code = String.valueOf(RandomUtil.randomInt(100000, 1000000));

            int i = 0;
            // 循环发送,发送三次失败则不发送
            while (i < 3 && MessageUtils.sendMessage(mobile, code) == 0) {
                i++;
            }
            if (i == 3 || i > 3) {
                return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "发送短信失败");
            }
            stringRedisTemplate.opsForValue().set("register:" + mobile, code, 3, TimeUnit.MINUTES);
            return ResUtil.getJsonStr(ResultCode.OK, "发送短信成功");
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "手机号码不符合标准");

    }

//    /**
//     * 验证短信验证码
//     *
//     * @param request 中间包含两个参数: mobile 手机号 code 验证码
//     * @return
//     * @throws IOException
//     */
////    @NewLog(value = "查询用户名是否存在",type = LogConstant.OPERATION)
//    @ApiOperation("验证短信验证码")
//    @PutMapping("/${soft_version}/register/captcha")
//    public String verifyMobileMessage(HttpServletRequest request) throws IOException {
//        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
//        String mobile = null, code = null;
//        if (StringUtils.hasText(body)) {
//            JSONObject jsonObject = JSON.parseObject(body);
//            mobile = jsonObject.getString("mobile");
//            code = jsonObject.getString("code");
//        }
//        log.info("mobile:" + mobile + " code: " + code);
//        if (PhoneFormatCheckUtils.isPhoneLegal(mobile)) {
//            String s = stringRedisTemplate.opsForValue().get("register:" + mobile);
//            if (Objects.equals(code, s)) {
//                return ResUtil.getJsonStr(ResultCode.OK, "验证成功");
//            }
//            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "验证码错误");
//        }
//        return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "手机号码不符合标准");
//
//    }

    /**
     * 验证短信验证码
     *
     * @param register 中间包含两个参数: mobile 手机号 code 验证码
     * @return
     * @throws IOException
     */
//    @NewLog(value = "查询用户名是否存在",type = LogConstant.OPERATION)
    @ApiOperation("验证短信验证码")
    @PutMapping("/${soft_version}/register/captcha")
    public String verifyMobileMessage(@RequestBody @Validated(Select.class) RegisterDTO register, BindingResult bindingResult) throws IOException {
        validData(bindingResult);
        String mobile = register.getMobile(), code = register.getCode();

        log.info("mobile:" + mobile + " code: " + code);
        if (PhoneFormatCheckUtils.isPhoneLegal(mobile)) {
            String s = stringRedisTemplate.opsForValue().get("register:" + mobile);
            if (Objects.equals(code, s)) {
                return ResUtil.getJsonStr(ResultCode.OK, "验证成功");
            }
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "验证码错误");
        }
        return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "手机号码不符合标准");
    }

    /**
     * 查询用户名是否存在
     *
     * @param username
     * @return
     */
    @NewLog(value = "查询用户名是否存在", type = LogConstant.OPERATION)
    @ApiOperation("查询用户名是否存在")
    @GetMapping("/${soft_version}/user/usernameExist")
    public String findUsernameIsExist(@RequestParam @Pattern(regexp = "^[A-Za-z_@.0-9]{4,16}$") String username) {
        if (StringUtils.hasText(username)) {
            User user = userService.selectUserByUsername(username);
            if (user != null) {
                return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "用户名已经存在");
            }
            return ResUtil.getJsonStr(ResultCode.OK, "用户名不存在");
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "用户名不能为空");
    }

    /**
     * 改变用户角色
     *
     * @param userid
     * @param rid
     * @return
     */
    @GetMapping("/${soft_version}/user/{userid}/role")
    @NewLog(value = "改变用户角色", type = LogConstant.OPERATION)
    @ApiOperation("改变用户角色")
    public String changeUserRole(@PathVariable("userid") @NotNull @Min(1) Integer userid, @NotNull @Min(1) Integer rid) {
        if (rid == null || rid > 99999999 || rid <= 0) {
            return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_TYPE_OR_FORMAT_ERROR, "角色id不正确");
        }
        Role role = roleService.selectByPrimaryKey(rid);
        if (role == null) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "角色不存在");
        }
        User user = userService.selectUserByUid(userid);
        if (user == null) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "用户不存在");
        }
        User user1 = new User();
        user1.setUroleId(rid);
        user1.setUid(userid);
        try {
            userService.updateUserByUid(userid, user1);
            return ResUtil.getJsonStr(ResultCode.OK, "更改用户uid=" + userid + " 角色(" + rid + ")成功");
        } catch (SQLIntegrityConstraintViolationException throwables) {
            throwables.printStackTrace();
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "系统出现错误");
        }


    }

    /**
     * 重置用户密码
     *
     * @param uid
     * @return
     */
    @NewLog(value = "重置用户密码", type = LogConstant.OPERATION)
    @ApiOperation("重置用户密码")
    @PutMapping("${soft_version}/user/reset/{uid}")
    public String resetUserPassword(@PathVariable("uid") Integer uid) {
        User user = userService.selectUserByUid(uid);
        if (user == null || StringUtils.isEmpty(user.getUmobile())) {
            return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "修改密码前请为用户添加手机号");
        }
        String code = RandomUtil.randomString(8);
        String name = StringUtils.hasText(user.getUrealname()) ? user.getUrealname() : user.getUsername();
        String[] params = {code};
        String[] paramName = {"code"};
        int send = 0;
        int nums = 0;
        while (send != 1 && nums < 3) {
            send = MessageUtils.send(AliyunConstant.SIGNNAME, AliyunConstant.CAPTCHA_CODE_TEMPLATE,
                    AliyunConstant.ACCESS_KEY, AliyunConstant.ACCESS_SECRET, 1,
                    paramName, params, user.getUmobile());
            nums++;
        }
        System.out.println("send=" + send + " nums=" + nums);
        if (send != 1) {
            ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "发送失败");
        }
        if (send == 1) {
            try {
                userService.updatePasswordByUid(uid, user.getPassword(), code);
                return ResUtil.getJsonStr(ResultCode.OK, "发送成功");
            } catch (SQLIntegrityConstraintViolationException throwables) {
                throwables.printStackTrace();
                return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "发送失败");
            }

        }
        return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "发送失败");
    }

    private void validData(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuffer sb = new StringBuffer();
            for (ObjectError error : bindingResult.getAllErrors()) {
                sb.append(error.getDefaultMessage());
            }
//            throw new ValidationException(sb.toString());
            throw new ValidationException("传入参数非法");
        }
    }
}
