package org.awiki.kamikaze.summit.service.processor;

import java.util.List;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;

// TODO review: is this required at all?
public interface BatchSourceProcessorService extends SourceProcessorService {
  public static final String BUILT_IN_SQL_DML_BATCH_TYPE = "text/plain; sql; dml; batch";
  public static final String BUILT_IN_SQL_DDL_BATCH_TYPE = "text/plain; sql; ddl; batch";
  public static final String BUILT_IN_SQL_STORED_PROC = "text/plain; sql; code";
  
  //public static final String BUILT_IN_PYTHON2_TYPE = "text/plain; python2";
  
  public SourceProcessorResult executeBatch(final List<String> source); // executes source, could be data manipulation - may not return anything
}
