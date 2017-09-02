package org.awiki.kamikaze.summit.service.report;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.service.BindVarService;
import org.awiki.kamikaze.summit.util.DatabaseUtils;
import org.awiki.kamikaze.summit.util.SQLUtils;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reportSQLQueryBuilder")
public class SQLReportQueryBuilderServiceImpl implements SQLReportQueryBuilderService
{
  /** TODO REFACTOR A LOT OF THIS OUT INTO {@link SQLUtils} */
  
  @Autowired
  private BindVarService bindVarService;
  
  @Autowired
  private DatabaseUtils databaseUtils;
  
  private static final Logger logger = LoggerFactory.getLogger(SQLReportQueryBuilderServiceImpl.class);

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
      StringBuilder fullQuery = SQLUtils.buildWrapperQuery(innerQuery, columnList);
      addPredicate(fullQuery, filterType, columnToFilter, columnList);
      if(page > 0)
        fullQuery = addPaginationToQuery(fullQuery, StringUtils.join(columnList, ", "), true, page, rowsPerPage);
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
        query.append(" WHERE ").append(wrapInCast(new ArrayList<String>(1) {{ add(columnToFilter); }}, databaseUtils.getVarcharCastType())).append(handleFilterType(FilterTypeEnum.valueOf(filterType.toUpperCase()), ":searchText"));
      }
    }
    else {
      /* No column specified - query against ALL columns */
      query.append(" WHERE ").append(StringUtils.join( wrapInCast(columnList, databaseUtils.getVarcharCastType()), handleFilterType(FilterTypeEnum.valueOf(filterType.toUpperCase()), ":searchText")+ " OR ")).append(handleFilterType(FilterTypeEnum.valueOf(filterType), ":searchText"));
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
       
    switch(databaseUtils.getDetectedDBType()) {
      case ORACLE:
        final String rownumAlias = "RNUM";
        
        StringBuilder paginationQuery = new StringBuilder(currentQuery.length() + (columnList.length()*2) + 100); // currentQuery length, plus two lots of the columnList for subquerying, plus additional wrapping syntax
        paginationQuery.insert(0, "SELECT ").append(columnList).append(" FROM ( "); // outermost query
        paginationQuery.append(" SELECT rownum ").append(rownumAlias).append(", ").append(columnList).append(" FROM ("); // first inner query with rownum in select 
        paginationQuery.append(currentQuery).append(" ) "); // inner most query (or queries)
        paginationQuery.append( " WHERE ").append(" rownum <= ").append(page * rowsPerPage); // first inner query -- predicate limiting results less than max via rownum
        paginationQuery.append( " ) ");
        paginationQuery.append(" WHERE ").append(rownumAlias).append( " > ").append(page <= 1 ? 0 : ( (page-1) * rowsPerPage));
        return paginationQuery;
        
      case POSTGRES:
      case MYSQL:
        currentQuery.append(" LIMIT ").append(rowsPerPage);
        if(page > 1) {
          currentQuery.append(" OFFSET ").append((page-1) * rowsPerPage);
        }
        break;
        
      case SQLSERVER:
      {
        String error = "Not yet supported. FIXME, TODO SQLServer: " + databaseUtils.getDetectedDBType();
        logger.error(error);
        throw new NotYetImplementedException(error);
      }
      
      default:
      {
        String error = "Unknown detected DB type enum value: " + databaseUtils.getDetectedDBType();
        logger.error(error);
        throw new RuntimeException(error);
      }
    }
    return currentQuery;
  }

}
