package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Label;
import org.awiki.kamikaze.summit.dto.render.LabelDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {FieldMapper.class, TemplateMapper.class, ConditionalMapper.class, CodeTableMapper.class}
  , injectionStrategy = InjectionStrategy.SETTER)
public interface LabelMapper 
{
  @Mapping(target = "customReplacementVariables", ignore = true)
  @Mapping(target = "childPageItems", ignore = true)
  LabelDto map(Label l, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeLabelType", ignore = true)
  Label map(LabelDto dto, @Context CycleAvoidingMappingContext context);

  @Mapping(target = "customReplacementVariables", ignore = true)
  @Mapping(target = "childPageItems", ignore = true)
  void updateDto(Label l, @MappingTarget LabelDto dto, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeLabelType", ignore = true)
  void updateDomain(LabelDto dto, @MappingTarget Label l, @Context CycleAvoidingMappingContext context);
}
