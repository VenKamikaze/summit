package org.awiki.kamikaze.summit.util.mapper;

import java.util.HashSet;
import java.util.Set;

import org.awiki.kamikaze.summit.domain.Field;
import org.awiki.kamikaze.summit.domain.PageRegion;
import org.awiki.kamikaze.summit.dto.render.ApplicationPageDto;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingDto;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.util.DebugUtils;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FieldToFieldDtoMapper
{
  @Autowired
  private Mapper mapper;

  private static final Logger logger = LoggerFactory.getLogger(FieldToFieldDtoMapper.class);
  
  public FieldDto map(Field field)
  {
    logger.info("in FieldToFieldDtoMapper");
    final FieldDto dto = mapper.map(field, FieldDto.class);

    // TODO FIXME we are mapping from Set<Source> to a singular string source.
    if(field.getSource() == null || (field.getSource().isEmpty()))
    {
      dto.setSource(null);
    }
    
    DebugUtils.debugObjectGetters(dto);
    
    dto.setCodeFieldSourceType(field.getCodeFieldSourceTypeBySourceType().getCode());
    dto.setCodeFieldDefaultValueSourceType(field.getCodeFieldSourceTypeByDefaultValueType() != null ? field.getCodeFieldSourceTypeByDefaultValueType().getCode() : null);
    return dto;
  }
  
  public <S, D> Set<D> mapSet(Set<S> source, Class<D> dest)
  {
    try
    {
      Set<D> destSet = new HashSet<D>(source.size());
      for (S src : source)
      {
        destSet.add( mapper.map(src, dest));
      }
      return destSet;
    }
    catch(MappingException e){
      throw new RuntimeException("Unable to map from " + source.toArray()[0].getClass().getCanonicalName() + " to: " + dest.getCanonicalName(), e);
    }
  }
  
}
