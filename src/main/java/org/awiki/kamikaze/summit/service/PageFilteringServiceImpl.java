package org.awiki.kamikaze.summit.service;

import static org.awiki.kamikaze.summit.domain.Region.REGION_TYPE_REPORT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
  private FieldService fieldService;

  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Autowired
  private ProxyFormatterService sourceFormatters;
  
  @Autowired
  @Qualifier(value="reportSQLQueryBuilder")
  private QueryBuilderService reportSQLQueryBuilder;
  
  @Autowired
  private BindVarService bindVarService;
  
  private static final Logger logger = LoggerFactory.getLogger(PageFilteringServiceImpl.class);
  
  @Override
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, final String filterType, final String searchText, long page, long rowsPerPage, boolean getTotalCount, final Map<String, String> parameterMap)
  {
    final PageDto pageDto = pageMapper.map(appPageStore.findByApplicationIdAndPageId(applicationId, pageId).getPage());
    
    for(PageRegionDto pr : pageDto.getPageRegions()) {
      return filterQueryOnRegion(pr.getRegionDto(), filterType != null ? filterType.toUpperCase() : null, null, searchText, page, rowsPerPage, getTotalCount, parameterMap);
    }
    
    throw new RuntimeException("No report region found on " + applicationId + "/" + pageId + ".");
  }

  @Override
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, final String filterType, final String columnName,
          final String searchText, long page, long rowsPerPage, boolean getTotalCount, final Map<String, String> parameterMap)
  {
    final PageDto pageDto = pageMapper.map(appPageStore.findByApplicationIdAndPageId(applicationId, pageId).getPage());
    
    for(PageRegionDto pr : pageDto.getPageRegions()) {
      return filterQueryOnRegion(pr.getRegionDto(), filterType != null ? filterType.toUpperCase() : null, columnName, searchText, page, rowsPerPage, getTotalCount, parameterMap);
    }
    
    throw new RuntimeException("No report region found on " + applicationId + "/" + pageId + ".");
  }

  @Override
  public SourceProcessorResultTable filterRegion(long regionId, final String filterType, final String searchText, long page, long rowsPerPage, boolean getTotalCount, final Map<String, String> parameterMap)
  {
    RegionDto region = mapper.map(regionStore.findOne(regionId), RegionDto.class);
    return filterQueryOnRegion(region, filterType != null ? filterType.toUpperCase() : null, null, searchText, page, rowsPerPage, getTotalCount, parameterMap);
  }
  
  @Override
  public SourceProcessorResultTable filterRegionByColumn(long regionId, final String filterType, final String columnName, final String searchText, long page, long rowsPerPage, boolean getTotalCount, final Map<String, String> parameterMap)
  {
    RegionDto region = mapper.map(regionStore.findOne(regionId), RegionDto.class);
    return filterQueryOnRegion(region, filterType != null ? filterType.toUpperCase() : null, columnName, searchText, page, rowsPerPage, getTotalCount, parameterMap);
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
  private SourceProcessorResultTable filterQueryOnRegion(final RegionDto regionDto, final String filterType, final String columnName, final String searchText, long page, long rowsPerPage, boolean getTotalCount, final Map<String, String> parameterMap) {
    if (REGION_TYPE_REPORT.equals(regionDto.getCodeRegionType())) {

      // TODO passing empty map here -- decide if we want to process associated fields first, or expect that we pass them in as form data?
      final Set<PageItem<String>> fields = fieldService.processFieldsForRender(regionDto.getRegionFields(), MapUtils.EMPTY_MAP, parameterMap);
      // TODO FIXME region has a set of source but we always treat it as one. Decide what we are going to do going forward.
      List<BindVar> fieldBindings = bindVarService.createBindVarsFromFields(regionDto.getSource().iterator().next(), fieldService.fieldsToMap(fields));
      
      regionDto.setSubPageItems(fields);
      
      ReportSourceProcessorService reportService = sourceProcessors.getReportSourceProcessorService(regionDto.getCodeSourceType());
      final Collection<String> columnList = reportService.getColumnList(regionDto.getId(), fieldBindings);
      // TODO FIXME region has a set of source but we always treat it as one. Decide what we are going to do going forward.
      final String filteredQuery = reportSQLQueryBuilder.buildWrapperQuery(regionDto.getSource().iterator().next(), filterType, columnName, columnList, searchText,page, rowsPerPage);
      fieldBindings.addAll(buildParametersForWrapperQuery(columnList.size(), searchText));
      
      SourceProcessorResultTable resultTable = reportService.querySource(null, filteredQuery, fieldBindings);
      
      if(getTotalCount) {
        final String countQuery = reportSQLQueryBuilder.buildWrapperCountQuery(regionDto.getSource().iterator().next(), filterType, columnName, columnList, searchText);
        resultTable.setTotalCount(reportService.getTotalRecordCount(countQuery, fieldBindings));
      }
      
      resultTable.setTemplateDto(regionDto.getTemplateDto());
      return resultTable;
    }
    return null;
  }
  
  
  @SuppressWarnings("serial")
  private List<BindVar> buildParametersForWrapperQuery(final int numColumns, final String searchText) {
    if(StringUtils.isNotEmpty(searchText)) {
      return new ArrayList<BindVar>(numColumns) {{ 
        for(int i = 0; i < numColumns; i++) {
          add(new BindVar(searchText, java.sql.Types.VARCHAR, "searchText")); 
        }
       }};
    }
    return new ArrayList<BindVar>(0);
  }
  
}
