package cn.edu.sicnu.cs.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname LogLogin
 * @Description TODO
 * @Date 2020/12/13 13:35
 * @Created by Huan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogLogin {
    String value() default "";
}
