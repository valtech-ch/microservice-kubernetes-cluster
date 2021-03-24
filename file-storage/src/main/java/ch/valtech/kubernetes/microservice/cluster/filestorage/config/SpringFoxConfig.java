package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant("/api/**"))
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Filestorage Service API")
        .description("Filestorage API Description")
        .contact(new Contact("Valtech", "https://www.valtech.com/en-ch/", "lukas.rohner@valtech.com"))
        .license("MIT License")
        .licenseUrl("https://github.com/valtech-ch/microservice-kubernetes-cluster/blob/main/LICENSE")
        .version("1.0.0")
        .build();
  }

}
