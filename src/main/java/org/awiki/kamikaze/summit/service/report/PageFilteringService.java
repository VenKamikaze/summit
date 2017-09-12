package org.awiki.kamikaze.summit.service.report;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.springframework.util.MultiValueMap;

public interface PageFilteringService {
  
  /* With pagination */
  public SourceProcessorResultTable filterRegion(long regionId, final String filterType, final String searchText, long page, long rowsPerPage, boolean getTotalCount, MultiValueMap<String, String> parameterMap);
  public SourceProcessorResultTable filterRegionByColumn(long regionId, final String filterType, final String columnName, final String searchText, long page, long rowsPerPage, boolean getTotalCount, MultiValueMap<String, String> parameterMap);
  
  
  public SourceProcessorResultTable filterPage(long applicationId, long pageId, final String filterType, final String searchText, long page, long rowsPerPage, boolean getTotalCount, MultiValueMap<String, String> parameterMap);
  public SourceProcessorResultTable filterPageByColumn(long applicationId, long pageId, final String filterType, final String columnName, final String searchText, long page, long rowsPerPage, boolean getTotalCount, MultiValueMap<String, String> parameterMap);
  
}
