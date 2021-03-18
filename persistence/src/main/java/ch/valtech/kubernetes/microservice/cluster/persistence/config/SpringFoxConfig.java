package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.DefaultPathProvider;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

  private final String swaggerApiPrefix;

  public SpringFoxConfig(@Value("${application.swaggerApiPrefix}") String swaggerApiPrefix) {
    this.swaggerApiPrefix = swaggerApiPrefix;
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .pathProvider(new DefaultPathProvider() {
          @Override
          public String getOperationPath(String operationPath) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(swaggerApiPrefix);
            return Paths.removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
          }
        })
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
  }

}
