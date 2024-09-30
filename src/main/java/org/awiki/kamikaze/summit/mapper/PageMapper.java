package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Page;
import org.awiki.kamikaze.summit.dto.edit.EditPageDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PageRegionMapper.class, TemplateMapper.class, PageProcessingMapper.class, ApplicationPageMapper.class}, 
  injectionStrategy = InjectionStrategy.SETTER )
public interface PageMapper 
{
  @Mapping(target = "pageRenderPreRegionPageProcessings", ignore = true)
  @Mapping(target = "pagePostProcessings", ignore = true)
  @Mapping(target = "pagePostProcessingBranches", ignore = true)
  @Mapping(target = "customReplacementVariables", ignore = true)
  @Mapping(target = "childPageItems", ignore = true)
  PageDto map(Page page, @Context CycleAvoidingMappingContext context);
  Page map(PageDto dto, @Context CycleAvoidingMappingContext context);

  EditPageDto mapEditDto(Page page, @Context CycleAvoidingMappingContext context);

  
  @Mapping(target = "pageRenderPreRegionPageProcessings", ignore = true)
  @Mapping(target = "pagePostProcessings", ignore = true)
  @Mapping(target = "pagePostProcessingBranches", ignore = true)
  @Mapping(target = "customReplacementVariables", ignore = true)
  @Mapping(target = "childPageItems", ignore = true)
  void updateDto(Page page, @MappingTarget PageDto dto, @Context CycleAvoidingMappingContext context);
  void updateDomain(PageDto dto, @MappingTarget Page page, @Context CycleAvoidingMappingContext context);
  
  void updateEditDto(Page page, @MappingTarget EditPageDto dto, @Context CycleAvoidingMappingContext context);

}
