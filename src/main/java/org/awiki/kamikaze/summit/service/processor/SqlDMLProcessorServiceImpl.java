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
public class SqlDMLProcessorServiceImpl implements SourceProcessorService {
  
  @Autowired
  JdbcTemplate jdbc;
  
  @Override
  public boolean isResponsibleFor(String sourceType) {
    return (BUILT_IN_SQL_DML_TYPE.equals(sourceType));
  }

  @Override
  public String executeSource(final String sql) {
    int result = jdbc.update(sql);
    return String.valueOf(result);
  }
  
  @Override
  public String querySource(final String sql) {
    List<String> results = jdbc.queryForList(sql, String.class);
    String result = "";
    for (String row: results)
    {
      result += row;
    }
    return result;
  }
}
