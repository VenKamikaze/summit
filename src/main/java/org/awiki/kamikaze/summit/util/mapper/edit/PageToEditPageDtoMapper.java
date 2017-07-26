package org.awiki.kamikaze.summit.util.mapper.edit;

import java.util.HashSet;
import java.util.Set;

import org.awiki.kamikaze.summit.domain.Page;
import org.awiki.kamikaze.summit.domain.PageRegion;
import org.awiki.kamikaze.summit.dto.edit.EditApplicationPageDto;
import org.awiki.kamikaze.summit.dto.edit.EditPageDto;
import org.awiki.kamikaze.summit.dto.edit.EditPageRegionDto;
import org.awiki.kamikaze.summit.dto.edit.EditRegionDto;
import org.awiki.kamikaze.summit.util.DebugUtils;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageToEditPageDtoMapper
{
  @Autowired
  private Mapper mapper;

  private static final Logger logger = LoggerFactory.getLogger(PageToEditPageDtoMapper.class);
  
  public EditPageDto map(Page page)
  {
    logger.info("in EditPageToPageDtoMapper");
    EditPageDto pageDto = mapper.map(page, EditPageDto.class);
    pageDto.getApplicationPages().addAll(mapSet(page.getApplicationPages(), EditApplicationPageDto.class));

    DebugUtils.debugObjectGetters(pageDto);
    
    if(pageDto.getPageRegions().size() > 0)
    {
      // TODO optimisation candidate
      for(EditPageRegionDto dto : pageDto.getPageRegions())
      {
        for(java.util.Iterator<PageRegion> it = page.getPageRegions().iterator(); it.hasNext();)
        {
          PageRegion pr = it.next();
          if(pr.getId() == dto.getId())
          {
            DebugUtils.debugObjectGetters(dto);
            dto.setRegionDto(mapper.map(pr.getRegion(), EditRegionDto.class));
            DebugUtils.debugObjectGetters(dto.getRegionDto());
            break;
          }
        }
      }
    }
    return pageDto;
  }
  
  public <S, D> Set<D> mapSet(Set<S> source, Class<D> dest)
  {
    try
    {
      Set<D> destSet = new HashSet<D>(source.size());
      for (S src : source)
      {
        destSet.add( (D) mapper.map(src, dest));
      }
      return destSet;
    }
    catch(MappingException e){
      throw new RuntimeException("Unable to map from " + source.toArray()[0].getClass().getCanonicalName() + " to: " + dest.getCanonicalName(), e);
    }
  }
  
}
