package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.awiki.kamikaze.summit.service.processor.BatchSourceProcessorService;
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
public class BatchSqlDMLProcessorServiceImpl implements BatchSourceProcessorService {
  
  private JdbcTemplate jdbc;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbc)
  {
    this.jdbc = jdbc;
  }

  
  @Override
  public SourceProcessorResult executeBatch(final List<String> sqls) {
    SourceProcessorResult result = new SourceProcessorResult();
    int changedRows = 0;
    for(String sql : sqls) {
      changedRows += jdbc.update(sql);
    }
    result.setRowsAffected(changedRows);
    result.setReturnCode(SourceProcessorResult.STANDARD_SUCCESS_CODE);
    result.setOutputMessage(SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
    return result; 
  }

  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(Arrays.asList(BatchSourceProcessorService.BUILT_IN_SQL_DML_BATCH_TYPE));
  }

}
