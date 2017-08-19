package org.awiki.kamikaze.summit.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;

public interface QueryBuilderService
{
  public String buildWrapperCountQuery(final String innerQuery, final String filterType, final String columnToFilter, final Collection<String> columnList, final String searchText);
  
  public String buildWrapperQuery(final String innerQuery, final String filterType, final String columnToFilter, final Collection<String> columnList, final String searchText, long page, long rowsPerPage);
}
