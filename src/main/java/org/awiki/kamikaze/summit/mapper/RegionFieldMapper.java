package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.RegionField;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {RegionMapper.class, FieldMapper.class}, 
  injectionStrategy = InjectionStrategy.SETTER)
public interface RegionFieldMapper 
{
  RegionFieldDto map(RegionField region, @Context CycleAvoidingMappingContext context);
  RegionField map(RegionFieldDto dto, @Context CycleAvoidingMappingContext context);
  
  void updateDto(RegionField region, @MappingTarget RegionFieldDto dto, @Context CycleAvoidingMappingContext context);
  void updateDomain(RegionFieldDto dto, @MappingTarget RegionField region, @Context CycleAvoidingMappingContext context);
}
