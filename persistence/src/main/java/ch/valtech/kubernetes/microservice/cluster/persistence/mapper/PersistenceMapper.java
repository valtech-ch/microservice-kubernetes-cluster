package ch.valtech.kubernetes.microservice.cluster.persistence.mapper;

import ch.valtech.kubernetes.microservice.cluster.common.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersistenceMapper {

  
  @Mapping(target = "modificationDate", expression = "java(java.time.LocalDate.now())")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "filename", source = "auditingRequestDTO.filename")
  @Mapping(target = "action", source = "auditingRequestDTO.action")
  Auditing toAuditing(AuditingRequestDto auditingRequestDTO, String username);

  Action toDomainAction(ch.valtech.kubernetes.microservice.cluster.common.dto.Action action);

}
