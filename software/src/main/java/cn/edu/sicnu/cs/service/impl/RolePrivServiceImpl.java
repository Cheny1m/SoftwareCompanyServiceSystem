package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.dao.RoleprivMapper;
import cn.edu.sicnu.cs.model.Metaoperation;
import cn.edu.sicnu.cs.model.Role;
import cn.edu.sicnu.cs.model.Rolepriv;
import cn.edu.sicnu.cs.model.RoleprivExample;
import cn.edu.sicnu.cs.vo.NavigationBarVo;
import cn.edu.sicnu.cs.vo.NavigationBarChilrenVo;
import cn.edu.sicnu.cs.dto.PrivGradationalDto;
import cn.edu.sicnu.cs.service.MetaOperationService;
import cn.edu.sicnu.cs.service.RolePrivService;
import cn.edu.sicnu.cs.service.RoleService;
import cn.edu.sicnu.cs.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname RolePrivServiceImpl
 * @Description TODO
 * @Date 2020/11/16 16:19
 * @Created by Huan
 */
@Service
public class RolePrivServiceImpl implements RolePrivService {

    private static final Logger logger = LoggerFactory.getLogger(RolePrivServiceImpl.class);

    @Resource
    RoleprivMapper roleprivMapper;

    @Autowired
    MetaOperationService metaOperationService;

    @Autowired
    RoleService roleService;

    @Autowired
    RedisUtils redisUtils;

    @Override
    ////@Cacheable(value = "roleprivsevict",key = "#rid",unless = "#result==null")
    public List<Metaoperation> selectRolePrivsByRid(int rid) {
        RoleprivExample roleprivExample = new RoleprivExample();
        RoleprivExample.Criteria criteria = roleprivExample.createCriteria();
        criteria.andRoleidEqualTo(rid);
        List<Rolepriv> roleprivs = roleprivMapper.selectByExample(roleprivExample);

        logger.debug("角色id "+rid+"  roleprivs: "+roleprivs);

        List<Metaoperation> privileges = new ArrayList<>();
        for (Rolepriv rolepriv : roleprivs) {
            Metaoperation privilege = metaOperationService.selectByPrimaryKey(rolepriv.getPrivilegeid());
            if (privilege!=null){
                privileges.add(privilege);
            }

        }
        logger.debug("角色id为: "+rid+"的角色拥有的权限集合为: privileges: "+privileges);
        return privileges;
    }

    @Override
    ////@Cacheable(value = "roleprivsIntevict",key = "#rid",unless = "#result==null")
    public List<Integer> selectRolePrivsNamesByRid(int rid)
    {
        RoleprivExample roleprivExample = new RoleprivExample();
        RoleprivExample.Criteria criteria = roleprivExample.createCriteria();
        criteria.andRoleidEqualTo(rid);
        List<Rolepriv> roleprivs = roleprivMapper.selectByExample(roleprivExample);
        List<Integer> privilegeNames = new ArrayList<>();
        for (Rolepriv rolepriv : roleprivs) {
            Metaoperation privilege = metaOperationService.selectByPrimaryKey(rolepriv.getPrivilegeid());
            if (privilege!=null){
                privilegeNames.add(privilege.getMoid());
            }

        }
        return privilegeNames;
    }

    @Override
    ////@Cacheable(value = "privrolesevict",key = "#pid",unless = "#result==null")
    public List<Role> selectRolesByPid(int pid) {
        RoleprivExample roleprivExample = new RoleprivExample();
        roleprivExample.createCriteria().andPrivilegeidEqualTo(pid);
        List<Rolepriv> roleprivs = roleprivMapper.selectByExample(roleprivExample);
        List<Role> roles = new ArrayList<>();
        if (!roleprivs.isEmpty()){
            for (Rolepriv rolepriv : roleprivs) {
                Role role = roleService.selectByPrimaryKey(rolepriv.getRoleid());
                if (role!=null){
                    roles.add(role);
                }

            }
            return roles;
        }
        return null;
    }

    @Override
    ////@Cacheable(value = "roleprivs",key = "#root.methodName.toString().toString()+'--'+#rid+'--'+#pid",unless = "#result==null")
    public boolean selectIsExistByRidAndPid(int rid, int pid) {
        RoleprivExample roleprivExample = new RoleprivExample();
        roleprivExample.createCriteria().andRoleidEqualTo(rid).andPrivilegeidEqualTo(pid);
        List<Rolepriv> roleprivs = roleprivMapper.selectByExample(roleprivExample);
        return !roleprivs.isEmpty();
    }

