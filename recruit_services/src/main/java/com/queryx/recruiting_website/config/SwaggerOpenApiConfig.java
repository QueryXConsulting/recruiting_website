package com.queryx.recruiting_website.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger配置类
 *
 * @author fjj
 * @since 2025-1-5 15:33:31
 */
@Configuration
public class SwaggerOpenApiConfig {
    
    @Bean
    public OpenAPI publicApi() {
        return new OpenAPI()
                .info(new Info().title("泽克拉姆招聘网站")
                        .description("API文档")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目地址")
                        .url("https://github.com/QueryXConsulting/recruiting_website"));
    }
}
