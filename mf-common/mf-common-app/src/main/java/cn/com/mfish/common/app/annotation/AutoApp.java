package cn.com.mfish.common.app.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 轻量级应用自动注解(不访问数据库应用)
 * @author: mfish
 * @date: 2023/3/1 17:21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication
public @interface AutoApp {
}
