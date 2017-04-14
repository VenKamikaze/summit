package org.awiki.kamikaze.summit.util.mapper;

import org.dozer.Mapper;
import org.dozer.MappingException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Basic type mapper that assumes all fields must match between source and dest objects.
 * @author msaun
 *
 */
public class GenericMapper<S,D>
{
  @Autowired
  private Mapper mapper;

  public <S, D> D map(S source, Class<D> dest)
  {
    try
    {
      D d = mapper.map(source, dest);

      return d;
    }
    catch(MappingException e){
      throw new RuntimeException("Unable to map from " + source.getClass().getCanonicalName() + " to: " + dest.getCanonicalName(), e);
    }
  }
}
