package cn.com.mfish.oauth.config;

import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.config.properties.WhitesProperties;
import cn.com.mfish.oauth.filter.TokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final List<AuthenticationProvider> providers;
    private final WhitesProperties whitesProperties;
    private final TokenFilter tokenFilter;

    @Autowired
    public SecurityConfig(List<AuthenticationProvider> providers, WhitesProperties whitesProperties, TokenFilter tokenFilter) {
        this.providers = providers;
        this.whitesProperties = whitesProperties;
        this.tokenFilter = tokenFilter;
    }

    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilterRegistration(TokenFilter tokenFilter) {
        FilterRegistrationBean<TokenFilter> registration = new FilterRegistrationBean<>(tokenFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        HttpSessionSecurityContextRepository repository =
                new HttpSessionSecurityContextRepository();
        repository.setAllowSessionCreation(true);
        return repository;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JsonAuthenticationEntryPoint()));

        if (ServiceConstants.isBoot(Utils.getServiceType())) {
            List<String> whitesList = whitesProperties.getWhites();
            http.authorizeHttpRequests(auth -> auth
                            .requestMatchers(whitesList.toArray(new String[0])).permitAll()
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
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
        public void commence(@NonNull HttpServletRequest request, HttpServletResponse response,
                             @NonNull AuthenticationException authException) throws IOException, ServletException {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().print(mapper.writeValueAsString(
                    Result.fail(HttpStatus.UNAUTHORIZED.value(), "未认证或认证已过期")));
        }
    }
}
