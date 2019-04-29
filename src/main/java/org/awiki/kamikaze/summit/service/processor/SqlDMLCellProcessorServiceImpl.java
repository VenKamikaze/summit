package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

/**
 * This service is used for executing single SQL DML actions where a result containing one cell is to be returned,
 *   or where a single action is executed (e.g. a single insert or update).
 * For DDL actions, a different service should be used.
 * For select actions returning a row, a different service should be used
 * For select actions returning a table, use {@link SQLQueryReportRegionSourceProcessorServiceImpl}
 * 
 * FIXME: does not make sense to have MODIFY type here, this is for a Cell only!?
 * 
 * @author msaun
 */
@Service
public class SqlDMLCellProcessorServiceImpl implements SingularSourceProcessorService {
  
  @Autowired
  private NamedParameterJdbcTemplate jdbc;
  
  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(Arrays.asList(SingularSourceProcessorService.BUILT_IN_SQL_DML_MODIFY_TYPE,
      SingularSourceProcessorService.BUILT_IN_SQL_DML_SELECT_CELL_TYPE));
  }
  
  @Override
  public SourceProcessorResult processSource(final String source, final String sourceType, final List<BindVar> bindVars)
  {
    if(SingularSourceProcessorService.BUILT_IN_SQL_DML_MODIFY_TYPE.equals(sourceType)) {
      return executeSource(source, bindVars);
    }
    else {
      return querySource(source, bindVars);
    }
  }
  
  @Override
  public SourceProcessorResult executeSource(final String sql, final List<BindVar> bindVars) {
    SqlParameterSource params = mapBindVars(bindVars);
    int changedRows = jdbc.update(sql,params);
    return new SourceProcessorResult((long) changedRows, SourceProcessorResult.STANDARD_SUCCESS_CODE, SourceProcessorResult.STANDARD_SUCCESS_MESSAGE, null);
  }
  
  @Override
  public SourceProcessorResult querySource(final String sql, final List<BindVar> bindVars) {
    SqlParameterSource params = mapBindVars(bindVars);
    
    SourceProcessorResult result = new SourceProcessorResult();
    try {
      result.setResultValue(jdbc.queryForObject(sql, params, String.class));
      result.setReturnCode(SourceProcessorResult.STANDARD_SUCCESS_CODE);
      result.setOutputMessage(SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
    }
    catch(EmptyResultDataAccessException emptyE) {
      result.setResultValue(null);
      result.setReturnCode(SourceProcessorResult.STANDARD_NO_RESULT_CODE);
      result.setOutputMessage(SourceProcessorResult.STANDARD_NO_RESULT_MESSAGE);
    }
    return result;
    /*
    List<String> results = jdbc.queryForList(sql, String.class);
    String result = "";
    for (String row: results)
    {
      result += row;
    }
    return result;
    */
    //return "evaluate if this makes sense here.";
  }

  protected SqlParameterSource mapBindVars(List<BindVar> bindVars)
  {
    MapSqlParameterSource params = new MapSqlParameterSource();
    for(BindVar var : bindVars)
    {
      params.addValue(var.getBindParameter(), var.getValue());
    }
    return params;
  }

}
