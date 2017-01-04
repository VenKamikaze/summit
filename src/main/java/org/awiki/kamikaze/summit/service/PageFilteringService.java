package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;

public interface PageFilteringService {
  
  /* No pagination */
  public SourceProcessorResultTable filterRegion(long regionId, final String searchText);
  public SourceProcessorResultTable filterRegionByColumn(long regionId, final String columnName, final String searchText);
  
  /* With pagination */
  public SourceProcessorResultTable filterRegion(long regionId, final String filterType, final String searchText, long page, long rowsPerPage, boolean getTotalCount);
  public SourceProcessorResultTable filterRegionByColumn(long regionId, final String filterType, final String columnName, final String searchText, long page, long rowsPerPage, boolean getTotalCount);
  
  
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, final String filterType, final String searchText, long page, long rowsPerPage, boolean getTotalCount);
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, final String filterType, final String columnName, final String searchText, long page, long rowsPerPage, boolean getTotalCount);
  
}
