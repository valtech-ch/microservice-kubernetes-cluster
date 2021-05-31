package ch.valtech.kubernetes.microservice.cluster.persistence.mapper;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import java.time.LocalDate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PersistenceMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "modificationDate", ignore = true)
  @Mapping(target = "username", source = "username")
  @Mapping(target = "filename", source = "auditingRequestDto.filename")
  @Mapping(target = "action", source = "auditingRequestDto.action")
  Auditing toAuditing(AuditingRequestDto auditingRequestDto, String username);

  @AfterMapping
  default void toAuditing(@MappingTarget Auditing auditing, AuditingRequestDto auditingRequestDto, String username) {
    auditing.setModificationDate(now());
  }

  Action toDomainAction(ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action action);

  default MessageResponse toMessageResponse(MessageDto messageDto){
    return MessageResponse.newBuilder()
        .setMessage(messageDto.getMessage())
        .build();
  }

  default AuditingRequestDto toAuditingRequestDto(AuditingRequest auditingRequest) {
    return AuditingRequestDto.builder()
        .filename(auditingRequest.getFilename())
        .action(
            ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action.valueOf(auditingRequest.getAction().toString()))
        .build();
  }

  @Named("now")
  default LocalDate now() {
    return LocalDate.now();
  }
}
