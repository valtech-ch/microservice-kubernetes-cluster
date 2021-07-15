package ch.valtech.kubernetes.microservice.cluster.filestorage.mapper;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditingMapper {

  // Protobuf fields can be ignored
  @Mapping(target = "mergeFrom", ignore = true)
  @Mapping(target = "clearField", ignore = true)
  @Mapping(target = "clearOneof", ignore = true)
  @Mapping(target = "filenameBytes", ignore = true)
  @Mapping(target = "actionValue", ignore = true)
  @Mapping(target = "unknownFields", ignore = true)
  @Mapping(target = "mergeUnknownFields", ignore = true)
  @Mapping(target = "allFields", ignore = true)
  // Payload fields
  @Mapping(target = "filename", source = "filename")
  @Mapping(target = "action", source = "action")
  AuditingRequest toAuditingRequest(String filename, Action action);

  @Mapping(target = "filename", source = "filename")
  @Mapping(target = "action", source = "action")
  AuditingRequestDto toAuditingRequestDto(String filename, Action action);

  @Mapping(target = "message", source = "message")
  MessageDto toMessageDto(MessageResponse messageResponse);

}
