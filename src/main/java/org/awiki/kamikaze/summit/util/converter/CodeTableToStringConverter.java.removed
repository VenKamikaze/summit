package org.awiki.kamikaze.summit.util.converter;

import org.awiki.kamikaze.summit.domain.codetable.CodeTable;
import org.dozer.CustomConverter;
import org.dozer.MappingException;

public class CodeTableToStringConverter  implements CustomConverter {
    
  @Override
  public Object convert(Object destination, Object src, Class<?> destClass, Class<?> srcClass) {

    if (src == null) {
      return null;
    }

    if (src instanceof CodeTable) {
      return ((CodeTable)src).getCode();
    }
    else if (src instanceof java.lang.String) {
      throw new MappingException("Converter CodeTableToStringConverter cannot perform String to Code mapping yet.");
    }

    throw new MappingException("Converter SetSourceToListStringConverter "
        + "used incorrectly. Arguments passed in were:"
        + destClass.getName() + " and " + srcClass.getName());
  }

}

