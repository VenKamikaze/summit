package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.SourceMetadataCols;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface SourceProcessorResultTableColumnMapper 
{
  @Mapping(target = "columnName", source = "column.name")
  @Mapping(target = "columnType", source = "column.sqlType")
  @Mapping(target = "columnOrder", source = "column.order")
  SourceMetadataCols map(SourceProcessorResultTable.Column column, @Context CycleAvoidingMappingContext context);
}
