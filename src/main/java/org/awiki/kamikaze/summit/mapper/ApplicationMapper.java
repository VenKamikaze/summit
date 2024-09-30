package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Application;
import org.awiki.kamikaze.summit.dto.render.ApplicationDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ApplicationPageMapper.class}, injectionStrategy = InjectionStrategy.SETTER )
public interface ApplicationMapper 
{
  ApplicationDto applicationToApplicationDto(Application application, @Context CycleAvoidingMappingContext context);
  
  @Mapping(target = "applicationSchemases", ignore = true) // Assuming we don't map this field to DTO
  Application applicationDtoToApplication(ApplicationDto applicationDto, @Context CycleAvoidingMappingContext context);
}
