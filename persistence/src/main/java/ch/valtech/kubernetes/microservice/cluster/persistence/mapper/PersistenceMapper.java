package ch.valtech.kubernetes.microservice.cluster.persistence.mapper;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing.AuditingBuilder;
import java.time.LocalDate;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
public interface PersistenceMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "modificationDate", ignore = true)
  @Mapping(target = "username", source = "username")
  @Mapping(target = "filename", source = "auditingRequestDto.filename")
  @Mapping(target = "action", source = "auditingRequestDto.action")
  Auditing toAuditing(AuditingRequestDto auditingRequestDto, String username);

  @AfterMapping
  default void updateAuditingModificationDate(@MappingTarget AuditingBuilder auditingBuilder,
      AuditingRequestDto auditingRequestDto, String username) {
    auditingBuilder.modificationDate(now());
  }

  // Protobuf fields can be ignored
  @Mapping(target = "mergeFrom", ignore = true)
  @Mapping(target = "clearField", ignore = true)
  @Mapping(target = "clearOneof", ignore = true)
  @Mapping(target = "messageBytes", ignore = true)
  @Mapping(target = "unknownFields", ignore = true)
  @Mapping(target = "mergeUnknownFields", ignore = true)
  @Mapping(target = "allFields", ignore = true)
  // Payload fields
  @Mapping(target = "message", source = "message")
  MessageResponse toMessageResponse(MessageDto messageDto);

  @Mapping(target = "filename", source = "filename")
  @Mapping(target = "action", source = "action")
  AuditingRequestDto toAuditingRequestDto(AuditingRequest auditingRequest);

  // TODO replace target to MappingConstants.THROW_EXCEPTION in version 1.5
  @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.NULL)
  Action toAction(AuditingRequest.Action action);

  @Named("now")
  default LocalDate now() {
    return LocalDate.now();
  }
}
