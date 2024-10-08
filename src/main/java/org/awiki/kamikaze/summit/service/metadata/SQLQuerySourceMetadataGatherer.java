package org.awiki.kamikaze.summit.service.metadata;

import java.util.Collection;
import java.util.List;

import org.awiki.kamikaze.summit.domain.SourceMetadata;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.dto.render.SourceMetadataDto;
import org.awiki.kamikaze.summit.mapper.SourceMetadataMapper;
import org.awiki.kamikaze.summit.mapper.SourceProcessorResultTableColumnMapper;
import org.awiki.kamikaze.summit.repository.SourceMetadataRepository;
import org.awiki.kamikaze.summit.service.BindVarService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Column;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTableExtractor;
import org.awiki.kamikaze.summit.util.SQLUtils;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service("SQLQueryMetadata")
public class SQLQuerySourceMetadataGatherer implements QuerySourceMetadataGatherer
{
  private static final Logger logger = LoggerFactory.getLogger(SQLQuerySourceMetadataGatherer.class);
  
  private BindVarService bindVarService;
  //private final ProxySourceProcessorService sourceProcessors;
  private NamedParameterJdbcTemplate jdbc;
  private SourceProcessorResultTableColumnMapper columnMapper;
  private SourceMetadataMapper sourceMetadataMapper;
  private SourceMetadataRepository repository;
  
  @Autowired
  public void setBindVarService(BindVarService bindVarService) {
    this.bindVarService = bindVarService;
  }

  @Autowired
  public void setJdbc(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Autowired
  public void setColumnMapper(@Lazy SourceProcessorResultTableColumnMapper columnMapper) {
    this.columnMapper = columnMapper;
  }

  @Autowired
  public void setSourceMetadataMapper(@Lazy SourceMetadataMapper sourceMetadataMapper) {
    this.sourceMetadataMapper = sourceMetadataMapper;
  }

  @Autowired
  public void setRepository(@Lazy SourceMetadataRepository repository) {
    this.repository = repository;
  }

  
  /*
  private Collection<String> getColumnNames(final SourceProcessorResultTable table)
  {
    ArrayList<String> columns = new ArrayList<String>(table.getColumns().size());
    for (Column c : table.getColumns()) {
      columns.add(c.getName());
    }
    return columns;
  }
  
  @Override
  @Cacheable(value="reportColumnList")
  public Collection<String> getColumnList(long regionId, final List<BindVar> bindVars)
  {
    RegionDto regionDto = regionMapper.map(regionRepo.findById(regionId), RegionDto.class);
    final SourceProcessorResultTable table = getResults(regionDto.getSource().iterator().next(), bindVarService.convertBindVarsToSqlParameterMap(bindVars), String.valueOf(regionId));
    return getColumnNames(table);
  }
*/


  @Override
  @CachePut(value="sourceMetadata", key="sourceDto.id")
  public SourceMetadataDto updateSourceMetadata(SourceDto sourceDto, List<BindVar> bindVars)
  {
    logger.debug(SQLQuerySourceMetadataGatherer.class.getCanonicalName() + ": " + "building stripped query to determine metadata");
    logger.debug(SQLQuerySourceMetadataGatherer.class.getCanonicalName() + ": " + sourceDto.getSource());
    final String strippedSql = SQLUtils.stripCriteriaClause(sourceDto.getSource());
    StringBuilder modifiedQuery = SQLUtils.buildWrapperQuery(strippedSql);
    modifiedQuery = SQLUtils.addPaginationToQuery(modifiedQuery, "*", 1, null);
    logger.debug(SQLQuerySourceMetadataGatherer.class.getCanonicalName() + " (stripped query): " + modifiedQuery.toString());

    SourceProcessorResultTable res = jdbc.query(strippedSql,  new MapSqlParameterSource(bindVarService.convertBindVarsToSqlParameterMap(bindVars)), new SourceProcessorResultTableExtractor(null));
    
    SourceMetadata metadata = new SourceMetadata();
    for(Column c : res.getColumns()) {
      metadata.getSourceMetadataCols().add(columnMapper.map(c, new CycleAvoidingMappingContext()));
    }
    return sourceMetadataMapper.map(repository.save(metadata), new CycleAvoidingMappingContext());
  }

  /*
  @Override
  @Cacheable(value="sourceMetadata", key="sourceDto.id")
  public SourceMetadataDto getSourceMetadata(SourceDto sourceDto, List<BindVar> bindVars)
  {
    logger.debug(SQLQuerySourceMetadataGatherer.class.getCanonicalName() + ": " + "getting sourceMetadata for source ID: " + sourceDto.getId());
    repository.findById(sourceDto.getSourceMetadata().getId())
    return mapper.map(repository.save(metadata), SourceMetadataDto.class);
  }
  */
  
  @Override
  @CacheEvict(value="sourceMetadata", allEntries=true)
  public void clearSourceMetadata()
  {
    logger.info(SQLQuerySourceMetadataGatherer.class.getCanonicalName() + ": " + "clearing cache for sourceMetadata");
  }

  @Override
  @CachePut(value="reportColumnList", key="sourceid")
  public Collection<String> updateColumnList(long sourceId, Collection<String> columns) {
    logger.info(SQLQuerySourceMetadataGatherer.class.getCanonicalName() + ": " + "forcing population of cache \"reportColumnList\" for sourceId=" + sourceId);
    return columns;
  }

  @Override
  @CacheEvict(value="reportColumnList", allEntries=true)
  public void clearColumnList(long sourceId) {
    logger.info(SQLQuerySourceMetadataGatherer.class.getCanonicalName() + ": " + "clearing cache for reportColumnList");
  }

}
