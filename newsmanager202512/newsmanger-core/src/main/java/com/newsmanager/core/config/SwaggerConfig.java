package com.newsmanager.core.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI swaggeropenapi(){
        List<Server> list= new ArrayList<Server>();
        list.add(new Server().url("http://localhost:8080/"));
        list.add(new Server().url("http://localhost:8080/"));
        OpenAPI oa= new OpenAPI();
        oa.setServers(list);
        return oa.info(new Info().title("新闻聚合平台后台业务接口")
                .description("Swagger接口文档").version("1.0"))
                .externalDocs(new ExternalDocumentation().description("我的小博客").url("https://www.baidu.com"));
    }

}
