package cn.edu.sicnu.cs.service.impl;

import cn.edu.sicnu.cs.dao.RoleMapper;
import cn.edu.sicnu.cs.model.Metaoperation;
import cn.edu.sicnu.cs.model.Role;
import cn.edu.sicnu.cs.model.RoleExample;
import cn.edu.sicnu.cs.vo.NavigationBarVo;
import cn.edu.sicnu.cs.pojo.RoleInfo;
import cn.edu.sicnu.cs.service.RolePrivService;
import cn.edu.sicnu.cs.service.RoleService;
import cn.edu.sicnu.cs.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname RoleServiceImpl
 * @Description TODO
 * @Date 2020/11/16 10:08
 * @Created by Huan
 */
@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Resource
    RoleMapper roleMapper;

    @Autowired
    RolePrivService rolePrivService;

    @Autowired
    RedisUtils redisUtils;

    @Override
    ////@Cacheable(cacheNames = "rolelist",key = "#root.methodName.toString()",unless = "#result==null")
    public List<Role> selectAll() {
        RoleExample roleExample = new RoleExample();
        RoleExample.Criteria criteria = roleExample.createCriteria();
        criteria.andRidIsNotNull();
        return roleMapper.selectByExample(roleExample);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "navigationbar",allEntries = true)
                    //@CacheEvict(value = "roleprivs",allEntries = true),
                    //@CacheEvict(value = "privrolesevict",allEntries = true),
                    //@CacheEvict(value = "privsevict",allEntries = true),
                    //@CacheEvict(value = "role",key = "#id"),
                    //@CacheEvict(value = "roleprivsevict",key = "#id"),
                    //@CacheEvict(value = "roleprivsIntevict",key = "#id"),
                    //@CacheEvict(value = "roleprivs",allEntries = true),
            }
    )
    public int deleteByPrimaryKey(Integer id) {
        redisUtils.delete("configAttributes:permissions");
        if (id==null){
            logger.error("???????????????id????????????");
            return 0;
        }
        return roleMapper.deleteByPrimaryKey(id);
    }


    /**
     *       **************  ????????????
     * @param record ?????????????????????
     * @return
     */
    @Override
    //@Caching(
//            evict = {
//                    //@CacheEvict(value = "roleprivs",allEntries = true),
//                    //@CacheEvict(value = "rolelist",allEntries = true)
//            }
//    )
    public int insert(Role record) {
        redisUtils.delete("configAttributes:permissions");
        if (record==null){
            logger.error("?????????????????????????????????");
            return 0;
        }
        return roleMapper.insertSelective(record);
    }

    @Override
////    ////@Cacheable(cacheNames = "role",key = "#result?.rid",unless = "#result==null")
//    //@Caching(
//            cacheable = {
//                    //@Cacheable(value = "navigationbar",key = "#roleName+'---'+#root.methodName.toString().toString()",unless = "#result==null")
//            }
//    )
    public Role selectRoleByRoleName(String roleName) {
        RoleExample roleExample = new RoleExample();
        RoleExample.Criteria criteria = roleExample.createCriteria();
        criteria.andRnameEqualTo(roleName);
        List<Role> roles = roleMapper.selectByExample(roleExample);
        if (!roles.isEmpty()){
            return roles.get(0);
        }
        return null;
    }

    @Override
    ////@Cacheable(cacheNames = "role",key = "#id",unless = "#result==null")
    public Role selectByPrimaryKey(Integer id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    ////@Cacheable(cacheNames = "roleprivsByrid",key = "#rid",unless = "#result==null")
    public List<Metaoperation> selectPrivilegesByRid(int rid) {
        return rolePrivService.selectRolePrivsByRid(rid);
    }


    @Override
    @Caching(
            evict = {
                    //@CacheEvict(cacheNames = "role",key = "#rid"),
                    //@CacheEvict(cacheNames = "rolelist",allEntries = true),
                    //@CacheEvict(cacheNames = "roleprivs",allEntries = true),
                    //@CacheEvict(cacheNames = "sUserUserpojo",allEntries = true)
            }
    )
    public int updateRoleByPrimaryKey(int rid, Role record) {
        redisUtils.delete("configAttributes:permissions");
        if (record==null){
            logger.error("???????????????????????????id");
            return 0;
        }
        record.setRid(rid);
        return roleMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    ////@Cacheable(value = "roleprivs",key = "#root.methodName.toString()",unless = "#result==null")
    public List<RoleInfo> selectAllRoleAndMetaoperations() {
        List<Role> roles = this.selectAll();
        List<RoleInfo> roleInfos = new ArrayList<>();
        for (Role role : roles) {
            if (role!=null){
                roleInfos.add(new RoleInfo(role.getRid(),role.getRname(),this.selectPrivilegesByRid(role.getRid())));
            }
        }
        return roleInfos;
    }

    @Override
//    ////@Cacheable(value = "rolelist",key = "#root.methodName.toString()",unless = "#result==null")
    public List<Role> selectAllRoles() {
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andRidIsNotNull();
        return roleMapper.selectByExample(roleExample);
    }

    @Override
    //@Cacheable(value = "navigationbar",key = "#root.methodName.toString()+'--'+#rid")
    public List<NavigationBarVo> selectNavBarByRole(Integer rid) {
//        List<NavigationBar> navigationBars = rolePrivService.selectNavBarByRole(Integer rid);

        return null;
    }
}
