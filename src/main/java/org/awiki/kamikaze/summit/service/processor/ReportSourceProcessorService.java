package org.awiki.kamikaze.summit.service.processor;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;

public interface ReportSourceProcessorService extends SourceProcessorService {
  public static final String BUILT_IN_SQL_DML_TYPE_REPORT = "text/plain; sql; dml; report";
  
  public SourceProcessorResultTable querySource(final String source);   // runs source as a query - should return one result.
}
