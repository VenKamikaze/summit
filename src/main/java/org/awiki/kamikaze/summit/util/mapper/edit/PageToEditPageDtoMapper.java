package org.awiki.kamikaze.summit.util.mapper.edit;

import java.util.HashSet;
import java.util.Set;

import org.awiki.kamikaze.summit.domain.Page;
import org.awiki.kamikaze.summit.domain.PageRegion;
import org.awiki.kamikaze.summit.dto.render.ApplicationPageDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
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
  
  public PageDto map(Page page)
  {
    logger.info("in EditPageToPageDtoMapper");
    PageDto pageDto = mapper.map(page, PageDto.class);
    pageDto.getApplicationPages().addAll(mapSet(page.getApplicationPages(), ApplicationPageDto.class));

    DebugUtils.debugObjectGetters(pageDto);
    
    if(pageDto.getPageRegions().size() > 0)
    {
      // TODO optimisation candidate
      for(PageRegionDto dto : pageDto.getPageRegions())
      {
        for(java.util.Iterator<PageRegion> it = page.getPageRegions().iterator(); it.hasNext();)
        {
          PageRegion pr = it.next();
          if(pr.getId() == dto.getId())
          {
            DebugUtils.debugObjectGetters(dto);
            dto.setRegionDto(mapper.map(pr.getRegion(), RegionDto.class));
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
