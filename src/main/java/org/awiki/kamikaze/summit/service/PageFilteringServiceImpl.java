package org.awiki.kamikaze.summit.service;

import static org.awiki.kamikaze.summit.domain.Region.REGION_TYPE_REPORT;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.entry.PageDto;
import org.awiki.kamikaze.summit.dto.entry.PageRegionDto;
import org.awiki.kamikaze.summit.dto.entry.RegionDto;
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
  private DriverManagerDataSource dataSource;
  
  private DatabaseTypeEnum detectedDbType = null;
  
  private static final Logger logger = LoggerFactory.getLogger(PageFilteringServiceImpl.class);
  
  @Override
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, String searchText)
  {
    final PageDto pageDto = pageMapper.map(appPageStore.findByApplicationIdAndPageId(applicationId, pageId).getPage());
    
    for(PageRegionDto pr : pageDto.getPageRegions()) {
      return filterQueryOnRegion(pr.getRegionDto(), searchText, 0, 0, false); // FIXME, TODO Pagination
    }
    
    throw new RuntimeException("No report region found on " + applicationId + "/" + pageId + ".");
  }

  private String buildWrapperCountQuery(final String innerQuery, final Collection<String> columnList, final String searchText) {
    StringBuilder countQuery = new StringBuilder();
    countQuery.append("SELECT COUNT(1) as TOTALCOUNT");
    countQuery.append(" FROM ( " + innerQuery + " ) SUBQUERY ");
    if(StringUtils.isNotEmpty(searchText)) {
      countQuery.append(" WHERE ").append(StringUtils.join( wrapInCast(columnList, "varchar"), " = :searchText OR ")).append(" = :searchText ");  
    }
    return countQuery.toString(); // if no search is specified, just run the standard query.
  }
  
  private String buildWrapperQuery(final String innerQuery, final Collection<String> columnList, final String searchText,
          long page, long rowsPerPage) {
    if(StringUtils.isNotEmpty(searchText)) {
      StringBuilder fullQuery = new StringBuilder();
      fullQuery.append("SELECT ").append(StringUtils.join(columnList, ", "));
      fullQuery.append(" FROM ( " + innerQuery + " ) SUBQUERY ");
      fullQuery.append(" WHERE ").append(StringUtils.join( wrapInCast(columnList, "varchar"), " = :searchText OR ")).append(" = :searchText ");
      if(page > 0)
        addPaginationToQuery(fullQuery, StringUtils.join(columnList, ", "), true, page, rowsPerPage);
      return fullQuery.toString();      
    }
    if(page > 0)
      return addPaginationToQuery(new StringBuilder(innerQuery), StringUtils.join(columnList, ", "), false, page, rowsPerPage).toString();
    
    return innerQuery; // if no search is specified, and we are not using pagination, just run the standard query.
  }
  
  private Collection<String> wrapInCast(final Collection<String> columnList, final String castType) {
    Collection<String> castedColumns = new ArrayList<String>(columnList.size());
    for(String col : columnList) {
      castedColumns.add("CAST (" + col + " as " + castType + ")");
    }
    return castedColumns;
  }
  
  private StringBuilder addPaginationToQuery(StringBuilder currentQuery, final String columnList, boolean hasWhereClause, long page, long rowsPerPage) {
    if(detectedDbType == null)
      detectDbType();
    
    switch(detectedDbType) {
      case ORACLE:
        // append rownum as column to the current query
        int firstSelect = currentQuery.indexOf("SELECT ");
        currentQuery.insert(firstSelect, "rownum RNUM, ");
        if(! hasWhereClause) {
          currentQuery.append(" WHERE ");
        }
        else {
          currentQuery.append(" AND ");
        }
        // limit max rows to the last row of the current page.
        currentQuery.append(" rownum <= ").append(page * rowsPerPage);
        // Sub-Query the current query again, so we can specify rows to start from.
        currentQuery.insert(0, "SELECT RNUM, ").append(columnList).append(" FROM ( ");
        currentQuery.append(" ) WHERE RNUM >= ").append(page <= 1 ? 1 : (page-1 * rowsPerPage));
        break;
        
      case POSTGRES:
      case MYSQL:
        currentQuery.append(" LIMIT ").append(rowsPerPage);
        if(page > 1) {
          currentQuery.append(" OFFSET ").append((page-1) * rowsPerPage);
        }
        break;
        
      case SQLSERVER:
      {
        String error = "Not yet supported. FIXME, TODO SQLServer: " + detectedDbType;
        logger.error(error);
        throw new NotYetImplementedException(error);
      }
      
      default:
      {
        String error = "Unknown detected DB type enum value: " + detectedDbType;
        logger.error(error);
        throw new RuntimeException(error);
      }
    }
    return currentQuery;
  }
  
  private void detectDbType() {
    final String dbConnectionUrl = dataSource.getUrl() != null ? dataSource.getUrl().toLowerCase() : null;
    if(dbConnectionUrl != null) {
      if(dbConnectionUrl.contains("oracle")) {
        detectedDbType = DatabaseTypeEnum.ORACLE;
      }
      else if (dbConnectionUrl.contains("postgres")) {
        detectedDbType = DatabaseTypeEnum.POSTGRES;
      }
      else if (dbConnectionUrl.contains("mysql")) {
        detectedDbType = DatabaseTypeEnum.MYSQL;
      }
      else if (dbConnectionUrl.contains("sqlserver")) {
        detectedDbType = DatabaseTypeEnum.SQLSERVER;
      }
      else {
        logger.warn("Unknown database type detected! This is determined from the database connection URL, which is detected as: " + dbConnectionUrl);
        logger.warn("Falling back to postgres style pagination. Pagination may not function correctly.");
        detectedDbType = DatabaseTypeEnum.POSTGRES;
      }
      logger.info("Detected DB type enum is: " + detectedDbType);
    }
  }
  
  /**
   * 
   * @param regionDto
   * @param searchText
   * @param page
   * @param rowsPerPage
   * @param getTotalCount - only retrieve the total number of records if asked, as this adds another expensive query.
   * @return
   */
  private SourceProcessorResultTable filterQueryOnRegion(final RegionDto regionDto, final String searchText, long page, long rowsPerPage, boolean getTotalCount) {
    if (REGION_TYPE_REPORT.equals(regionDto.getCodeRegionType())) {
      ReportSourceProcessorService reportService = (ReportSourceProcessorService) sourceProcessors.getSourceProcessorService(regionDto.getCodeSourceType());
      final Collection<String> columnList = reportService.getColumnList(regionDto.getId());
      final String filteredQuery = buildWrapperQuery(regionDto.getSource().iterator().next(), columnList, searchText,page, rowsPerPage);
      SourceProcessorResultTable resultTable = reportService.querySource(null, filteredQuery, buildParametersForFullQuery(columnList.size(), searchText));
      
      if(getTotalCount) {
        final String countQuery = buildWrapperCountQuery(regionDto.getSource().iterator().next(), columnList, searchText);
        resultTable.setTotalCount(reportService.getTotalRecordCount(countQuery));
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
    return filterRegion(regionId, searchText, 0L, 0L, false);
  }

  @Override
  public SourceProcessorResultTable filterRegion(long regionId, String searchText, long page, long rowsPerPage, boolean getTotalCount)
  {
    RegionDto region = mapper.map(regionStore.findOne(regionId), RegionDto.class);
    return filterQueryOnRegion(region, searchText, page, rowsPerPage, getTotalCount);
  }
  
  @Override
  public SourceProcessorResultTable filterRegionByColumn(long regionId, String columnName, String searchText)
  {
    return filterRegionByColumn(regionId, columnName, searchText, 0, 0, false); // FIXME, TODO pagination plus impl
  }

  @Override
  public SourceProcessorResultTable filterRegionByColumn(long regionId, String columnName, String searchText, long page, long rowsPerPage, boolean getTotalCount)
  {
    // TODO Auto-generated method stub
    return null;
  }
}
