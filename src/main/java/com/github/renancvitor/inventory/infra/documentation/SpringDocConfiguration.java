package com.github.renancvitor.inventory.infra.documentation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .info(new Info()
                        .title("Inventory Notification System")
                        .version("1.0.0")
                        .description(
                                "API REST da aplicação Inventory Notification System." +
                                        "Forneceções operações de criação, leitura, atualização e remoção (CRUD) para os recursos do sistema. O projeto está em desenvolvimento e esta documentação será atualizada em tempo."
                                        +
                                        "Esta API garante validação, autenticação via JWT (JSON Web Token) e tratamento consistente de erros, permitindo integração segura com o Frontend.")
                        .contact(new Contact()
                                .name("Renan C. Vitor")
                                .email("renan.vitor.cm@gmail.com"))
                        .license(new License()
                                .name("MIT Licence")
                                .url("https://github.com/renancvitor/inventory-notification-system-backend/blob/main/LICENSE")));

    }

}
