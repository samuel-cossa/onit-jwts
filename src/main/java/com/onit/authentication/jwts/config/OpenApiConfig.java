package com.onit.authentication.jwts.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(info = @Info(title = "✨ JWTs", version = "v1.0", description = """
        API oficial do projeto **JWTs**. para desafios da ONIT

        > Feito com Spring Boot 3.5.6 + Spring Security + JWT.
        """, contact = @Contact(name = "Samuel Cossa", email = "ar.sam.cossa@gmail.com", url = "https://github.com/samuel-cossa"), license = @License(name = "Apache License V2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"), termsOfService = "https://we-onit-jwts.com/terms"), servers = {
        @Server(description = "🌱 Local", url = "http://localhost:8080"),
        @Server(description = "🚀 Produção", url = "https://we-onit-jwts.com")
})
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")

public class OpenApiConfig {
}
