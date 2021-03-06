package cn.edu.sicnu.cs.config;

import cn.edu.sicnu.cs.utils.SpringUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 创建spring启动后任务
 * @Classname StartService
 * @Description TODO
 * @Date 2020/11/24 10:27
 * @Created by Huan
 */
@Component
//@Order(1)
public class StartService implements ApplicationRunner {

    @Override   //suoying  规则
    public void run(ApplicationArguments args) throws Exception {
       RedisTemplate redisTemplate = (RedisTemplate) SpringUtil.getBean("redisTemplate");
       redisTemplate.setKeySerializer(new StringRedisSerializer());
        Set<String> keys = redisTemplate.keys("*");
//        redisTemplate.delete(keys);
    }
}
