package org.awiki.kamikaze.summit.service.processor;

import java.sql.Types;
import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;

public interface ReportSourceProcessorService extends SourceProcessorService {
  // TODO change to this source identifier later public static final String BUILT_IN_SQL_DML_TYPE_REPORT = "text/plain; sql; dml; report";
  public static final String BUILT_IN_SQL_DML_TYPE_REPORT = "dml_report";
  
  public SourceProcessorResultTable querySource(String source, List<BindVar<Types>> bindVars);
}
