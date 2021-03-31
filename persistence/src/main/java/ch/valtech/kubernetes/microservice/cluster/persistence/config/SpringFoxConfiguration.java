package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfiguration {

  @Bean
  public Docket api(@Value("${application.token.url}") String tokenUrl) {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant("/**/api/**"))
        .build()
        .securitySchemes(List.of(securityScheme(tokenUrl)))
        .securityContexts(List.of(securityContext()));
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Persistence Service API")
        .description("Persistence API Description")
        .contact(new Contact("Valtech", "https://www.valtech.com/en-ch/", "lukas.rohner@valtech.com"))
        .license("MIT License")
        .licenseUrl("https://github.com/valtech-ch/microservice-kubernetes-cluster/blob/main/LICENSE")
        .version("1.0.0")
        .build();
  }

  private SecurityScheme securityScheme(String tokenUrl) {
    GrantType grantType = new ResourceOwnerPasswordCredentialsGrant(tokenUrl);
    return new OAuthBuilder().name("spring_oauth")
        .grantTypes(List.of(grantType))
        .scopes(List.of(scopes()))
        .build();
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(
            List.of(new SecurityReference("spring_oauth", scopes())))
        .build();
  }

  private AuthorizationScope[] scopes() {
    return new AuthorizationScope[0];
  }

}
