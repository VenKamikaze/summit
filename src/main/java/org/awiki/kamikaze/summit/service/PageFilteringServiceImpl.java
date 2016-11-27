package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.domain.PageRegion;
import org.awiki.kamikaze.summit.dto.entry.PageDto;
import org.awiki.kamikaze.summit.dto.entry.PageRegionDto;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.mapper.PageToPageDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.awiki.kamikaze.summit.domain.Region.REGION_TYPE_REPORT;

public class PageFilteringServiceImpl implements PageFilteringService
{
  @Autowired
  private ApplicationPageRepository appPageStore;

  @Autowired
  private PageToPageDtoMapper pageMapper;

  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Override
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, String searchText)
  {
    final PageDto pageDto = pageMapper.map(appPageStore.findByApplicationIdAndPageId(applicationId, pageId).getPage());
    
    int count = 0;
    
    for(PageRegionDto pr : pageDto.getPageRegions()) {
      if (REGION_TYPE_REPORT.equals(pr.getRegionDto().getCodeRegionType())) {
        count++;
       // ReportSourceProcessorService reportService = (ReportSourceProcessorService) sourceProcessors.getSourceProcessorService(regionDto.getCodeSourceType());
       // SourceProcessorResultTable resultTable = reportService.querySource(regionDto.getSource().iterator().next(), null);
       // resultTable.setTemplateDto(regionDto.getTemplateDto());
       // regionItems.add(resultTable);
      }
    }
    
    if(count > 1) {
      throw new RuntimeException("Cannot use this method to search over two report regions. Please use method identifying regionId instead.");
    }
    
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, String columnName,
          String searchText)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
