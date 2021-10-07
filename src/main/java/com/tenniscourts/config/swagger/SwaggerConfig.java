package com.tenniscourts.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;


@Configuration
public class SwaggerConfig {

    public static final String GUEST_TAG = "guest";
    public static final String TENNIS_COURT_TAG = "tennis court";
    public static final String SCHEDULE_TAG = "schedule";
    public static final String RESERVATION_TAG = " reservation";

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("com.tenniscourts"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .tags(new Tag(GUEST_TAG, "Operations about guests"))
                .tags(new Tag(TENNIS_COURT_TAG, "Operations about tennis courts"))
                .tags(new Tag(SCHEDULE_TAG, "Operations about schedules"))
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Tennis Courts",
                "Tennis Courts API.",
                "1.0.0",
                "Terms of service",
                new Contact("Jonas C. do Carmo", "https://www.linkedin.com/in/jonas-candido-do-carmo/", "jonasccarmo@gmail.com"),
                "License of API", "API license URL", Collections.emptyList());
    }

}
