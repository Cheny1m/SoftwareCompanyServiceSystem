//package cn.edu.sicnu.cs.utils;
//
//import cn.hutool.crypto.Mode;
//import cn.hutool.crypto.Padding;
//import cn.hutool.crypto.SecureUtil;
//import cn.hutool.crypto.symmetric.AES;
//import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//
///**
// * 加解密工具类
// * @Classname EncyptUtils
// * @Description TODO
// * @Date 2020/12/15 1:05
// * @Created by Huan
// */
//@Component
//public class EncyptUtils {
//    private static final AES aes = new AES(Mode.ECB,Padding.valueOf("CryptoJS.pad.Pkcs7"));
//
//    private static final String byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
//
//    private static  StringRedisTemplate getStringRedisTemplate(){
//
//
//        return SpringUtil.getBean(StringRedisTemplate.class);
//    }
//
//    public static String AESENCODE(String data){
//        getStringRedisTemplate().opsForValue().getOperations().keys("kefu:login:*");
//    }
//    public static String AESDECODE(String data){
//        return null;
//    }
//
//}
