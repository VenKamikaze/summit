package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.SourceMetadataCols;
import org.awiki.kamikaze.summit.dto.render.SourceMetadataColsDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {SourceMetadataMapper.class}, 
  injectionStrategy = InjectionStrategy.SETTER)
public interface SourceMetadataColsMapper 
{
  SourceMetadataColsDto map(SourceMetadataCols smc, @Context CycleAvoidingMappingContext context);
  SourceMetadataCols map(SourceMetadataColsDto dto, @Context CycleAvoidingMappingContext context);
  
  void updateDto(SourceMetadataCols smc, @MappingTarget SourceMetadataColsDto dto, @Context CycleAvoidingMappingContext context);
  void updateDomain(SourceMetadataColsDto dto, @MappingTarget SourceMetadataCols smc, @Context CycleAvoidingMappingContext context);
}
