package org.awiki.kamikaze.summit.service;

import static org.awiki.kamikaze.summit.domain.Region.REGION_TYPE_REPORT;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.repository.RegionRepository;
import org.awiki.kamikaze.summit.service.formatter.ProxyFormatterService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.mapper.PageToPageDtoMapper;
import org.dozer.Mapper;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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
  
  @Autowired
  @Qualifier(value="sqlQueryBuilder")
  private QueryBuilderService sqlQueryBuilder;
  
  private static final Logger logger = LoggerFactory.getLogger(PageFilteringServiceImpl.class);
  
  @Override
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, final String filterType, final String searchText, long page, long rowsPerPage, boolean getTotalCount)
  {
    final PageDto pageDto = pageMapper.map(appPageStore.findByApplicationIdAndPageId(applicationId, pageId).getPage());
    
    for(PageRegionDto pr : pageDto.getPageRegions()) {
      return filterQueryOnRegion(pr.getRegionDto(), filterType, null, searchText, page, rowsPerPage, getTotalCount);
    }
    
    throw new RuntimeException("No report region found on " + applicationId + "/" + pageId + ".");
  }

  @Override
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, final String filterType, final String columnName,
          final String searchText, long page, long rowsPerPage, boolean getTotalCount)
  {
    final PageDto pageDto = pageMapper.map(appPageStore.findByApplicationIdAndPageId(applicationId, pageId).getPage());
    
    for(PageRegionDto pr : pageDto.getPageRegions()) {
      return filterQueryOnRegion(pr.getRegionDto(), filterType, columnName, searchText, page, rowsPerPage, getTotalCount);
    }
    
    throw new RuntimeException("No report region found on " + applicationId + "/" + pageId + ".");
  }

  @Override
  public SourceProcessorResultTable filterRegion(long regionId, final String filterType, final String searchText, long page, long rowsPerPage, boolean getTotalCount)
  {
    RegionDto region = mapper.map(regionStore.findOne(regionId), RegionDto.class);
    return filterQueryOnRegion(region, filterType, null, searchText, page, rowsPerPage, getTotalCount);
  }
  
  @Override
  public SourceProcessorResultTable filterRegionByColumn(long regionId, final String filterType, final String columnName, final String searchText, long page, long rowsPerPage, boolean getTotalCount)
  {
    RegionDto region = mapper.map(regionStore.findOne(regionId), RegionDto.class);
    return filterQueryOnRegion(region, filterType, columnName, searchText, page, rowsPerPage, getTotalCount);
  }
  
  /**
   * Query all columns in a report with the specified searchText.
   * @param regionDto
   * @param searchText - the value to search all columns (using an OR for each column)
   * @param page - which page we are on
   * @param rowsPerPage - number of rows to show per page
   * @param getTotalCount - only retrieve the total number of records if asked, as this can add another expensive query.
   * @return {@link SourceProcessorResultTable}
   */
  private SourceProcessorResultTable filterQueryOnRegion(final RegionDto regionDto, final String filterType, final String columnName, final String searchText, long page, long rowsPerPage, boolean getTotalCount) {
    if (REGION_TYPE_REPORT.equals(regionDto.getCodeRegionType())) {
      ReportSourceProcessorService reportService = (ReportSourceProcessorService) sourceProcessors.getSourceProcessorService(regionDto.getCodeSourceType());
      final Collection<String> columnList = reportService.getColumnList(regionDto.getId());
      final String filteredQuery = sqlQueryBuilder.buildWrapperQuery(regionDto.getSource().iterator().next(), filterType.toUpperCase(), columnName, columnList, searchText,page, rowsPerPage);
      SourceProcessorResultTable resultTable = reportService.querySource(null, filteredQuery, buildParametersForFullQuery(columnList.size(), searchText));
      
      if(getTotalCount) {
        final String countQuery = sqlQueryBuilder.buildWrapperCountQuery(regionDto.getSource().iterator().next(), filterType.toUpperCase(), columnName, columnList, searchText);
        resultTable.setTotalCount(reportService.getTotalRecordCount(countQuery, buildParametersForFullQuery(columnList.size(), searchText)));
      }
      
      resultTable.setTemplateDto(regionDto.getTemplateDto());
      return resultTable;
    }
    return null;
  }
    
  
  @SuppressWarnings("serial")
  private List<BindVar> buildParametersForFullQuery(final int numColumns, final String searchText) {
    if(StringUtils.isNotEmpty(searchText)) {
      return new ArrayList<BindVar>(numColumns) {{ 
        for(int i = 0; i < numColumns; i++) {
          add(new BindVar(searchText, java.sql.Types.VARCHAR, "searchText")); 
        }
       }};
    }
    return null;
  }
  
}
