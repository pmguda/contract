package com.pmguda.contract.api.config;




import com.fasterxml.classmate.TypeResolver;
import com.pmguda.contract.api.dto.ContractDTO;
import com.pmguda.contract.api.dto.ResponseDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDate;

@Configuration
@EnableWebMvc
public class SwaggerConfiguration {

    private static final String API_NAME = "계약관리 API";
    private static final String API_VERSION = "0.1.0";
    private static final String API_DESCRIPTION = "계약관리 API 명세서";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
  //              .globalOperationParameters(globalParamters)
                .directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .additionalModels(new TypeResolver().resolve(ContractDTO.class))
                .additionalModels(new TypeResolver().resolve(ResponseDTO.class))
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.pmguda.contract.api.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .license("pmguda")
                .licenseUrl("https://www.pmguda.com")
                .build();
    }
}