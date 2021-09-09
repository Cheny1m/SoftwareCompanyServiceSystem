package cn.edu.sicnu.cs.controller;

import cn.edu.sicnu.cs.anotations.NewLog;
import cn.edu.sicnu.cs.constant.LogConstant;
import cn.edu.sicnu.cs.constant.ResultCode;
import cn.edu.sicnu.cs.model.Metaoperation;
import cn.edu.sicnu.cs.model.Prigroup;
import cn.edu.sicnu.cs.service.MetaOperationService;
import cn.edu.sicnu.cs.service.PriGroupRelationService;
import cn.edu.sicnu.cs.service.PrigroupService;
import cn.edu.sicnu.cs.utils.ResUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Classname PrivGroupController
 * @Description TODO
 * @Date 2020/11/22 14:51
 * @Created by Huan
 */
@RestController
@Api(tags = "权限组操作")
public class PrivGroupController {

    private static final Logger logger  = LoggerFactory.getLogger(PrivGroupController.class);

    @Autowired
    PrigroupService prigroupService;

    @Autowired
    MetaOperationService metaOperationService;

    @Autowired
    PriGroupRelationService priGroupRelationService;

    /**
     * 查询权限组详细信息
     * @param pgid
     * @return
     */
    @NewLog(value = "查询权限组详细信息",type = LogConstant.OPERATION)
    @GetMapping("${soft_version}/privgroup/{privgroupid}")
    @ApiOperation(value = "查询权限组详细信息")
    public String selectPrivGroup(@PathVariable("privgroupid") Integer pgid){
        if (pgid!=null&&pgid!=0){
            Prigroup prigroup = prigroupService.selectByPrimaryKey(pgid);
            if (prigroup!=null){
                return ResUtil.getJsonStr(ResultCode.OK,"查询权限组成功",prigroup);
            }
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST,"此权限组不存在");
        }
        return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "请求错误");
    }

    /**
     * 删除权限组
     * @param pgid
     * @return
     */
    @NewLog(value = "删除权限组",type = LogConstant.OPERATION)
    @DeleteMapping("${soft_version}/privgroup/{privgroupid}")
    @ApiOperation(value = "删除权限组")
    public String deletePrivGroup(@PathVariable("privgroupid") Integer pgid){
        if (pgid!=null&&pgid!=0){
            Prigroup prigroup = prigroupService.selectByPrimaryKey(pgid);
            if (prigroup!=null){
                int i = prigroupService.deleteByPrimaryKey(pgid);
                if (i>=1){
                    return ResUtil.getJsonStr(ResultCode.OK,"删除权限组成功");
                }
                return ResUtil.getJsonStr(ResultCode.INTERNAL_SERVER_ERROR,"删除权限组失败");
            }
            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST,"此权限组不存在");
        }
        return ResUtil.getJsonStr(ResultCode.BAD_REQUEST, "请求错误");
    }

    /**
     *     添加权限组
     * @param request
     * @return
     * @throws IOException
     */
    @NewLog(value = "添加权限组",type = LogConstant.OPERATION)
    @PostMapping("${soft_version}/privgroup")
    @ApiOperation(value = "添加权限组")
    public String addPrivGroup(HttpServletRequest request) throws IOException {

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String prigroupname = null, prigroupdesc = null;
        if (StringUtils.hasText(body)){
            JSONObject jsonObject = JSON.parseObject(body);
            prigroupname = jsonObject.getString("prigroupname");
            prigroupdesc = jsonObject.getString("prigroupdesc");
        }
        if (StringUtils.hasText(prigroupname)){
            if (prigroupdesc.isEmpty()){
                prigroupdesc="";
            }
            Prigroup prigroup = new Prigroup(prigroupname,prigroupdesc);
            int insert = prigroupService.insert(prigroup);
            if (insert>=1){
                Prigroup prigroup1 = prigroupService.selectByPrivName(prigroupname);
                return ResUtil.getJsonStr(ResultCode.OK,"添加权限组成功",prigroup1);
            }
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING,"必要参数不正确");

    }

    /**
     * 更新权限组相关信息
     * @param request
     * @param privgroupid
     * @return
     * @throws IOException
     */
    @NewLog(value = "更新权限组相关信息",type = LogConstant.OPERATION)
    @PutMapping("${soft_version}/privgroup/{privgroupid}")
    @ApiOperation(value = "更新权限组相关信息")
    public String updatePrivGroup(HttpServletRequest request,@PathVariable("privgroupid") Integer privgroupid) throws IOException {

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        String prigroupname = null, prigroupdesc = null;
        if (StringUtils.hasText(body)){
            JSONObject jsonObject = JSON.parseObject(body);
            prigroupname = jsonObject.getString("prigroupname");
            prigroupdesc = jsonObject.getString("prigroupdesc");
        }
        if (StringUtils.hasText(prigroupname)||StringUtils.hasText(prigroupdesc)){
            Prigroup prigroup = new Prigroup();
            prigroup.setPrigroupdesc(prigroupdesc);
            prigroup.setPrigroupname(prigroupname);
            prigroup.setPgid(privgroupid);
            if (StringUtils.hasText(prigroupname)&&prigroupService.selectByPrivName(prigroupname)!=null){
                return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST,"此权限组名已经存在");
            }
            int insert = prigroupService.updateByPrimaryKey(prigroup);
            if (insert>=1){
                Prigroup prigroup1 = prigroupService.selectByPrivName(prigroupname);
                return ResUtil.getJsonStr(ResultCode.OK,"成功修改权限组信息",prigroup1);
            }
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING,"请求参数不能为空");

    }

    /**
     * 添加权限到权限组
     * @param privgroupid
     * @param privid
     * @return
     * @throws IOException
     */
    @NewLog(value = "添加权限到权限组",type = LogConstant.OPERATION)
    @PostMapping("${soft_version}/privgroup/{privgroupid}/priv/{privid}")
    @ApiOperation(value = "添加权限到权限组")
    public String addPrivToPrivGroup(@PathVariable("privgroupid") Integer privgroupid,
                                     @PathVariable("privid") Integer privid) throws IOException {
        if (privgroupid != null && privgroupid != 0 && privid != null && privid != 0) {

            // 判断角色是否存在
            Prigroup prigroup = prigroupService.selectByPrimaryKey(privgroupid);
            if (prigroup == null) {
                return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "此权限组不存在");
            }
            // 判断权限是否存在
            Metaoperation metaoperation = metaOperationService.selectByPrimaryKey(privid);
            if (metaoperation != null) {
                if(priGroupRelationService.selectIsExistByPidAndPriGroupId(privid, privgroupid)){
                    return ResUtil.getJsonStr(ResultCode.COMMIT_RESOURCE_HAD_EXIST, "此权限组已经拥有此权限");
                }

                int insert = priGroupRelationService.insert(privgroupid, privid);
                if (insert >= 1) {
                    return ResUtil.getJsonStr(ResultCode.OK, "给权限组添加权限成功");
                } else ResUtil.getJsonStr(ResultCode.INTERNAL_SERVER_ERROR, "请求错误");
            } else return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "添加的权限不存在");

