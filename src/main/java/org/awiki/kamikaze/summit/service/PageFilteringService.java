package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;

public interface PageFilteringService {
  
  public SourceProcessorResultTable filterRegion(long regionId, final String searchText);
  public SourceProcessorResultTable filterRegionByColumn(long regionId, final String columnName, final String searchText);
  
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, final String searchText);
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, final String columnName, final String searchText);
  
}
