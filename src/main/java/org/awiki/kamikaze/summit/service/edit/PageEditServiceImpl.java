package org.awiki.kamikaze.summit.service.edit;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.dto.edit.EditPageDto;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.util.DebugUtils;
import org.awiki.kamikaze.summit.util.mapper.edit.PageToEditPageDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageEditServiceImpl implements PageEditService
{
  @Autowired
  private PageToEditPageDtoMapper pageMapper;

  @Autowired
  private ApplicationPageRepository appPageStore;
  
  /**
   * Loads the 
   * Maps the page domain to the dto, .... nothing else yet implemented!
   */
  @Override
  public EditPageDto loadPage(long applicationId, long pageId)
  {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    //PageDto pageDto = mapper.map(appPage.getPage(), PageDto.class);
    EditPageDto pageDto = pageMapper.map(appPage.getPage());
    
    DebugUtils.debugObjectGetters(pageDto);
    
    return pageDto;
  }

}
