package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Conditional;
import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {SourceMapper.class, CodeTableMapper.class}, injectionStrategy = InjectionStrategy.SETTER)
public interface ConditionalMapper 
{
  @Mapping(target="processedSource", ignore = true)
  ConditionalDto map(Conditional c, @Context CycleAvoidingMappingContext context);
  
  @Mapping(target="sourceTypeCode", ignore = true)
  @Mapping(target="codeConditionalType", ignore = true)
  Conditional map(ConditionalDto dto, @Context CycleAvoidingMappingContext context);
  
  @Mapping(target="processedSource", ignore = true)
  void updateDto(Conditional c, @MappingTarget ConditionalDto dto, @Context CycleAvoidingMappingContext context);

  @Mapping(target="sourceTypeCode", ignore = true)
  @Mapping(target="codeConditionalType", ignore = true)
  void updateDomain(ConditionalDto dto, @MappingTarget Conditional c, @Context CycleAvoidingMappingContext context);
}
