package org.awiki.kamikaze.summit.service.processor;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTableExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class SQLReportSourceProcessorServiceImpl implements ReportSourceProcessorService
{

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
    SqlParameterSource params = mapBindVars(bindVars);
    
    SourceProcessorResultTable results = new SourceProcessorResultTable();
    results = jdbc.query(source, params, new SourceProcessorResultTableExtractor());
    return results;
  }
  
  protected SqlParameterSource mapBindVars(List<BindVar<Types>> bindVars)
  {
    MapSqlParameterSource params = new MapSqlParameterSource();
    for(BindVar var : bindVars)
    {
      params.addValue(var.getBindParameter(), var.getValue());
    }
    return params;
  }

}
