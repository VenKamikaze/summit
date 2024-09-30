package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.PageProcessingSource;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {PageProcessingMapper.class, PageProcessingSourceSelectMapper.class, CodeTableMapper.class}, 
  injectionStrategy = InjectionStrategy.SETTER)
public interface PageProcessingSourceMapper 
{
  @Mapping(target = "codeSourceType", source = "codeSourceType.code")
  @Mapping(target = "source", source = "source.source")
  PageProcessingSourceDto map(PageProcessingSource ppSource, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "source", ignore = true)
  @Mapping(target = "codeSourceType", ignore = true)
  PageProcessingSource map(PageProcessingSourceDto dto, @Context CycleAvoidingMappingContext context);

  @Mapping(target = "codeSourceType", source = "codeSourceType.code")
  @Mapping(target = "source", source = "source.source")
  void updateDto(PageProcessingSource ppSource, @MappingTarget PageProcessingSourceDto dto, @Context CycleAvoidingMappingContext context);
  
  @Mapping(target = "source", ignore = true)
  @Mapping(target = "codeSourceType", ignore = true)
  void updateDomain(PageProcessingSourceDto dto, @MappingTarget PageProcessingSource ppSource, @Context CycleAvoidingMappingContext context);
}
