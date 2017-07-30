package org.awiki.kamikaze.summit.service.processor;

import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;

public interface SingularSourceProcessorService extends SourceProcessorService {
  public static final String BUILT_IN_SQL_DML_TYPE = "text/plain; sql; dml";
  public static final String BUILT_IN_SQL_DML_BATCH_TYPE = "text/plain; sql; dml; batch"; // wrong service??
  public static final String BUILT_IN_SQL_DDL_TYPE = "text/plain; sql; ddl";
  public static final String BUILT_IN_SQL_STORED_PROC = "text/plain; sql; code";
  public static final String BUILT_IN_STATIC_TEXT_TYPE = "static";
  
  //public static final String BUILT_IN_PYTHON2_TYPE = "text/plain; python2";
  
  public SourceProcessorResult executeSource(final String source, List<BindVar> bindVars); // executes source, could be data manipulation - may not return anything
  public SourceProcessorResult querySource(final String sql, List<BindVar> bindVars);   // runs source as a query - should return one result.
}
