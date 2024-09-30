package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.PageRegion;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.awiki.kamikaze.summit.dto.edit.EditPageRegionDto;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {PageMapper.class, RegionMapper.class}, injectionStrategy = InjectionStrategy.SETTER)
public interface PageRegionMapper 
{
  @Mapping(source = "page", target = "pageDto")
  @Mapping(source = "region", target = "regionDto")
  PageRegionDto map(PageRegion page, @Context CycleAvoidingMappingContext context);
  
  @Mapping(source = "pageDto", target = "page")
  @Mapping(source = "regionDto", target = "region")
  PageRegion map(PageRegionDto dto, @Context CycleAvoidingMappingContext context);

  @Mapping(source = "page", target = "pageDto")
  @Mapping(source = "region", target = "regionDto")
  EditPageRegionDto mapEditDto(PageRegion page, @Context CycleAvoidingMappingContext context);
  
  @Mapping(source = "page", target = "pageDto")
  @Mapping(source = "region", target = "regionDto")
  void updateDto(PageRegion page, @MappingTarget PageRegionDto dto, @Context CycleAvoidingMappingContext context);
  
  @Mapping(source = "pageDto", target = "page")
  @Mapping(source = "regionDto", target = "region")
  void updateDomain(PageRegionDto dto, @MappingTarget PageRegion page, @Context CycleAvoidingMappingContext context);
  
  @Mapping(source = "page", target = "pageDto")
  @Mapping(source = "region", target = "regionDto")
  void updateEditDto(PageRegion page, @MappingTarget EditPageRegionDto dto, @Context CycleAvoidingMappingContext context);
}
