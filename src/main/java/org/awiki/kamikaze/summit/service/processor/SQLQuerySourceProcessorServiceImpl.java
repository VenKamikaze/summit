package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.List;

import org.awiki.kamikaze.summit.service.BindVarService;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTableExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * This service handles SQL Source Types that return a table of information.
 * It then returns them in a {@link SourceProcessorResultTable}
 * @author msaun
 *
 */
@Service("SQLQueryProcessor")
public class SQLQuerySourceProcessorServiceImpl implements TabularQuerySourceProcessorService {
  
  public static final String BUILT_IN_SQL_DML_SELECT_ROW_TYPE = "dml_selrow"; // query for a select statement, expects single row or no rows return.
  public static final String BUILT_IN_SQL_DML_SELECT_TYPE = "dml_select"; // query for a select statement, expects allow for zero or many rows return.

  @Autowired
  private BindVarService bindVarService;
  
  @Autowired
  private NamedParameterJdbcTemplate jdbc;

  public static final List<String> RESPONSIBILITIES = new ArrayList<String>(2);
  static { RESPONSIBILITIES.add(BUILT_IN_SQL_DML_SELECT_ROW_TYPE);
           RESPONSIBILITIES.add(BUILT_IN_SQL_DML_SELECT_TYPE); }
  
  @Override
  public List<String> getResponsibilities()
  {
    return RESPONSIBILITIES;
  }

  @Override
  @Cacheable(value="totalRecordCount")
  public Long getTotalRecordCount(final String fullQuery, final List<BindVar> bindVars) 
  {
    final SourceProcessorResultTable count = executeQuery(fullQuery, bindVars);
    return count.getBody().size() > 0 ? Long.parseLong(count.getBody().get(0).getCell(0).getValue()) : 0L;
  }
  
  @Override
  public SourceProcessorResultTable executeQuery(final String source, final List<BindVar> bindVars) {
    return jdbc.query(source,  new MapSqlParameterSource(bindVarService.convertBindVarsToSqlParameterMap(bindVars)), new SourceProcessorResultTableExtractor(null));
  }

}
