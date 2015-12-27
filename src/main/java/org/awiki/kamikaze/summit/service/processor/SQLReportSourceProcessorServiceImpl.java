package org.awiki.kamikaze.summit.service.processor;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTableExtractor;
import org.springframework.beans.factory.annotation.Autowired;

public class SQLReportSourceProcessorServiceImpl implements ReportSourceProcessorService
{

  @Autowired
  private JdbcTemplate jdbc;
  
  @Override
  public boolean isResponsibleFor(String sourceType)
  {
    return (ReportSourceProcessorService.BUILT_IN_SQL_DML_TYPE_REPORT.equals(sourceType));
  }

  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(1) {{ add(ReportSourceProcessorService.BUILT_IN_SQL_DML_TYPE_REPORT); }};
  }

  @Override
  public SourceProcessorResultTable querySource(String source, List<BindVar<Types>> bindVars)
  {
    Map<String, Object> mapBindVars = new HashMap<>(bindVars.size());
    for(BindVar var : bindVars)
    {
      mapBindVars.put(var.getBindParameter(), var.getValue());
    }
    
    SourceProcessorResultTable results = new SourceProcessorResultTable();
    results = jdbc.query(source, mapBindVars, new SourceProcessorResultTableExtractor());
    return results;
  }

}
