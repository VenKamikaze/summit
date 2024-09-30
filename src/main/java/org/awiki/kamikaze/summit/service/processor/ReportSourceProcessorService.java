package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.mapper.RegionMapper;
import org.awiki.kamikaze.summit.repository.RegionRepository;
import org.awiki.kamikaze.summit.service.BindVarService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Column;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class ReportSourceProcessorService implements TabularQuerySourceProcessorService {
  
  static final Logger logger = LoggerFactory.getLogger(ReportSourceProcessorService.class);
  
  NamedParameterJdbcTemplate jdbc;
  BindVarService bindVarService;
  RegionMapper regionMapper;
  RegionRepository regionRepo;
  
  @Autowired
  public void setJdbc(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Autowired
  public void setBindVarService(BindVarService bindVarService) {
    this.bindVarService = bindVarService;
  }

  @Autowired
  public void setRegionMapper(@Lazy RegionMapper regionMapper) {
    this.regionMapper = regionMapper;
  }

  @Autowired
  public void setRegionRepo(RegionRepository regionRepo) {
    this.regionRepo = regionRepo;
  }

  /**
   * Cachable list of columns associated with a particular region.
   * @param regionId
   * @return
   */
  @Cacheable(value="reportColumnList")
  public Collection<String> getColumnList(long regionId, final List<BindVar> bindVars) {
    RegionDto regionDto = regionMapper.map(regionRepo.findById(regionId).get(), new CycleAvoidingMappingContext());
    final SourceProcessorResultTable table = executeQuery(regionDto.getSource().iterator().next(), bindVars);
    table.setId(String.valueOf(regionId));
    return getColumnNames(table);
  }
  
  Collection<String> getColumnNames(final SourceProcessorResultTable table) {
    ArrayList<String> columns = new ArrayList<String>(table.getColumns().size());
    for (Column c : table.getColumns()) {
      columns.add(c.getName());
    }
    return columns;
  }
  
  /**
   * Evicts the cache of column names associated with a particular region.
   * @param regionId
   */
  @CacheEvict(value="reportColumnList", allEntries=true)
  public void clearColumnList(long regionId) {
    logger.info(getClass().getCanonicalName() + ": " + "clearing cache for reportColumnList");
  }
  
  @CachePut(value="reportColumnList", key="regionId")
  Collection<String> updateColumnList(long regionId, Collection<String> columns) 
  {
    logger.info(getClass().getCanonicalName() + ": forcing population of cache \"reportColumnList\" for regionId=" + regionId);
    return columns;
  }
  
  public abstract SourceProcessorResultTable querySource(Long regionId, final String source, List<BindVar> bindVars);
  
  /**
   * Cachable total record count associated with a particular query.
   * @param fullQuery
   * @param bindVars - the bind variables if applicable to the query.
   * @return Long
   */
  @Cacheable(value="totalRecordCount")
  public Long getTotalRecordCount(final String fullQuery, final List<BindVar> bindVars) {
    final SourceProcessorResultTable count = executeQuery(fullQuery,  bindVars);
    return count.getBody().size() > 0 ? Long.parseLong(count.getBody().get(0).getCell(0).getValue()) : 0L;
  }
}
