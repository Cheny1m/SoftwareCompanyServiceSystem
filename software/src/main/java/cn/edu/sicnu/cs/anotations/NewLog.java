package cn.edu.sicnu.cs.anotations;

import cn.edu.sicnu.cs.constant.LogConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 新的日志类型注解,相比@NewLog增加了Type注解
 * @author Huan
 * @Classname NewLog
 * @Description TODO
 * @Date 2020/12/13 20:57
 * @Created by Huan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NewLog {
    // 操作描述信息
    String value() default "";
    // 注解类别,也就是后续查寻一类的时候的标准,可以输入自己自定义的一类
    String type() default "";
}
