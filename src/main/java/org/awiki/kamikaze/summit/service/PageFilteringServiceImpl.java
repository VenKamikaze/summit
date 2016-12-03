package org.awiki.kamikaze.summit.service;

import static org.awiki.kamikaze.summit.domain.Region.REGION_TYPE_REPORT;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.entry.PageDto;
import org.awiki.kamikaze.summit.dto.entry.PageRegionDto;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.service.formatter.ProxyFormatterService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.mapper.PageToPageDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageFilteringServiceImpl implements PageFilteringService
{
  @Autowired
  private ApplicationPageRepository appPageStore;

  @Autowired
  private PageToPageDtoMapper pageMapper;

  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Autowired
  private ProxyFormatterService sourceFormatters;
  
  @Override
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, String searchText)
  {
    final PageDto pageDto = pageMapper.map(appPageStore.findByApplicationIdAndPageId(applicationId, pageId).getPage());
    
    for(PageRegionDto pr : pageDto.getPageRegions()) {
      if (REGION_TYPE_REPORT.equals(pr.getRegionDto().getCodeRegionType())) {
        ReportSourceProcessorService reportService = (ReportSourceProcessorService) sourceProcessors.getSourceProcessorService(pr.getRegionDto().getCodeSourceType());
        final String filteredQuery = buildWrapperQuery(pr.getRegionDto().getSource().iterator().next(), reportService.getColumnList(pr.getRegionDto().getId()), searchText);
        SourceProcessorResultTable resultTable = reportService.querySource(null, filteredQuery, null);
        resultTable.setTemplateDto(pr.getRegionDto().getTemplateDto());
        return resultTable;
      }
    }
    
    throw new RuntimeException("No report region found on " + applicationId + "/" + pageId + ".");
  }

  private String buildWrapperQuery(final String innerQuery, final Collection<String> columnList, final String searchText) {
    StringBuilder fullQuery = new StringBuilder();
    fullQuery.append("SELECT ").append(StringUtils.join(columnList, ", "));
    fullQuery.append(" FROM ( " + innerQuery + " ) SUBQUERY ");
    fullQuery.append(" WHERE ").append(StringUtils.join(columnList, " = '" + searchText + "' OR ")).append(" = '" + searchText +"'");
    return fullQuery.toString();
  }
  
  @Override
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, String columnName,
          String searchText)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
