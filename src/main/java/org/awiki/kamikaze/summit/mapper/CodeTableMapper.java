package org.awiki.kamikaze.summit.mapper;

import org.awiki.kamikaze.summit.domain.codetable.CodeTable;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import lombok.NonNull;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface CodeTableMapper 
{
  default String mapToString(@NonNull final CodeTable ct) {
    return ct.getCode();
  }
}
