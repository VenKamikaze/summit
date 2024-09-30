package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.Region;
import org.awiki.kamikaze.summit.dto.edit.EditRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {SetSourceToCollectionStringMapper.class, PageRegionMapper.class, RegionFieldMapper.class, TemplateMapper.class, ConditionalMapper.class, CodeTableMapper.class}
  , injectionStrategy = InjectionStrategy.SETTER)
public interface RegionMapper
{
  //CodeTableMapper covers it: @Mapping(source = "codeRegionPosition.code", target = "codeRegionPosition")
  RegionDto map(Region region, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeRegionPosition", ignore = true)
  @Mapping(target = "codeRegionType", ignore = true)
  @Mapping(target = "source", ignore = true)
  @Mapping(target = "codeSourceType", ignore = true)
  Region map(RegionDto dto, @Context CycleAvoidingMappingContext context);
  
  //CodeTableMapper covers it: @Mapping(source = "codeRegionPosition.code", target = "codeRegionPosition")
  EditRegionDto mapToEditDto(Region region);
  
  void updateDto(Region region, @MappingTarget RegionDto dto, @Context CycleAvoidingMappingContext context);
  @Mapping(target = "codeRegionPosition", ignore = true)
  @Mapping(target = "codeRegionType", ignore = true)
  @Mapping(target = "source", ignore = true)
  @Mapping(target = "codeSourceType", ignore = true)
  void updateDomain(RegionDto dto, @MappingTarget Region region, @Context CycleAvoidingMappingContext context);
  
  //CodeTableMapper covers it: @Mapping(source = "codeRegionPosition.code", target = "codeRegionPosition")
  void updateEditDto(Region region, @MappingTarget EditRegionDto dto, @Context CycleAvoidingMappingContext context);
}
