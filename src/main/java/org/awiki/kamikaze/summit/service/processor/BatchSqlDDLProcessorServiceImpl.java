package org.awiki.kamikaze.summit.service.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BatchSqlDDLProcessorServiceImpl implements BatchSourceProcessorService {

  private Logger log = LoggerFactory.getLogger(BatchSqlDDLProcessorServiceImpl.class);

  private JdbcTemplate jdbc;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbc)
  {
    this.jdbc = jdbc;
  }
  
  @Override
  public SourceProcessorResult executeBatch(final List<String> ddls) {
    final SourceProcessorResult result = new SourceProcessorResult();
    try {
      for(String ddl : ddls) {
        jdbc.execute(ddl);
      }
    } 
    catch(DataAccessException e) {
      final String error = "Unable to execute DDL source type";
      log.error(error, e);
      throw e;
    }
    result.setOutputMessage(SourceProcessorResult.STANDARD_SUCCESS_MESSAGE);
    result.setReturnCode(SourceProcessorResult.STANDARD_SUCCESS_CODE);
    return result;
  }
  
  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(Arrays.asList(BatchSourceProcessorService.BUILT_IN_SQL_DDL_BATCH_TYPE));
  }
}
