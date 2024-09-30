package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.SourceMetadata;
import org.awiki.kamikaze.summit.dto.render.SourceMetadataDto;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "springlazy", uses = {SourceMapper.class, SourceMetadataColsMapper.class} , injectionStrategy = InjectionStrategy.SETTER )
public interface SourceMetadataMapper 
{
  SourceMetadataDto map(SourceMetadata sm, @Context CycleAvoidingMappingContext context);
  SourceMetadata map(SourceMetadataDto dto, @Context CycleAvoidingMappingContext context);
  
  void updateDto(SourceMetadata template, @MappingTarget SourceMetadataDto dto, @Context CycleAvoidingMappingContext context);
  void updateDomain(SourceMetadataDto dto, @MappingTarget SourceMetadata template, @Context CycleAvoidingMappingContext context);
}