//            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "此角色不存在");
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "必要参数不正确");
    }

    /**
     * 删除权限组的某个权限
     * @param privgroupid
     * @param privid
     * @return
     * @throws IOException
     */
    @NewLog(value = "删除权限组的某个权限",type = LogConstant.OPERATION)
    @DeleteMapping("${soft_version}/privgroup/{privgroupid}/priv/{privid}")
    @ApiOperation(value = "删除权限组的某个权限")
    public String deletePrivToPrivGroup(@PathVariable("privgroupid") Integer privgroupid,
                                     @PathVariable("privid") Integer privid) throws IOException {
        if (privgroupid != null && privgroupid != 0 && privid != null && privid != 0) {

            // 判断权限组是否存在
            Prigroup prigroup = prigroupService.selectByPrimaryKey(privgroupid);
            if (prigroup == null) {
                return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "此权限组不存在");
            }
            // 判断权限是否存在
            if (priGroupRelationService.selectIsExistByPidAndPriGroupId(privid, privgroupid)) {
                // 为角色添加权限
                int i = priGroupRelationService.deleteByPrimaryKey(privid, privgroupid);
                if (i >= 1) {
                    return ResUtil.getJsonStr(ResultCode.OK, "将某权限从某权限组组中删除成功");
                } else ResUtil.getJsonStr(ResultCode.INTERNAL_SERVER_ERROR, "请求错误");
            } else return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "此权限组中没有此权限");

//            return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "此角色不存在");
        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "必要参数不正确");
    }

    /**
     * 查询权限组拥有的所有权限
     * @param privgroupid
     * @return
     * @throws IOException
     */
    @NewLog(value = "查询权限组拥有的所有权限",type = LogConstant.OPERATION)
    @GetMapping("${soft_version}/privgroup/{privgroupid}/_privs")
    @ApiOperation(value = "查询权限组拥有的所有权限")
    public String selectPrivsFromPrivGroup(@PathVariable("privgroupid") Integer privgroupid) throws IOException {
        if (privgroupid != null && privgroupid != 0) {

            // 判断角色组是否存在
            Prigroup prigroup = prigroupService.selectByPrimaryKey(privgroupid);
            if (prigroup == null) {
                return ResUtil.getJsonStr(ResultCode.RESOURCE_NOT_EXIST, "此权限组不存在");
            }
            List<Metaoperation> metaoperations = prigroupService.selectPrivilegesByPrimaryKey(privgroupid);

            return ResUtil.getJsonStr(ResultCode.OK,"查询权限组所有权限成功",metaoperations);

        }
        return ResUtil.getJsonStr(ResultCode.NECESSARY_PARAMETER_NOT_NULL_OR_NOTIING, "必要参数不正确");
    }


}
