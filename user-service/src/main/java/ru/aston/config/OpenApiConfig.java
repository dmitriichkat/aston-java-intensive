package ru.aston.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("v1")
                        .description("API для управления пользователями. Поддерживает CRUD-операции и HATEOAS-навигацию.")
                        .contact(new Contact()
                                .name("User Service Team")
                                .url("https://github.com/dmitriichkat/aston-java-intensive")));
    }
}
