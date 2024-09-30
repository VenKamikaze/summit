package org.awiki.kamikaze.summit.util.converter;

import org.awiki.kamikaze.summit.domain.codetable.CodeSourceType;
import org.awiki.kamikaze.summit.domain.codetable.CodeTable;
import org.dozer.CustomConverter;
import org.dozer.MappingException;

public class CodeSourceTypeToStringConverter  implements CustomConverter {
    
  @Override
  public Object convert(Object destination, Object src, Class<?> destClass, Class<?> srcClass) {

    if (src == null) {
      return null;
    }

    if(src instanceof CodeSourceType) {
      // return source identifier
      return ((CodeSourceType)src).getSourceIdentifier();
    }
    else if (src instanceof java.lang.String) {
      throw new MappingException("Converter CodeSourceTypeToStringConverter cannot perform String to Code mapping yet.");
    }
    else if (src instanceof CodeTable) {
      return ((CodeTable)src).getCode();
    }
    throw new MappingException("Converter CodeSourceTypeToStringConverter "
        + "used incorrectly. Arguments passed in were:"
        + destClass.getName() + " and " + srcClass.getName());
  }

}

