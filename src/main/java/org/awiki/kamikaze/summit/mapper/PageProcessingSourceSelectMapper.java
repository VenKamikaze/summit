package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.PageProcessingSourceSelect;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PageProcessingMapper.class, PageProcessingSourceMapper.class, SourceMapper.class}, 
  injectionStrategy = InjectionStrategy.SETTER)
public interface PageProcessingSourceSelectMapper {

  @Mapping(target = "fieldValue", ignore = true) // This field is not present in the entity
  PageProcessingSourceSelectDto map(PageProcessingSourceSelect page, @Context CycleAvoidingMappingContext context);
  
  @Mapping(target = "pageProcessingSource", source = "pageProcessingSource")
  PageProcessingSourceSelect map(PageProcessingSourceSelectDto dto, @Context CycleAvoidingMappingContext context);

  @Mapping(target = "fieldValue", ignore = true) // This field is not present in the entity
  void updateDto(PageProcessingSourceSelect page, @MappingTarget PageProcessingSourceSelectDto dto, @Context CycleAvoidingMappingContext context);
  
  @Mapping(target = "pageProcessingSource", source = "pageProcessingSource")
  void updateDomain(PageProcessingSourceSelectDto dto, @MappingTarget PageProcessingSourceSelect page, @Context CycleAvoidingMappingContext context);
}