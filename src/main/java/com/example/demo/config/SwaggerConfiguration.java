package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();
        server.setUrl("https://class101.nuaa.edu.cn/");
        return new OpenAPI().info(apiInfo());
    }

    private Info apiInfo() {
        return new Info().title("NUAA 良师益友评选赛后台接口")
                .description("南京航空航天大学 第N届良师益友评选赛后台接口")
                .contact(new Contact().name("Chance").email("zzhchance@163.com"))
                .version("1.0.0");
    }

}
