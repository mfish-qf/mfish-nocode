package cn.com.mfish.monitor.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.util.UUID;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

/**
 * @description: 监控安全配置
 * @author: mfish
 * @date: 2023/1/27 11:39
 */
@Configuration(proxyBeanMethods = false)
public class MonitorSecurityConfig {
    private final AdminServerProperties adminServer;
    /**
     * 构造监控安全配置，注入Admin Server属性以获取应用上下文路径
     *
     * @param adminServer Admin Server配置属性
     */
    public MonitorSecurityConfig(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
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
        successHandler.setDefaultTargetUrl(adminServer.path("/"));

        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher(adminServer.path("/assets/**")))
                        .permitAll()
                        .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher(adminServer.path("/actuator/info")))
                        .permitAll()
                        .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher(adminServer.path("/actuator/health")))
                        .permitAll()
                        .requestMatchers(PathPatternRequestMatcher.withDefaults().matcher(adminServer.path("/login")))
                        .permitAll()
                        // Spring Security 7 需要放行ASYNC分发类型，否则SSE等异步请求会被拦截
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        .anyRequest().authenticated()
                ).formLogin((login) -> login
                        .loginPage(adminServer.path("/login"))
                        .successHandler(successHandler)
                ).logout((logout) -> logout
                        .logoutUrl(adminServer.path("/logout"))
                )
                //开启http basic支持，admin-client注册时需要使用
                .httpBasic(Customizer.withDefaults())
                // 允许嵌入到iframe中
                .headers((header) -> header
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                // Spring Security 7 延迟加载CSRF Token，必须添加CustomCsrfFilter将token写入cookie，
                // 否则前端JS无法获取token，登录表单提交时缺少token会被拒绝
                .addFilterAfter(new CustomCsrfFilter(), BasicAuthenticationFilter.class)
                //开启基于cookie的csrf保护
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers(
                                PathPatternRequestMatcher.withDefaults().matcher(POST, adminServer.path("/instances")),
                                PathPatternRequestMatcher.withDefaults().matcher(DELETE, adminServer.path("/instances/*")),
                                PathPatternRequestMatcher.withDefaults().matcher(adminServer.path("/actuator/**"))
                        )
                )
                .rememberMe((rememberMe) -> rememberMe
                        .key(UUID.randomUUID().toString())
                        .tokenValiditySeconds(1209600)
                );
        return http.build();
    }
}