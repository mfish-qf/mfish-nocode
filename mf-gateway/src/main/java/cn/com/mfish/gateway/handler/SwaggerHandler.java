package cn.com.mfish.gateway.handler;

import cn.com.mfish.gateway.config.SwaggerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.*;

import java.util.List;

/**
 * @author: mfish
 * @description: swagger处理器
 * @date: 2021/11/15 11:44
 */
@RestController
@RequestMapping("/swagger-resources")
public class SwaggerHandler {
    private final SwaggerProvider swaggerProvider;

    @Autowired
    public SwaggerHandler(SwaggerProvider swaggerProvider) {
        this.swaggerProvider = swaggerProvider;
    }

    @GetMapping("/configuration/security")
    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
        return Mono.just(new ResponseEntity<>(SecurityConfigurationBuilder.builder().build(), HttpStatus.OK));
    }

    @GetMapping("/configuration/ui")
    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
        return Mono.just(new ResponseEntity<>(UiConfigurationBuilder.builder().build(), HttpStatus.OK));
    }

    @GetMapping
    public Mono<ResponseEntity<List<SwaggerResource>>> swaggerResources() {
        return Mono.just((new ResponseEntity<>(swaggerProvider.get(), HttpStatus.OK)));
    }

}
