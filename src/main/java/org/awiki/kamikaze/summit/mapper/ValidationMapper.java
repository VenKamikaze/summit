package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Validation;
import org.awiki.kamikaze.summit.dto.render.ValidationDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {PageMapper.class, SourceMapper.class, ConditionalMapper.class, CodeTableMapper.class},
  injectionStrategy = InjectionStrategy.SETTER)
public interface ValidationMapper
{
  // Code table refs map via dotted source paths (null-safe): SOURCE_TYPE_CODE
  // is null for NOT_NULL validations, and CodeTableMapper rejects nulls.
  @Mapping(target = "codeValidationType", source = "codeValidationType.code")
  @Mapping(target = "sourceTypeCode", source = "sourceTypeCode.code")
  ValidationDto map(Validation validation, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeValidationType", ignore = true)
  @Mapping(target = "sourceTypeCode", ignore = true)
  Validation map(ValidationDto dto, @Context CycleAvoidingMappingContext context);

  @Mapping(target = "codeValidationType", source = "codeValidationType.code")
  @Mapping(target = "sourceTypeCode", source = "sourceTypeCode.code")
  void updateDto(Validation validation, @MappingTarget ValidationDto dto, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeValidationType", ignore = true)
  @Mapping(target = "sourceTypeCode", ignore = true)
  void updateDomain(ValidationDto dto, @MappingTarget Validation validation, @Context CycleAvoidingMappingContext context);
}
