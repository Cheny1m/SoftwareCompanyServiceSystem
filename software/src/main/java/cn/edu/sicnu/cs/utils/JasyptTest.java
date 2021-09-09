package cn.edu.sicnu.cs.utils;

/**
 * @Classname DruidBrypt
 * @Description TODO
 * @Date 2020/11/2 18:43
 * @Created by Huan
 */



import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

public class JasyptTest {

    public static void main(String[] args) {

        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");     // 加密的算法，这个算法是默认的
        config.setPassword("SJLFjiflDJjksadfNjkasdffmop1JSOFIJ26re@#15r4324FE");            // 加密的密钥
        standardPBEStringEncryptor.setConfig(config);
        String username = "Redis3.1415926";
        String password = "49.232.130.26:26379,49.232.130.26:26380,49.232.130.26:26382";
        String url="mymaster";
        System.out.println(standardPBEStringEncryptor.encrypt(username));
        System.out.println(standardPBEStringEncryptor.encrypt(password));
        System.out.println(standardPBEStringEncryptor.encrypt(url));
    }

    public void testDe() throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword("test");
        standardPBEStringEncryptor.setConfig(config);
        String encryptedText = "ip10XNIEfAMTGQLdqt87XnLRsshu0rf0";
        String plainText = standardPBEStringEncryptor.decrypt(encryptedText);
        System.out.println(plainText);
    }
}