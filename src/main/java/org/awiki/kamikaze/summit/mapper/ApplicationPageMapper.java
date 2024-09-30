package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.dto.render.ApplicationPageDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {ApplicationMapper.class, PageMapper.class}, injectionStrategy = InjectionStrategy.SETTER)
public interface ApplicationPageMapper 
{
  ApplicationPageDto map(ApplicationPage applicationPage, @Context CycleAvoidingMappingContext context);
  ApplicationPage map(ApplicationPageDto applicationPageDto, @Context CycleAvoidingMappingContext context);
  
  void updateDto(ApplicationPage applicationPage, @MappingTarget ApplicationPageDto dto, @Context CycleAvoidingMappingContext context);
  void updateDomain(ApplicationPageDto dto, @MappingTarget ApplicationPage applicationPage, @Context CycleAvoidingMappingContext context);
}
