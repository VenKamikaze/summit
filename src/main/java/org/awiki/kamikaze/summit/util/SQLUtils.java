package org.awiki.kamikaze.summit.util;

import java.util.Collection;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.awiki.kamikaze.summit.util.DatabaseTypeEnum.*;


public class SQLUtils
{
  private static final Logger logger = LoggerFactory.getLogger(SQLUtils.class);
  
  public static StringBuilder buildWrapperQuery(final String innerQuery, final Collection<String> columnList) {
    StringBuilder fullQuery = new StringBuilder();
    fullQuery.append("SELECT ").append(StringUtils.join(columnList, ", "));
    fullQuery.append(" FROM ( " + innerQuery + " ) SUBQUERY ");
    return fullQuery;     
  }
  
  /**
   * Wrap innerQuery and select all columns (*) from it.
   * @param innerQuery
   * @return StringBuilder - the innerQuery wrapped as a subquery.
   */
  public static StringBuilder buildWrapperQuery(final String innerQuery) {
    StringBuilder fullQuery = new StringBuilder();
    fullQuery.append("SELECT ").append(" * ");
    fullQuery.append(" FROM ( " + innerQuery + " ) SUBQUERY ");
    return fullQuery;     
  }
  
  /**
   * Adds database agnostic pagination to the query.
   * TODO: Use JSQLParser?
   * @param currentQuery - the query to add pagination to that does not already have row limiting.
   * @param columnList - list of columns to select (required if Oracle) - you can specify '*' as columnList too.
   * @param rowLimit   - number of rows to limit resultset to
   * @param pageNumber - optional if not just limiting results
   * @return StringBuilder
   */
  public static StringBuilder addPaginationToQuery(StringBuilder currentQuery, final String columnList, long rowLimit, Long pageNumber) {
    
    switch(DatabaseUtils.get().getDetectedDBType()) {
      case ORACLE:
        final String rownumAlias = "RNUM";
        
        StringBuilder paginationQuery = new StringBuilder(currentQuery.length() + (columnList.length()*2) + 100); // currentQuery length, plus two lots of the columnList for subquerying, plus additional wrapping syntax
        paginationQuery.insert(0, "SELECT ").append(columnList).append(" FROM ( "); // outermost query
        paginationQuery.append(" SELECT rownum ").append(rownumAlias).append(", ").append(columnList).append(" FROM ("); // first inner query with rownum in select 
        paginationQuery.append(currentQuery).append(" ) "); // inner most query (or queries)
        paginationQuery.append( " WHERE ").append(" rownum <= ").append((pageNumber != null ? (pageNumber * rowLimit) : rowLimit)); // first inner query -- predicate limiting results less than max via rownum
        paginationQuery.append( " ) ");
        if(pageNumber != null) {
          paginationQuery.append(" WHERE ").append(rownumAlias).append( " > ").append(pageNumber <= 1 ? 0 : ( (pageNumber-1) * rowLimit));
        }
        currentQuery = paginationQuery;
        return paginationQuery;
        
      case POSTGRES:
      case MYSQL:
        currentQuery.append(" LIMIT ").append(rowLimit);
        if(pageNumber > 1) {
          currentQuery.append(" OFFSET ").append((pageNumber-1) * rowLimit);
        }
        break;
        
      case SQLSERVER:
      {
        String error = "Not yet supported. FIXME, TODO SQLServer: " + DatabaseUtils.get().getDetectedDBType();
        logger.error(error);
        throw new NotYetImplementedException(error);
      }
      
      default:
      {
        String error = "Unknown detected DB type enum value: " + DatabaseUtils.get().getDetectedDBType();
        logger.error(error);
        throw new RuntimeException(error);
      }
    }
    return currentQuery;
  }
  
  /**
   * 
   * @param sql
   * @return
   */
  public static String stripCriteriaClause(final String sqlSelect) {
    try {
      Statement stmt = CCJSqlParserUtil.parse(sqlSelect);
      logger.debug("Trying to strip criteria clauses off:" + sqlSelect);
      if(stmt instanceof PlainSelect) {
        return stripCriteriaClause((PlainSelect)stmt); 
      }
      else if(stmt instanceof Select) {
        return stripCriteriaClause((Select)stmt); 
      }
      else { //(! (stmt instanceof Select)) {
        logger.warn("This function is only for stripping criteria from SELECT statements.");
        return sqlSelect;
      }
    }
    catch (JSQLParserException e) {
      logger.error("Hit parse exception when parsing SQL=" + sqlSelect, e);
      throw new RuntimeException(e);
    }
  }
  
  public static String stripCriteriaClause(Select select) {
    return stripCriteriaClause((PlainSelect) select.getSelectBody());
  }
  
  public static String stripCriteriaClause(PlainSelect plainSelect) {
    SelectDeParserMinusWhere deParser = new SelectDeParserMinusWhere();
    deParser.visit(plainSelect);
    logger.debug("Stripped SQL is:" + deParser.getBuffer().toString());
    return deParser.getBuffer().toString();
  }
}
