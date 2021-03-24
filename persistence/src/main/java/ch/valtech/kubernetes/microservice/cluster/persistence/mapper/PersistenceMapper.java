package ch.valtech.kubernetes.microservice.cluster.persistence.mapper;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
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

  @Mapping(target = "username", source = "username")
  @Mapping(target = "filename", source = "auditingRequestDTO.filename")
  @Mapping(target = "action", source = "auditingRequestDTO.action")
  Auditing toAuditing(AuditingRequestDto auditingRequestDTO, String username);

  @AfterMapping
  default void toAuditing(@MappingTarget Auditing auditing, AuditingRequestDto auditingRequestDTO, String username) {
    auditing.setModificationDate(now());
  }

  Action toDomainAction(ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action action);

  @Named("now")
  default LocalDate now() {
    return LocalDate.now();
  }
}
