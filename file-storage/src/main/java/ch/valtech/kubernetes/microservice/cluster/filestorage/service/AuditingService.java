package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import reactor.core.publisher.Flux;

public interface AuditingService {

  MessageDto audit(String filename, Action action);

  Flux<MessageDto> search(SearchRequest searchRequest);

}
