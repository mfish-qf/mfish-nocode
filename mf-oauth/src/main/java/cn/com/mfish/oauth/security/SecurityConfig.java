package cn.com.mfish.oauth.security;

import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.validator.TokenValidator;
import cn.com.mfish.oauth.config.properties.ShiroWhitesProperties;
import cn.com.mfish.oauth.filter.TokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Spring Security 配置类，替代原 ShiroConfig
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final List<AuthenticationProvider> providers;

    @Autowired
    public SecurityConfig(List<AuthenticationProvider> providers) {
        this.providers = providers;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JsonAuthenticationEntryPoint()));

        if (ServiceConstants.isBoot(Utils.getServiceType())) {
            ShiroWhitesProperties whitesProperties = SpringBeanFactory.getBean(ShiroWhitesProperties.class);
            List<String> whitesList = whitesProperties.getWhites();
            String[] whites = whitesList.toArray(new String[0]);
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(whites).permitAll()
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(new TokenFilter(whitesList), UsernamePasswordAuthenticationFilter.class);
        } else {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(providers);
    }

    /**
     * 返回 JSON 401 响应的认证入口点
     */
    static class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException, ServletException {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().print(mapper.writeValueAsString(
                    Result.fail(HttpStatus.UNAUTHORIZED.value(), "未认证或认证已过期")));
        }
    }
}
