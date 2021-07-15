package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.client.ReactivePersistenceClient;
import ch.valtech.kubernetes.microservice.cluster.filestorage.mapper.AuditingMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

@Slf4j
@Service("auditingServiceRest")
public class AuditingServiceRest implements AuditingService {

  private final String persistenceUrl;
  private final RestTemplate restTemplate;
  private final ReactivePersistenceClient reactivePersistenceClient;
  private final AuditingMapper auditingMapper;

  public AuditingServiceRest(@Value("${application.persistence.url}") String persistenceUrl,
      @Qualifier("jwtRestTemplate") RestTemplate restTemplate,
      ReactivePersistenceClient reactivePersistenceClient,
      AuditingMapper auditingMapper) {
    this.persistenceUrl = persistenceUrl;
    this.restTemplate = restTemplate;
    this.reactivePersistenceClient = reactivePersistenceClient;
    this.auditingMapper = auditingMapper;
  }

  @Override
  public MessageDto audit(String filename, Action action) {
    AuditingRequestDto auditingRequest = auditingMapper.toAuditingRequestDto(filename, action);
    HttpEntity<AuditingRequestDto> request = new HttpEntity<>(auditingRequest, populateHeaders());
    return postForEntity(request).getBody();
  }

  @Override
  public Flux<MessageDto> search(SearchRequest searchRequest) {
    return reactivePersistenceClient.getMessagesByFilename(searchRequest);
  }

  private HttpHeaders populateHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  public HttpEntity<MessageDto> postForEntity(HttpEntity<AuditingRequestDto> httpRequest) {
    try {
      return restTemplate.postForEntity(persistenceUrl, httpRequest, MessageDto.class);
    } catch (RestClientException e) {
      return handleException(e);
    }
  }

  private ResponseEntity<MessageDto> handleException(RestClientException e) {
    if (e instanceof HttpStatusCodeException) {
      return ResponseEntity.status(((HttpStatusCodeException) e).getStatusCode()).build();
    }

    log.error("Failed to audit", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

}
