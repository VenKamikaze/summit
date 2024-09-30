package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.PageProcessing;
import org.awiki.kamikaze.summit.dto.render.PageProcessingDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {PageMapper.class, PageProcessingSourceMapper.class, ConditionalMapper.class, CodeTableMapper.class}, 
  injectionStrategy = InjectionStrategy.SETTER)
public interface PageProcessingMapper 
{
  PageProcessingDto map(PageProcessing page, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeProcessingType", ignore = true)
  PageProcessing map(PageProcessingDto dto, @Context CycleAvoidingMappingContext context);
  
  void updateDto(PageProcessing page, @MappingTarget PageProcessingDto dto, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeProcessingType", ignore = true)
  void updateDomain(PageProcessingDto dto, @MappingTarget PageProcessing page, @Context CycleAvoidingMappingContext context);
}
