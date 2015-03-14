package org.awiki.kamikaze.summit.service.processor;

import java.util.List;

public interface SourceProcessorService {
  public static final String BUILT_IN_SQL_DML_TYPE = "text/plain; sql; dml";
  public static final String BUILT_IN_SQL_DML_BATCH_TYPE = "text/plain; sql; dml; batch";
  public static final String BUILT_IN_SQL_DDL_TYPE = "text/plain; sql; ddl";
  public static final String BUILT_IN_SQL_STORED_PROC = "text/plain; sql; code";
  
  //public static final String BUILT_IN_PYTHON2_TYPE = "text/plain; python2";
  
  public boolean isResponsibleFor(final String sourceType);
  public String executeSource(final List<String> source); // executes source, could be data manipulation - may not return anything
  public String querySource(final List<String> source);   // runs source as a query - should return one result.
}
