package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Field;
import org.awiki.kamikaze.summit.dto.render.DropDownFieldDto;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring", uses = {SourceMapper.class, RegionFieldMapper.class, TemplateMapper.class, LabelMapper.class, ConditionalMapper.class},
  injectionStrategy = InjectionStrategy.SETTER)
public interface FieldMapper
{
  public static final String DROPDOWN_FIELD_TYPE_CODE = "DROPDOWN";

  /**
   * Field processors and formatters are dispatched on the DTO class canonical
   * name, so DROPDOWN fields must map to the DropDownFieldDto subclass or they
   * fall through to SimpleFieldProcessorServiceImpl, which cannot process a
   * tabular (dml_select) field source.
   */
  @ObjectFactory
  default FieldDto createFieldDto(Field field) {
    if (field != null && field.getCodeFieldType() != null
        && DROPDOWN_FIELD_TYPE_CODE.equals(field.getCodeFieldType().getCode())) {
      return new DropDownFieldDto();
    }
    return new FieldDto();
  }

  @Mapping(source = "codeFieldType.code", target = "codeFieldType")
  @Mapping(source = "codeFieldSourceTypeBySourceType.code", target = "codeFieldSourceType")
  @Mapping(source = "codeFieldSourceTypeByDefaultValueType.code", target = "codeFieldDefaultValueSourceType")
  @Mapping(target = "postProcessedSource", ignore = true)
  @Mapping(target = "postProcessedDefaultValue", ignore = true)
  @Mapping(target = "childPageItems", ignore = true)
  @Mapping(target = "customReplacementVariables", ignore = true)
  FieldDto map(Field field, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeFieldType", ignore = true)
  @Mapping(target = "codeFieldSourceTypeBySourceType", ignore = true)
  @Mapping(target = "codeFieldSourceTypeByDefaultValueType", ignore = true)
  Field map(FieldDto dto, @Context CycleAvoidingMappingContext context);

  @Mapping(source = "codeFieldType.code", target = "codeFieldType")
  @Mapping(source = "codeFieldSourceTypeBySourceType.code", target = "codeFieldSourceType")
  @Mapping(source = "codeFieldSourceTypeByDefaultValueType.code", target = "codeFieldDefaultValueSourceType")
  @Mapping(target = "postProcessedSource", ignore = true)
  @Mapping(target = "postProcessedDefaultValue", ignore = true)
  @Mapping(target = "childPageItems", ignore = true)
  @Mapping(target = "customReplacementVariables", ignore = true)
  void updateDto(Field field, @MappingTarget FieldDto dto, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeFieldType", ignore = true)
  @Mapping(target = "codeFieldSourceTypeBySourceType", ignore = true)
  @Mapping(target = "codeFieldSourceTypeByDefaultValueType", ignore = true)
  void updateDomain(FieldDto dto, @MappingTarget Field field, @Context CycleAvoidingMappingContext context);
}
