package org.awiki.kamikaze.summit.service.processor;

import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * This service is used for execute single SQL DML actions.
 * For DDL actions, a different service should be used.
 * 
 * @author msaun
 */
@Service
public class SqlDMLProcessorServiceImpl implements SingularSourceProcessorService {
  
  @Autowired
  private JdbcTemplate jdbc;
  
  @Override
  public boolean isResponsibleFor(String sourceType) {
    return (BUILT_IN_SQL_DML_TYPE.equals(sourceType));
  }

  @Override
  public SourceProcessorResult executeSource(final String sql) {
    int changedRows = jdbc.update(sql);
    return new SourceProcessorResult(changedRows, SourceProcessorResult.STANDARD_SUCCESS_CODE, SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
  }
  
  @Override
  public SourceProcessorResult querySource(final String sql, List<BindVar<Types>> bindVars) {
    
    Map<String, Object> mapBindVars = new HashMap<>(bindVars.size());
    for(BindVar var : bindVars)
    {
      mapBindVars.put(var.getBindParameter(), var.getValue());
    }
    
    SourceProcessorResult result = new SourceProcessorResult();
    result.setResultValue(jdbc.queryForObject(sql, mapBindVars, String.class));
    result.setReturnCode(SourceProcessorResult.STANDARD_SUCCESS_CODE);
    result.setOutputMessage(SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
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
}
