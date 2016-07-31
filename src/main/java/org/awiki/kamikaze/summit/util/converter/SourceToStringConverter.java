package org.awiki.kamikaze.summit.util.converter;

import org.awiki.kamikaze.summit.domain.Source;
import org.dozer.CustomConverter;
import org.dozer.MappingException;

public class SourceToStringConverter  implements CustomConverter {
    
  @Override
  public Object convert(Object destination, Object src, Class<?> destClass, Class<?> srcClass) {

    if (src == null) {
      return null;
    }

    if(src instanceof Source) {
      // return source identifier
      return ((Source)src).getSource();
    }
    else if (src instanceof java.lang.String) {
      throw new MappingException("Converter SourceToStringConverter cannot perform String to Source mapping.");
    }
    throw new MappingException("Converter SourceToStringConverter "
        + "used incorrectly. Arguments (dest and src) passed in were:"
        + destClass.getName() + " and " + srcClass.getName());
  }

}

