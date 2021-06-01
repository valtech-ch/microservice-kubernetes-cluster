package ch.valtech.kubernetes.microservice.cluster.filestorage.client;

import ch.valtech.kubernetes.microservice.cluster.filestorage.util.SecurityUtils;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientRequest.Builder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class ReactivePersistenceClient {

  private final WebClient client;

  public ReactivePersistenceClient(@Value("${application.persistence.url}") String persistenceUrl) {
    this.client = WebClient.builder()
        .baseUrl(persistenceUrl)
        .filter((request, next) -> {
          Builder requestBuilder = ClientRequest.from(request);
          SecurityUtils.getJwt().ifPresent(token -> requestBuilder.headers(headers -> headers.setBearerAuth(token)));
          return next.exchange(requestBuilder.build());
        }).build();
  }

  public Flux<MessageDto> getMessagesByFilename(SearchRequest searchRequest) {
    return client.get()
        .uri("/" + searchRequest.getFilename(), searchRequest.getFilename(), searchRequest.getLimit())
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(MessageDto.class))
        .log("Messages fetched : ");
  }

}
