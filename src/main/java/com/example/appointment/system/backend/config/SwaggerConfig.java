package com.example.appointment.system.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

// Настройки для отображения Swagger-ui
@OpenAPIDefinition(
        info = @Info(
                title = "Spring-Security JWT API",
                description = "API, предназначенное для аунтентификации пользователь и получения JWT токена",
                version = "v1"
        )
)
public class SwaggerConfig {
}
