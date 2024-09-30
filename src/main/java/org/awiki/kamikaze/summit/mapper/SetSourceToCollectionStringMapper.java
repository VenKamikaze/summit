package org.awiki.kamikaze.summit.mapper;

import java.util.List;
import java.util.Set;

import org.awiki.kamikaze.summit.domain.Source;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface SetSourceToCollectionStringMapper 
{
  List<String> mapList(Set<Source> sources);
  Set<String> mapSet(Set<Source> sources);
  default String mapSourceToString(Source s) {
    return s.getSource();
  }
}
