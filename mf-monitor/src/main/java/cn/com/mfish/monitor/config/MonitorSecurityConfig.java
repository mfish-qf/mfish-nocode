package cn.com.mfish.monitor.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * @description: 监控安全配置
 * @author: mfish
 * @date: 2023/1/27 11:39
 */
@Configuration
public class MonitorSecurityConfig {
    //项目应用路径
    private final String adminContextPath;

    public MonitorSecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");
        http.authorizeRequests()
                //无需登录即可访问
                .antMatchers(adminContextPath + "/assets/**",
                        adminContextPath + "/login",
                        adminContextPath+"/actuator/health").permitAll()
                .anyRequest().authenticated()
                .and()
                //登录和登出路径
                .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and()
                .logout().logoutUrl(adminContextPath + "/logout").and()
                //开启http basic支持，admin-client注册时需要使用
                .httpBasic().and()
                .csrf()
                //开启基于cookie的csrf保护
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers(
                        adminContextPath + "/instances",
                        adminContextPath + "/actuator/**"
                );
        return http.build();
    }
}