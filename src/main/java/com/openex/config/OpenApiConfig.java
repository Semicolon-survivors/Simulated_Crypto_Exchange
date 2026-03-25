package com.openex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class OpenApiConfig {

    @Bean
    @SuppressWarnings("unused")
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("OpenEx API")
                .description("HTTP API for OpenEx services, including real-time feed and order handling.")
                .version("v1"))
            .info(new Info()
                .title("OpenEx API")
                .version("v1")
                .description("HTTP API for OpenEx services, including real-time feed and order handling.")
                .contact(new Contact().name("OpenEx Team").email("support@openex.local"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
