package org.awiki.kamikaze.summit.service.edit;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.dto.edit.EditPageDto;
import org.awiki.kamikaze.summit.exception.ResourceNotFoundException;
import org.awiki.kamikaze.summit.mapper.PageMapper;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.util.DebugUtils;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class PageEditServiceImpl implements PageEditService
{
  private PageMapper pageMapper;
  private ApplicationPageRepository appPageStore;
  
  @Autowired
  public void setPageMapper(@Lazy PageMapper pageMapper) {
    this.pageMapper = pageMapper;
  }

  @Autowired
  public void setAppPageStore(ApplicationPageRepository appPageStore) {
    this.appPageStore = appPageStore;
  }

  /**
   * Loads the 
   * Maps the page domain to the dto, .... nothing else yet implemented!
   */
  @Override
  public EditPageDto loadPage(long applicationId, long pageId)
  {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    
    if(appPage == null || appPage.getPage() == null)
    {
      throw new ResourceNotFoundException("Could not find edit page for application: " + applicationId + " and page: " + pageId);
    }
    //PageDto pageDto = mapper.map(appPage.getPage(), PageDto.class);
    EditPageDto pageDto = pageMapper.mapEditDto(appPage.getPage(), new CycleAvoidingMappingContext());
    
    DebugUtils.debugObjectGetters(pageDto);
    
    return pageDto;
  }

}
