package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.awiki.kamikaze.summit.dto.entry.RegionDto;
import org.awiki.kamikaze.summit.repository.RegionRepository;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Column;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTableExtractor;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * This service handles SQL Source Types that return a table of information.
 * It then returns them in a {@link SourceProcessorResultTable}
 * @author msaun
 *
 */
@Service
public class SQLReportSourceProcessorServiceImpl implements ReportSourceProcessorService
{
  private static final Logger logger = LoggerFactory.getLogger(SQLReportSourceProcessorServiceImpl.class);
  
  @Autowired
  private RegionRepository regionRepo;
  
  @Autowired
  private Mapper regionMapper;
  
  @Autowired
  private NamedParameterJdbcTemplate jdbc;

  @SuppressWarnings("serial")
  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(1) {{ add(ReportSourceProcessorService.BUILT_IN_SQL_DML_TYPE_REPORT); }};
  }

  @Override
  public SourceProcessorResultTable querySource(Long regionId, final String source, List<BindVar> bindVars)
  {
    final SourceProcessorResultTable table = getResults(source, getParamsForSpring(bindVars), regionId != null ? regionId.toString() : null);
    if(regionId != null) {
      updateColumnList(regionId, getColumnNames(table));
    }
    return table;
  }

  private SourceProcessorResultTable getResults(final String source, Map<String, SqlParameter> params, final String regionId) {
    logger.info(SQLReportSourceProcessorServiceImpl.class.getCanonicalName() + ": " + "about to process source: " + source );
   //logger.info(SQLReportSourceProcessorServiceImpl.class.getCanonicalName() + ": " + "param names=" + StringUtils.collectionToCommaDelimitedString(params.keySet()) );
    return jdbc.query(source,  new MapSqlParameterSource(params), new SourceProcessorResultTableExtractor(regionId));
  }
  
  /**
   * TODO: We should probably just use Spring's SqlParameterValue all the way through considering we are mostly
   * tied to spring anyway
   * @param bindVars
   * @return
   */
  @SuppressWarnings("unchecked") // for empty map
  private Map<String, SqlParameter> getParamsForSpring(List<BindVar> bindVars)
  {
    if(bindVars == null)
      return MapUtils.EMPTY_MAP;
    Map<String, SqlParameter> paramMap = new HashMap<>(bindVars.size()); // possibly this size or smaller.
    for (BindVar var : bindVars) {
      paramMap.put(var.getBindParameter(), new SqlParameterValue(new SqlParameter(var.getBindParameter(), var.getType()), var.getValue()) );
    }
    return paramMap;
  }
  
  private Collection<String> getColumnNames(final SourceProcessorResultTable table)
  {
    ArrayList<String> columns = new ArrayList<String>(table.getColumns().size());
    for (Column c : table.getColumns()) {
      columns.add(c.getName());
    }
    return columns;
  }
  
  @Override
  @Cacheable(value="totalRecordCount")
  public Long getTotalRecordCount(final String fullQuery) 
  {
    final SourceProcessorResultTable count = getResults(fullQuery, null , null);
    return Long.parseLong(count.getBody().get(0).getCell(0).getValue());
  }
  
  @Override
  @Cacheable(value="reportColumnList")
  public Collection<String> getColumnList(long regionId)
  {
    RegionDto regionDto = regionMapper.map(regionRepo.findOne(regionId), RegionDto.class);
    final SourceProcessorResultTable table = getResults(regionDto.getSource().iterator().next(), null, String.valueOf(regionId));
    return getColumnNames(table);
  }

  @CachePut(value="reportColumnList", key="regionId")
  private Collection<String> updateColumnList(long regionId, Collection<String> columns) 
  {
    logger.info(SQLReportSourceProcessorServiceImpl.class.getCanonicalName() + ": " + "forcing population of cache \"reportColumnList\" for regionId=" + regionId);
    return columns;
  }
  
  @Override
  @CacheEvict(value="reportColumnList", allEntries=true)
  public void clearColumnList(long regionId)
  {
    logger.info(SQLReportSourceProcessorServiceImpl.class.getCanonicalName() + ": " + "clearing cache for reportColumnList");
  }

}
