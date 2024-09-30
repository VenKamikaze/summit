package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.dto.render.DropDownOptionItemDto;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import lombok.NonNull;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface SourceProcessorResultTableRowMapper 
{
  default DropDownOptionItemDto map(@NonNull SourceProcessorResultTable.Row row) {
    return new DropDownOptionItemDto(row.getCell(0).getValue(), row.getCell(1).getValue());
  }
}
