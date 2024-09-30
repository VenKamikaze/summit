package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Template;
import org.awiki.kamikaze.summit.dto.edit.EditTemplateDto;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface TemplateMapper 
{
  @Mapping(target = "pages", ignore = true) // FIXME should pages even be on TemplateDto?
  TemplateDto map(Template template, @Context CycleAvoidingMappingContext context);
  Template map(TemplateDto dto, @Context CycleAvoidingMappingContext context);
  
  EditTemplateDto mapEditDto(Template template, @Context CycleAvoidingMappingContext context);
  
  @Mapping(target = "pages", ignore = true) // FIXME should pages even be on TemplateDto?
  void updateDto(Template template, @MappingTarget TemplateDto dto, @Context CycleAvoidingMappingContext context);
  void updateDomain(TemplateDto dto, @MappingTarget Template template, @Context CycleAvoidingMappingContext context);
  
  void updateEditDto(Template template, @MappingTarget EditTemplateDto dto, @Context CycleAvoidingMappingContext context);
}
