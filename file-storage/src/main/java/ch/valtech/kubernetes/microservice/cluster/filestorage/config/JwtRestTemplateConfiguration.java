package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import ch.valtech.kubernetes.microservice.cluster.filestorage.util.SecurityUtils;
import java.io.IOException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JwtRestTemplateConfiguration {

  @Bean
  public RestTemplate jwtRestTemplate(RestTemplateBuilder builder) {
    return builder.additionalInterceptors(new CustomClientHttpRequestInterceptor())
        .build();
  }

  public static class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
      SecurityUtils.getJwt().ifPresent(token -> request.getHeaders()
          .add(HttpHeaders.AUTHORIZATION, "Bearer " + token));
      return execution.execute(request, body);
    }

  }

}
