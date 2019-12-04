package com.xcyy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger配置类
 */
@Configuration
public class Swagger2 {

    /**
     * 配置接口地址
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xcyy.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 配置接口信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("服务端Restful Api")
                .description("Restful Api")
                .termsOfServiceUrl("http://www.baidu.com/")
                .contact("tuwq")
                .version("1.0")
                .build();
    }
}
