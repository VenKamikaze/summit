package org.awiki.kamikaze.summit.service.metadata;

import java.util.Collection;
import java.util.List;

import org.awiki.kamikaze.summit.domain.SourceMetadata;
import org.awiki.kamikaze.summit.domain.SourceMetadataCols;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.dto.render.SourceMetadataDto;
import org.awiki.kamikaze.summit.repository.SourceMetadataRepository;
import org.awiki.kamikaze.summit.service.BindVarService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Column;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTableExtractor;
import org.awiki.kamikaze.summit.util.SQLUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service("SQLQueryMetadata")
public class SQLQuerySourceMetadataGatherer implements QuerySourceMetadataGatherer
{
  private static final Logger logger = LoggerFactory.getLogger(SQLQuerySourceMetadataGatherer.class);
  
  @Autowired
  private BindVarService bindVarService;

  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Autowired
  private NamedParameterJdbcTemplate jdbc;
  
  @Autowired
  private Mapper mapper;
  
  @Autowired
  private SourceMetadataRepository repository;
  
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
    RegionDto regionDto = regionMapper.map(regionRepo.findOne(regionId), RegionDto.class);
    final SourceProcessorResultTable table = getResults(regionDto.getSource().iterator().next(), bindVarService.convertBindVarsToSqlParameterMap(bindVars), String.valueOf(regionId));
    return getColumnNames(table);
  }
*/

  @Override
  @CachePut(value="sourceMetadata", key="sourceDto.id")
  public SourceMetadataDto getSourceMetadata(SourceDto sourceDto, List<BindVar> bindVars)
  {
    final String strippedSql = SQLUtils.stripCriteriaClause(sourceDto.getSource());
    StringBuilder modifiedQuery = SQLUtils.buildWrapperQuery(strippedSql);
    SQLUtils.addPaginationToQuery(modifiedQuery, "*", 1, null);
    SourceProcessorResultTable res = jdbc.query(strippedSql,  new MapSqlParameterSource(bindVarService.convertBindVarsToSqlParameterMap(bindVars)), new SourceProcessorResultTableExtractor(null));
    
    SourceMetadata metadata = new SourceMetadata();
    for(Column c : res.getColumns()) {
      metadata.getSourceMetadataCols().add(mapper.map(c, SourceMetadataCols.class));
    }
    return mapper.map(repository.save(metadata), SourceMetadataDto.class);
  }

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
