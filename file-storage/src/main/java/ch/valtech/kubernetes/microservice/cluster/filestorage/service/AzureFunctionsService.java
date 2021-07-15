package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AzureFunctionsService implements FunctionsService {

  public static final String FUNCTION_KEY_HEADER = "x-functions-key";

  private final String functionsUrl;
  private final RestTemplate restTemplate;

  public AzureFunctionsService(
      @Value("${application.functions.url}") String functionsUrl,
      @Value("${application.functions.key}") String functionsKey,
      RestTemplateBuilder restTemplateBuilder) {
    this.functionsUrl = functionsUrl;
    this.restTemplate = restTemplateBuilder
        .defaultHeader(FUNCTION_KEY_HEADER, functionsKey)
        .build();
  }

  @Override
  public String echo(String body) {
    return postForEntity(new HttpEntity<>(body), "/echo").getBody();
  }

  @Override
  public String reverse(String body) {
    return postForEntity(new HttpEntity<>(body), "/reverse").getBody();
  }

  public HttpEntity<String> postForEntity(HttpEntity<String> httpRequest, String path) {
    try {
      return restTemplate.postForEntity(functionsUrl + path, httpRequest, String.class);
    } catch (RestClientException e) {
      return handleException(e, path);
    }
  }

  private ResponseEntity<String> handleException(RestClientException e, String path) {
    if (e instanceof HttpStatusCodeException) {
      return ResponseEntity.status(((HttpStatusCodeException) e).getStatusCode()).build();
    }

    log.error("Function {} failed", path, e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

}
