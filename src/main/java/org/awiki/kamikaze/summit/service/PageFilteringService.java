package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;

public interface PageFilteringService {
  
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, final String searchText);
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, final String columnName, final String searchText);
  
}
