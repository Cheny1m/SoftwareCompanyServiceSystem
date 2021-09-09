package cn.edu.sicnu.cs.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

/**
 * @Classname UserOfNumUtils
 * @Description TODO
 * @Date 2020/12/15 11:35
 * @Created by Huan
 */
public class UserOfNumUtils {

    private static  StringRedisTemplate getStringRedisTemplate(){
        return SpringUtil.getBean(StringRedisTemplate.class);
    }

    /**
     * 查询正则表达式pattern匹配的数量
     * @param pattern
     * @return
     */
    public  static int selectLoginedUserNum(String pattern){
        Set<String> keys = getStringRedisTemplate().opsForValue().getOperations().keys(pattern);
        return Objects.requireNonNull(keys).size();
    }

    /**
     * 查询匹配的用户id
     * @param pattern
     * @return
     */
    public  static String[] selectLoginedUserId(String pattern){
        Set<String> keys = getStringRedisTemplate().opsForValue().getOperations().keys(pattern);
        if (keys.size() > 0) {
            return keys.toArray(new String[keys.size()]);
        }
        return new String[]{};

    }

    /**
     * 查询客服平均登录时间
     * @return
     */
    public static float    selectAverageLoginedTime(){
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
        long l = System.currentTimeMillis();
        Set<String> loginKeys = getStringRedisTemplate().opsForValue().getOperations().keys("kefu:login:*");
        Set<String> logoutKeys = getStringRedisTemplate().opsForValue().getOperations().keys("kefu:logout:*");

        long OnlinetimeSum = 0;

        for (String loginKey : loginKeys) {
            if(!TimeUtils.isToday(Long.parseLong(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(loginKey))))){
                stringRedisTemplate.delete(loginKey);
                continue;
            }
            String loginKeyStr = loginKey;
            String logoutStr = loginKey.replace("login", "logout");
            String s = stringRedisTemplate.opsForValue().get(logoutStr);
//            if (StringUtils.isNotBlank(s)){
//                if (!TimeUtils.isToday(Long.parseLong(s))){
//                    stringRedisTemplate.delete(logoutStr);
//                    OnlinetimeSum = OnlinetimeSum + System.currentTimeMillis() - Long.parseLong(stringRedisTemplate.opsForValue().get(loginKeyStr));
//                }else {
//                    OnlinetimeSum = OnlinetimeSum +  Long.parseLong(s) - Long.parseLong(stringRedisTemplate.opsForValue().get(loginKeyStr));
//                }
//            }else {
//                OnlinetimeSum = OnlinetimeSum + System.currentTimeMillis() - Long.parseLong(stringRedisTemplate.opsForValue().get(loginKeyStr));
//            }
            OnlinetimeSum = OnlinetimeSum + System.currentTimeMillis() - Long.parseLong(stringRedisTemplate.opsForValue().get(loginKeyStr));
        }
        float ratio = 0;
        if (!loginKeys.isEmpty()){
            ratio = (float) ((OnlinetimeSum * 1.0) / (8*1000*60*60*1.0* loginKeys.size()));
        }
        ratio = ((float)(((int)(ratio*100))/100.0));
        return ratio;

    }

    /**
     * 格式化查询客服登录时间
     * @return
     */
    public static String selectLoginedTime(String id){
        StringRedisTemplate stringRedisTemplate = getStringRedisTemplate();
        long l = System.currentTimeMillis();
        String logingTime = stringRedisTemplate.opsForValue().get("kefu:login:" + id);

        long time;

        if (!Objects.isNull(logingTime)){
            String logoutTime = stringRedisTemplate.opsForValue().get("kefu:logout:" + id);
            if (!Objects.isNull(logoutTime)){
                time = Long.parseLong(logoutTime) - Long.parseLong(logingTime);
            }else {
                time = System.currentTimeMillis() - Long.parseLong(logingTime);
            }
            int hour = (int) ((time / (1000*60*60))%24);
            int minute = (int)(time / (1000 * 60))%60;
            if (hour == 0){
                return String.valueOf(minute)+"min";
            }
            return String.valueOf(hour)+"h"+String.valueOf(minute)+"min";
        }else {
            return "";
        }

    }

}
