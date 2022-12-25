package cn.com.mfish.common.web.annotation;

import cn.com.mfish.common.core.annotation.AutoFeignClients;
import cn.com.mfish.common.swagger.annotation.AutoSwagger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ：qiufeng
 * @description：web自动注解
 * @date ：2022/9/20 10:57
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication
@AutoSwagger
@MapperScan({"cn.com.mfish.**.mapper"})
@AutoFeignClients
public @interface AutoWeb {
}
