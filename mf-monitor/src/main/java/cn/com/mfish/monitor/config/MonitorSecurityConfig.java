package cn.com.mfish.monitor.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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

    /**
     * 构造监控安全配置，注入Admin Server属性以获取应用上下文路径
     *
     * @param adminServerProperties Admin Server配置属性
     */
    public MonitorSecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    /**
     * 配置安全过滤器链，定义请求授权规则、表单登录、注销、HTTP Basic认证、CSRF保护及iframe嵌入策略
     *
     * @param http HTTP安全配置对象
     * @return 安全过滤器链
     * @throws Exception 安全配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");
        http.authorizeHttpRequests((auth) -> {
                    auth.requestMatchers(adminContextPath + "/assets/**",
                                    adminContextPath + "/login",
                                    adminContextPath + "/actuator/health").permitAll()
                            .anyRequest().authenticated();
                }).formLogin((login) -> {
                    login.loginPage(adminContextPath + "/login").successHandler(successHandler);
                }).logout((logout) -> {
                    logout.logoutUrl(adminContextPath + "/logout");
                })
                //开启http basic支持，admin-client注册时需要使用
                .httpBasic(Customizer.withDefaults())
                // 允许嵌入到iframe中
                .headers((header) -> {
                    header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
                })
                //开启基于cookie的csrf保护
                .csrf((csrf) -> {
                    csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringRequestMatchers(
                            adminContextPath + "/instances",
                            adminContextPath + "/actuator/**"
                    );
                });
        return http.build();
    }
}