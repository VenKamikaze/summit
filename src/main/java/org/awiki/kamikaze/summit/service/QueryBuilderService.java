package org.awiki.kamikaze.summit.service;

import java.util.Collection;

public interface QueryBuilderService
{
  public String buildWrapperCountQuery(final String innerQuery, final String filterType, final String columnToFilter, final Collection<String> columnList, final String searchText);
  
  public String buildWrapperQuery(final String innerQuery, final String filterType, final String columnToFilter, final Collection<String> columnList, final String searchText, long page, long rowsPerPage);
}
