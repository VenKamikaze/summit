package org.awiki.kamikaze.summit.service;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

@Service("sqlQueryBuilder")
public class SqlQueryBuilderServiceImpl implements QueryBuilderService
{
  @Autowired
  private DriverManagerDataSource dataSource;
  
  private DatabaseTypeEnum detectedDbType = null;
  
  private static final Logger logger = LoggerFactory.getLogger(SqlQueryBuilderServiceImpl.class);

  public String buildWrapperCountQuery(final String innerQuery, final String filterType, final String columnToFilter, final Collection<String> columnList, final String searchText) {
    StringBuilder countQuery = new StringBuilder();
    countQuery.append("SELECT COUNT(1) as TOTALCOUNT");
    countQuery.append(" FROM ( " + innerQuery + " ) SUBQUERY ");
    if(StringUtils.isNotEmpty(searchText)) {
      addPredicate(countQuery, filterType, columnToFilter, columnList);
    }
    return countQuery.toString(); // if no search is specified, just run the standard query.
  }
  
  public String buildWrapperQuery(final String innerQuery, final String filterType, final String columnToFilter, final Collection<String> columnList, final String searchText,
          long page, long rowsPerPage) {
    if(StringUtils.isNotEmpty(searchText)) {
      StringBuilder fullQuery = new StringBuilder();
      fullQuery.append("SELECT ").append(StringUtils.join(columnList, ", "));
      fullQuery.append(" FROM ( " + innerQuery + " ) SUBQUERY ");
      addPredicate(fullQuery, filterType, columnToFilter, columnList);
      if(page > 0)
        addPaginationToQuery(fullQuery, StringUtils.join(columnList, ", "), true, page, rowsPerPage);
      return fullQuery.toString();      
    }
    if(page > 0)
      return addPaginationToQuery(new StringBuilder(innerQuery), StringUtils.join(columnList, ", "), false, page, rowsPerPage).toString();
    
    return innerQuery; // if no search is specified, and we are not using pagination, just run the standard query.
  }

  @SuppressWarnings("serial")
  private String addPredicate(StringBuilder query, final String filterType, final String columnToFilter, final Collection<String> columnList) {
    if(StringUtils.isNotBlank(columnToFilter)) {
      /* A column is specified - query against the column specified (check it is valid first) */
      if(columnList.contains(columnToFilter)) {
        query.append(" WHERE ").append(wrapInCast(new ArrayList<String>(1) {{ add(columnToFilter); }}, "varchar")).append(handleFilterType(FilterTypeEnum.valueOf(filterType.toUpperCase()), ":searchText"));
      }
    }
    else {
      /* No column specified - query against ALL columns */
      query.append(" WHERE ").append(StringUtils.join( wrapInCast(columnList, "varchar"), handleFilterType(FilterTypeEnum.valueOf(filterType.toUpperCase()), ":searchText")+ " OR ")).append(handleFilterType(FilterTypeEnum.valueOf(filterType), ":searchText"));
    }
    return query.toString();
  }
  
  private String handleFilterType(final FilterTypeEnum filterType, final String parameterName) {
    String predicate;
    switch(filterType) {
      case CONTAINS:      
        predicate = filterType.getSql() + " '%' || " + parameterName + " || '%' ";
        break;
        
      case ENDSWITH:
        predicate = filterType.getSql() + " '%' || " + parameterName;
        break;
        
      case STARTSWITH:
        predicate = filterType.getSql() + " " + parameterName + " || '%' ";
        break;
        
      case EXACT:
        predicate = filterType.getSql() + parameterName;
        break;
      
      default:
      {
        String error = "Unknown detected filtertype type enum value: " + filterType.getType();
        logger.error(error);
        throw new RuntimeException(error);
      }
    }
    return predicate;
  }
  
  private Collection<String> wrapInCast(final Collection<String> columnList, final String castType) {
    Collection<String> castedColumns = new ArrayList<String>(columnList.size());
    for(String col : columnList) {
      castedColumns.add("CAST (" + col + " as " + castType + ")");
    }
    return castedColumns;
  }
  
  private StringBuilder addPaginationToQuery(StringBuilder currentQuery, final String columnList, boolean hasWhereClause, long page, long rowsPerPage) {
    if(detectedDbType == null)
      detectDbType();
    
    switch(detectedDbType) {
      case ORACLE:
        // append rownum as column to the current query
        int firstSelect = currentQuery.indexOf("SELECT ");
        currentQuery.insert(firstSelect, "rownum RNUM, ");
        if(! hasWhereClause) {
          currentQuery.append(" WHERE ");
        }
        else {
          currentQuery.append(" AND ");
        }
        // limit max rows to the last row of the current page.
        currentQuery.append(" rownum <= ").append(page * rowsPerPage);
        // Sub-Query the current query again, so we can specify rows to start from.
        currentQuery.insert(0, "SELECT RNUM, ").append(columnList).append(" FROM ( ");
        currentQuery.append(" ) WHERE RNUM >= ").append(page <= 1 ? 1 : (page-1 * rowsPerPage));
        break;
        
      case POSTGRES:
      case MYSQL:
        currentQuery.append(" LIMIT ").append(rowsPerPage);
        if(page > 1) {
          currentQuery.append(" OFFSET ").append((page-1) * rowsPerPage);
        }
        break;
        
      case SQLSERVER:
      {
        String error = "Not yet supported. FIXME, TODO SQLServer: " + detectedDbType;
        logger.error(error);
        throw new NotYetImplementedException(error);
      }
      
      default:
      {
        String error = "Unknown detected DB type enum value: " + detectedDbType;
        logger.error(error);
        throw new RuntimeException(error);
      }
    }
    return currentQuery;
  }
  
  private void detectDbType() {
    final String dbConnectionUrl = dataSource.getUrl() != null ? dataSource.getUrl().toLowerCase() : null;
    if(dbConnectionUrl != null) {
      if(dbConnectionUrl.contains("oracle")) {
        detectedDbType = DatabaseTypeEnum.ORACLE;
      }
      else if (dbConnectionUrl.contains("postgres")) {
        detectedDbType = DatabaseTypeEnum.POSTGRES;
      }
      else if (dbConnectionUrl.contains("mysql")) {
        detectedDbType = DatabaseTypeEnum.MYSQL;
      }
      else if (dbConnectionUrl.contains("sqlserver")) {
        detectedDbType = DatabaseTypeEnum.SQLSERVER;
      }
      else {
        logger.warn("Unknown database type detected! This is determined from the database connection URL, which is detected as: " + dbConnectionUrl);
        logger.warn("Falling back to postgres style pagination. Pagination may not function correctly.");
        detectedDbType = DatabaseTypeEnum.POSTGRES;
      }
      logger.info("Detected DB type enum is: " + detectedDbType);
    }
  }

}
