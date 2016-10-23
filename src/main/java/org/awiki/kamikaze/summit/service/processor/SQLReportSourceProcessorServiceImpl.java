package org.awiki.kamikaze.summit.service.processor;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTableExtractor;
import org.awiki.kamikaze.summit.util.mapper.PageToPageDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
  private NamedParameterJdbcTemplate jdbc;

  @SuppressWarnings("serial")
  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(1) {{ add(ReportSourceProcessorService.BUILT_IN_SQL_DML_TYPE_REPORT); }};
  }

  @Override
  public SourceProcessorResultTable querySource(String source, List<BindVar<Types>> bindVars)
  {
    return getResults(source, getParams(bindVars));
  }
  
  private SourceProcessorResultTable getResults(final String source, List params)
  {
    PreparedStatementCreatorFactory  factory = new PreparedStatementCreatorFactory(source);
    factory.setResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
    factory.setUpdatableResults(false);
    PreparedStatementCreator psc = factory.newPreparedStatementCreator(params);
    logger.info(SQLReportSourceProcessorServiceImpl.class.getCanonicalName() + ": " + "about to process source: " + source );
    logger.info(SQLReportSourceProcessorServiceImpl.class.getCanonicalName() + ": " + "params=" + StringUtils.collectionToCommaDelimitedString(params) );
    return jdbc.getJdbcOperations().query(psc, new SourceProcessorResultTableExtractor());
  }
  
  private List getParams(List<BindVar<Types>> bindVars)
  {
    ArrayList paramList = new ArrayList(bindVars != null ? bindVars.size() : 1);
    if (bindVars != null)
    {
      for (BindVar<Types> var : bindVars)
      {
        paramList.add( var.getValue());
      }
    }
    return paramList;
  }
  
  @Deprecated
  protected SqlParameterSource mapBindVars(List<BindVar<Types>> bindVars)
  {
    MapSqlParameterSource params = new MapSqlParameterSource();
    if (bindVars != null)
    {
      for (BindVar var : bindVars)
      {
        params.addValue(var.getBindParameter(), var.getValue());
      }
    }
    return params;
  }

}
