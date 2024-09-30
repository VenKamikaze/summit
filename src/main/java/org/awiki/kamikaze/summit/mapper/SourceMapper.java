package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Source;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {RegionMapper.class, FieldMapper.class, PageProcessingSourceMapper.class, SourceMetadataMapper.class},
   injectionStrategy = InjectionStrategy.SETTER)
public interface SourceMapper 
{
  SourceDto map(Source src, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "conditional", ignore = true) // FIXME? This isn't on the DTO?
  Source map(SourceDto dto, @Context CycleAvoidingMappingContext context);
  
  void updateDto(Source src, @MappingTarget SourceDto dto, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "conditional", ignore = true) // 
  void updateDomain(SourceDto dto, @MappingTarget Source src, @Context CycleAvoidingMappingContext context);
}