    @Override
    //@Caching(
//            evict = {
//                    //@CacheEvict(value = "navigationbar",allEntries = true),
//                    //@CacheEvict(value = "privsevict",allEntries = true),
//                    //@CacheEvict(value = "privgroupIsExistBypidAndpgroupId",allEntries = true),
//                    //@CacheEvict(value = "roleprivsevict",allEntries = true),
//                    //@CacheEvict(value = "privrolesevict",allEntries = true),
//                    //@CacheEvict(value = "roleprivsIntevict",allEntries = true),
//                    //@CacheEvict(value = "sUserUserpojo",allEntries = true),
//                    //@CacheEvict(value = "operationlist",allEntries = true)
//            }
//    )
    public int insert(int rid, int pid) throws Exception{
        redisUtils.delete("configAttributes:permissions");
        int i = roleprivMapper.insertSelective(new Rolepriv(rid, pid));
        redisUtils.addConfigrationPermissions();
        return i;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "navigationbar",allEntries = true)
//                    @CacheEvict(value = "privsevict",allEntries = true),
//                    @CacheEvict(value = "privgroupIsExistBypidAndpgroupId",allEntries = true),
//                    @CacheEvict(value = "roleprivsevict",allEntries = true),
//                    @CacheEvict(value = "privrolesevict",allEntries = true),
//                    @CacheEvict(value = "roleprivsIntevict",allEntries = true),
//                    @CacheEvict(value = "sUserUserpojo",allEntries = true),
//                    @CacheEvict(value = "operationlist",allEntries = true),
            }
    )
    public int deleteByRidAndPid(int rid, int pid) {

        redisUtils.delete("configAttributes:permissions");
        RoleprivExample roleprivExample = new RoleprivExample();
        roleprivExample.createCriteria().andRoleidEqualTo(rid).andPrivilegeidEqualTo(pid);
        List<Rolepriv> roleprivs = roleprivMapper.selectByExample(roleprivExample);
        return roleprivMapper.deleteByExample(roleprivExample);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "navigationbar",allEntries = true)
                    //@CacheEvict(value = "privsevict",allEntries = true),
                    //@CacheEvict(value = "privgroupIsExistBypidAndpgroupId",allEntries = true),
                    //@CacheEvict(value = "roleprivsevict",allEntries = true),
                    //@CacheEvict(value = "privrolesevict",allEntries = true),
                    //@CacheEvict(value = "roleprivsIntevict",allEntries = true),
                    //@CacheEvict(value = "sUserUserpojo",allEntries = true),
                    //@CacheEvict(value = "operationlist",allEntries = true),
            }
    )
    public int deleteByPid(int pid) {
        redisUtils.delete("configAttributes:permissions");
        RoleprivExample roleprivExample = new RoleprivExample();
        RoleprivExample.Criteria criteria = roleprivExample.createCriteria();
        criteria.andPrivilegeidEqualTo(pid);
        return roleprivMapper.deleteByExample(roleprivExample);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "navigationbar",allEntries = true)
