package cn.edu.sicnu.cs.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.List;


@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {




    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()

                .title("软件公司售后系统接口API文档")

                .description("四川师范大学计算机科学学院2018级软件工程专业4班第四小组")

                .contact(new Contact("第四小组", "四川师范大学", "@stu.edu.sicnu.com"))

                .version("1.1")

                .build();

    }

//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("github.javaguide.springsecurityjwtguide"))
//                .paths(PathSelectors.any())
//                .build()
//                .securityContexts(securityContext())
//                .securitySchemes(securitySchemes());
//    }
    @Bean
    public Docket createRestApi() {

        return new Docket(DocumentationType.SWAGGER_2)

                .apiInfo(apiInfo())

                .select()

                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))

                .paths(PathSelectors.any())

                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());

    }

    private List<SecurityScheme> securitySchemes() {
//        return Collections.singletonList(new ApiKey("Authorization", SecurityConstants.TOKEN_HEADER, "header"));
        return Collections.singletonList(new ApiKey("JWT", "Authorization", "header"));
    }

    private List<SecurityContext> securityContext() {
        SecurityContext securityContext = SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
        return Collections.singletonList(securityContext);
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
    }


}
