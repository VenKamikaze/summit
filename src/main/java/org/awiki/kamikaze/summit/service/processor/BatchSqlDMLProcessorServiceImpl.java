package org.awiki.kamikaze.summit.service.processor;

import java.util.List;

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
public class BatchSqlDMLProcessorServiceImpl implements SingularSourceProcessorService {
  
  @Autowired
  private JdbcTemplate jdbc;
  
  @Override
  public boolean isResponsibleFor(String sourceType) {
    return (BUILT_IN_SQL_DML_TYPE.equals(sourceType));
  }

  @Override
  public SourceProcessorResult executeSource(final List<String> sqls) {
    int changedRows = 0;
    for(String sql : sqls) {
      changedRows += jdbc.update(sql);
    }
    return String.valueOf(changedRows); 
  }
  
  @Override
  public SourceProcessorResult querySource(final String sqls) {
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
    throw new NotImplementedException("You cannot batch query.");
  }
}
