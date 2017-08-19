package org.awiki.kamikaze.summit.service.metadata;

import java.util.Collection;
import java.util.List;

import org.awiki.kamikaze.summit.domain.SourceMetadata;
import org.awiki.kamikaze.summit.service.BindVarService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service("SQLQueryMetadata")
public class SQLQuerySourceMetadataGatherer implements QuerySourceMetadataGatherer
{
  private static final Logger logger = LoggerFactory.getLogger(SQLQuerySourceMetadataGatherer.class);
  
  @Autowired
  private BindVarService bindVarService;

  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
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
  @CachePut(value="sourceMetadata", key="sourceId")
  public SourceMetadata getSourceMetadata(long sourceId, List<BindVar> bindVars)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  @CacheEvict(value="sourceMetadata", allEntries=true)
  public void clearSourceMetadata(long sourceId)
  {
    // TODO Auto-generated method stub

  }

  @Override
  @CachePut(value="reportColumnList", key="sourceId")
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
