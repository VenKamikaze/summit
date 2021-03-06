package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;

public interface SingularSourceProcessorService extends SourceProcessorService {
  public static final String BUILT_IN_SQL_DML_SELECT_CELL_TYPE = "dml_selcel"; // query for a select statement, expects single row with single cell or no rows return.
  public static final String BUILT_IN_SQL_DML_MODIFY_TYPE = "dml_modify"; // execute an insert/update/delete statement.
  public static final String BUILT_IN_SQL_DDL_TYPE = "ddl_exec"; // execute a DDL statement 
  //public static final String BUILT_IN_SQL_DML_TYPE = "text/plain; sql; dml";
  //public static final String BUILT_IN_SQL_DML_BATCH_TYPE = "text/plain; sql; dml; batch"; // wrong service??
  //public static final String BUILT_IN_SQL_DDL_TYPE = "text/plain; sql; ddl";
  //public static final String BUILT_IN_SQL_STORED_PROC = "text/plain; sql; code";
  public static final String BUILT_IN_STATIC_TEXT_TYPE = "static";
  
  //public static final String BUILT_IN_PYTHON2_TYPE = "text/plain; python2";
  
  @SuppressWarnings("serial")
  public static final List<String> SINGULAR_SOURCE_TYPES = new ArrayList<String>(4) {{  
    add(BUILT_IN_SQL_DML_SELECT_CELL_TYPE);
    add(BUILT_IN_SQL_DML_MODIFY_TYPE);
    add(BUILT_IN_SQL_DDL_TYPE);
    add(BUILT_IN_STATIC_TEXT_TYPE);
  }};
  
  public SourceProcessorResult processSource(final String source, final String sourceType, List<BindVar> bindVars); // generic method - will call execute or query depending on what the sourceType is.
  public SourceProcessorResult executeSource(final String source, List<BindVar> bindVars); // executes source, could be data manipulation - may not return anything
  public SourceProcessorResult querySource(final String sql, List<BindVar> bindVars);   // runs source as a query - should return one result.
}
