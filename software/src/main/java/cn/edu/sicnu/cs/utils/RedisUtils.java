package cn.edu.sicnu.cs.utils;

import cn.edu.sicnu.cs.model.Metaoperation;
import cn.edu.sicnu.cs.pojo.RoleInfo;
import cn.edu.sicnu.cs.service.MetaOperationService;
import cn.edu.sicnu.cs.service.RoleService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Classname RedisUtils
 * @Description TODO
 * @Date 2020/11/24 19:47
 * @Created by Huan
 */
@Component
public class RedisUtils {


    @Autowired
    MetaOperationService metaOperationService;

    @Autowired
    RoleService roleService;


    private static StringRedisTemplate getRedisTemplate(){
        StringRedisTemplate redisTemplate;
        redisTemplate = (StringRedisTemplate) SpringUtil.getBean("stringRedisTemplate");
        return redisTemplate;
    }

    public void delete(String key){
        getRedisTemplate().delete(key);
    }

    public void addConfigrationPermissions(){
        Collection<ConfigAttribute> configAttributes=new ArrayList<>();
        List<Metaoperation> operations = metaOperationService.selectAll();
        for (Metaoperation operation:operations) {
            ConfigAttribute configAttribute=new SecurityConfig(operation.getMolurl()+" "+operation.getMomethod());
            configAttributes.add(configAttribute);
        }
        //将权限存入redis
        getRedisTemplate().opsForValue().set("configAttributes:permissions", JSON.toJSONString(operations),480, TimeUnit.MINUTES);

        getRedisTemplate().delete("authentication:roleinfos:permissions");
        List<RoleInfo> roleInfos= roleService.selectAllRoleAndMetaoperations();
        getRedisTemplate().opsForValue().set("authentication:roleinfos:permissions", JSON.toJSONString(roleInfos),480,TimeUnit.MINUTES);
    }


}
