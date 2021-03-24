package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static ch.valtech.kubernetes.microservice.cluster.filestorage.utils.FileStorageUtils.getToken;

import ch.valtech.kubernetes.microservice.cluster.common.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.common.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.common.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class AuditingService {

  private final String persistenceUrl;
  private final RestTemplate restTemplate;

  public AuditingService(@Value("${application.persistence.url}") String persistenceUrl,
      RestTemplate restTemplate) {
    this.persistenceUrl = persistenceUrl;
    this.restTemplate = restTemplate;
  }

  public void audit(String filename, Action action) {
    AuditingRequestDTO auditingRequest = AuditingRequestDTO.builder()
        .filename(filename)
        .action(action).build();
    HttpHeaders headers = populateHeaders();
    HttpEntity<AuditingRequestDTO> request = new HttpEntity<>(auditingRequest, headers);

    postForEntity(request);
  }

  private HttpHeaders populateHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(getToken().get());
    return headers;
  }

  public HttpEntity<? extends Object> postForEntity(HttpEntity<AuditingRequestDTO> httpRequest) {
    try {
      return restTemplate.postForEntity(persistenceUrl, httpRequest, MessageDTO.class);
    } catch (RestClientException e) {
      return handleException(e);
    }
  }

  private ResponseEntity<String> handleException(RestClientException e) {
    if (e instanceof HttpStatusCodeException) {
      return ResponseEntity.status(((HttpStatusCodeException) e).getStatusCode()).build();
    }

    log.error("Failed to audit", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

}
