package com.example.appointment.system.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

// Настройки для отображения Swagger-ui
@OpenAPIDefinition(
        info = @Info(
                title = "Medical center API",
                description = "API, предназначенное для управлением рабочими процессами в медецинских центрах",
                version = "v1"
        )
)
public class SwaggerConfig {
}
