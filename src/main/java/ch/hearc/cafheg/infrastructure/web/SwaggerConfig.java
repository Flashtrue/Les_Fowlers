package ch.hearc.cafheg.infrastructure.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("${spring.application.name:application}")
    private String appName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title(appName).version("v1"));
    }
}
