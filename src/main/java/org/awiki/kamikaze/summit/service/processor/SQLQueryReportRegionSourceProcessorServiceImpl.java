package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTableExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

/**
 * This service handles SQL Source Types that return a table of information.
 * It then returns them in a {@link SourceProcessorResultTable}
 * @author msaun
 *
 */
@Service("SQLQueryReportProcessor")
public class SQLQueryReportRegionSourceProcessorServiceImpl extends ReportSourceProcessorService 
{
  public static final String BUILT_IN_SQL_DML_TYPE_REPORT = "dml_report"; // tabular report via SQL
  
  static final List<String> RESPONSIBILITIES = new ArrayList<String>(1);
  static { RESPONSIBILITIES.add(BUILT_IN_SQL_DML_TYPE_REPORT); }
  
  @Override
  public List<String> getResponsibilities()
  {
    return RESPONSIBILITIES;
  }

  public SourceProcessorResultTable querySource(Long regionId, final String source, List<BindVar> bindVars)
  {
    final SourceProcessorResultTable table = executeQuery(source, bindVars);
    if(regionId != null) {
      table.setId(String.valueOf(regionId));
      updateColumnList(regionId, getColumnNames(table));
    }
    return table;
  }

  public SourceProcessorResultTable executeQuery(final String source, final List<BindVar> bindVars) {
    return jdbc.query(source,  new MapSqlParameterSource(bindVarService.convertBindVarsToSqlParameterMap(bindVars)), new SourceProcessorResultTableExtractor(null));
  }
  

}