//                    @CacheEvict(value = "privsevict",allEntries = true),
//                    @CacheEvict(value = "privgroupIsExistBypidAndpgroupId",allEntries = true),
//                    @CacheEvict(value = "roleprivsevict",allEntries = true),
//                    @CacheEvict(value = "privrolesevict",allEntries = true),
//                    @CacheEvict(value = "roleprivsIntevict",allEntries = true),
//                    @CacheEvict(value = "sUserUserpojo",allEntries = true),
//                    @CacheEvict(value = "operationlist",allEntries = true),
            }
    )
    public int deleteByRid(int rid) {
        redisUtils.delete("configAttributes:permissions");
        RoleprivExample roleprivExample = new RoleprivExample();
        RoleprivExample.Criteria criteria = roleprivExample.createCriteria();
        criteria.andRoleidEqualTo(rid);
        return roleprivMapper.deleteByExample(roleprivExample);
    }

    // List<Map>

    @Override
    @Cacheable(value = "navigationbar",key = "'selectnavigationbarrole'+#rid",unless = "#result==null")
    public List<NavigationBarVo> selectNavBarByRole(Integer rid) {
        List<Metaoperation> metaoperations = this.selectRolePrivsByRid(rid);
        List<Metaoperation> metaoperations1 = metaOperationService.selectAllHeadNavBar();
        List<NavigationBarVo> navigationBarVoList = new ArrayList<>();

        logger.debug("-----------"+metaoperations+"\n----------"+metaoperations1+"\n----------"+ navigationBarVoList);
        int id =0;

        if (!metaoperations.isEmpty()){
            for (Metaoperation metaoperation : metaoperations) {

                for (Metaoperation metaoperation1 : metaoperations1) {
                    if (metaoperation1.getMoid().equals(metaoperation.getMoid())){
                        navigationBarVoList.add(new NavigationBarVo(++id,metaoperation1.getMoname(),metaoperation1.getMolurl(), id == 1, id != 1));
                    }
                }
            }
            return navigationBarVoList;
        }
    return null;
    }

    @Override
    //@Cacheable(value = "navigationbar",key = "#root.methodName.toString()+'--'+#roleid+'--'+#privname",unless = "#result==null")
    public List<NavigationBarChilrenVo> selectNavBarChildrenByRole(Integer roleid, String privname) {
        List<Metaoperation> metaoperations = this.selectRolePrivsByRid(roleid);
        List<Metaoperation> metaoperations1 = metaOperationService.selectAllChildNavBarByHead(privname);
        List<NavigationBarChilrenVo> navigationBarChilrenVos = new ArrayList<>();
        int id = 0;

        if (!metaoperations.isEmpty()){
            for (Metaoperation metaoperation : metaoperations) {
                if (!metaoperations1.isEmpty()){
                    for (Metaoperation metaoperation1 : metaoperations1) {
                        if (metaoperation1.getMoid().equals(metaoperation.getMoid())){
                            navigationBarChilrenVos.add(new NavigationBarChilrenVo(++id,metaoperation1.getMoname(),metaoperation1.getMolurl(),id==1));

                        }
                    }
                }else{
                    return null;
                }
//                logger.debug("navigationBarChilrens"+navigationBarChilrens);

            }
            return navigationBarChilrenVos;
        }

        return null;
    }

    @Override
//    //@Cacheable(value = "navigationbar",key = "#root.methodName.toString()+'--'+#roleid+'--'+#privname",unless = "#result==null")
    public List<PrivGradationalDto> selectErJiBiaoTiChildrenByRole(Integer roleid, String privname) {
        System.out.println("我进来了");
        List<Metaoperation> metaoperations = this.selectRolePrivsByRid(roleid);
        logger.debug("id为"+roleid+"角色拥有的所有权限为:"+metaoperations);
        List<Metaoperation> metaoperations1 = metaOperationService.selectAllChildNavBarByHead(privname);
        logger.debug("id为"+roleid+" 的角色在 "+privname+" 导航栏下拥有的权限为: "+ metaoperations1);
        List<PrivGradationalDto> privGradationalDtos = new ArrayList<>();

        if (!metaoperations.isEmpty()){
            for (Metaoperation metaoperation : metaoperations) {
                if (!metaoperations1.isEmpty()){
                    for (Metaoperation metaoperation1 : metaoperations1) {
                        if (metaoperation1.getMoid().equals(metaoperation.getMoid())){
                            privGradationalDtos.add(new PrivGradationalDto(metaoperation.getMoid(),metaoperation.getMoname(),metaoperation.getMolurl(),null));
                        }
                    }
                }else{
                    return null;
                }
//                logger.debug("navigationBarChilrens"+navigationBarChilrens);

            }
            return privGradationalDtos;
        }return null;


    }

    @Override
    //@Cacheable(value = "navigationbar",key = "#root.methodName.toString().toString()+'--'+#privname",unless = "#result==null")
    public List<PrivGradationalDto> selectAllErJiBiaoTiChildrenByGroupdesc(String privname) {
        List<Metaoperation> metaoperations1 = metaOperationService.selectAllChildNavBarByHead(privname);
        List<PrivGradationalDto> privGradationalDtos = new ArrayList<>();
        for (Metaoperation metaoperation1 : metaoperations1) {
                privGradationalDtos.add(new PrivGradationalDto(metaoperation1.getMoid(),metaoperation1.getMoname(),metaoperation1.getMolurl(),null));
        }
        return privGradationalDtos;
    }


}
