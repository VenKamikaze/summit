package org.awiki.kamikaze.summit.util.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.awiki.kamikaze.summit.domain.Source;
import org.dozer.CustomConverter;
import org.dozer.MappingException;

public class SetSourceToListStringConverter  implements CustomConverter {
  
    @Override
  public Object convert(Object destination, Object src,
      Class<?> destClass, Class<?> srcClass) {

    if (src == null) {
      return null;
    }

    if (src instanceof java.util.Set<?>) {
      List<String> sources = new ArrayList<>( ((Set) src).size() );
      for(Object o : (Set) src) {
        if(o instanceof org.awiki.kamikaze.summit.domain.Source) {
          sources.add( ((org.awiki.kamikaze.summit.domain.Source)o).getSource() );  
        }
      }
      return sources;
    }
    else if (src instanceof java.util.List<?>) {
      Set<Source> sourceDomains = new HashSet<Source>(  ((List)src).size()  );
      for(Object o : (List) src) {
        if(o instanceof String) {
          sourceDomains.add( (Source) o );
        }
      }
      return sourceDomains;
    }

    throw new MappingException("Converter SetSourceToListStringConverter "
        + "used incorrectly. Arguments passed in were:"
        + destClass.getName() + " and " + srcClass.getName());
  }

}

