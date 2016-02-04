package org.awiki.kamikaze.summit.service.processor;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.awiki.kamikaze.summit.service.processor.bindvars.BindVar;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class SqlDDLProcessorServiceImpl implements SingularSourceProcessorService {

  private Logger log = LoggerFactory.getLogger(SqlDDLProcessorServiceImpl.class);
  
  @Autowired
  JdbcTemplate jdbc;

  @Override
  public List<String> getResponsibilities()
  {
    return new ArrayList<String>(Arrays.asList(SingularSourceProcessorService.BUILT_IN_SQL_DDL_TYPE));
  }

  @Override
  public SourceProcessorResult executeSource(final String ddl, List<BindVar<Types>> bindVars) {
    Assert.isTrue(bindVars == null || bindVars.isEmpty()); // execute immediate can support bind? consider..
    try {
      jdbc.execute(ddl);
    } 
    catch(DataAccessException e) {
      final String error = "Unable to execute DDL source type";
      log.error(error, e);
      throw e;
    }
    return null;
  }
  
  @Override
  public SourceProcessorResult querySource(final String ddl, List<BindVar<Types>> bindVars) {
    throw new NotImplementedException("Cannot querySource with DDL! Maybe you want SqlDMLProcessor?");
  }

}
