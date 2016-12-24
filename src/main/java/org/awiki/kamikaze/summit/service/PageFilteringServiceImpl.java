package org.awiki.kamikaze.summit.service;

import static org.awiki.kamikaze.summit.domain.Region.REGION_TYPE_REPORT;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.entry.PageDto;
import org.awiki.kamikaze.summit.dto.entry.PageRegionDto;
import org.awiki.kamikaze.summit.dto.entry.RegionDto;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.repository.RegionRepository;
import org.awiki.kamikaze.summit.service.formatter.ProxyFormatterService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.mapper.PageToPageDtoMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageFilteringServiceImpl implements PageFilteringService
{
  @Autowired
  private ApplicationPageRepository appPageStore;
  
  @Autowired
  private RegionRepository regionStore;

  @Autowired
  private Mapper mapper;
  
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
      return filterQueryOnRegion(pr.getRegionDto(), searchText);
    }
    
    throw new RuntimeException("No report region found on " + applicationId + "/" + pageId + ".");
  }

  private String buildWrapperQuery(final String innerQuery, final Collection<String> columnList, final String searchText) {
    if(searchText != null) {
      StringBuilder fullQuery = new StringBuilder();
      fullQuery.append("SELECT ").append(StringUtils.join(columnList, ", "));
      fullQuery.append(" FROM ( " + innerQuery + " ) SUBQUERY ");
      fullQuery.append(" WHERE ").append(StringUtils.join(columnList, " = '" + searchText + "' OR ")).append(" = '" + searchText +"'");
      return fullQuery.toString();      
    }
    return innerQuery; // if no search is specified, just run the standard query.
  }
  
  private SourceProcessorResultTable filterQueryOnRegion(final RegionDto regionDto, final String searchText) {
    if (REGION_TYPE_REPORT.equals(regionDto.getCodeRegionType())) {
      ReportSourceProcessorService reportService = (ReportSourceProcessorService) sourceProcessors.getSourceProcessorService(regionDto.getCodeSourceType());
      final String filteredQuery = buildWrapperQuery(regionDto.getSource().iterator().next(), reportService.getColumnList(regionDto.getId()), searchText);
      SourceProcessorResultTable resultTable = reportService.querySource(null, filteredQuery, null);
      resultTable.setTemplateDto(regionDto.getTemplateDto());
      return resultTable;
    }
    return null;
  }
  
  @Override
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, String columnName,
          String searchText)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SourceProcessorResultTable filterRegion(long regionId, String searchText)
  {
    RegionDto region = mapper.map(regionStore.findOne(regionId), RegionDto.class);
    return filterQueryOnRegion(region, searchText);
  }

  @Override
  public SourceProcessorResultTable filterRegionByColumn(long regionId, String columnName, String searchText)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
