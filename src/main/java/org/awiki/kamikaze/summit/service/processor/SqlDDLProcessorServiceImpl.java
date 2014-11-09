package org.awiki.kamikaze.summit.service.processor;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SqlDDLProcessorServiceImpl implements SourceProcessorService {

  private Logger log = Logger.getLogger(SqlDDLProcessorServiceImpl.class);
  
  @Autowired
  JdbcTemplate jdbc;

  @Override
  public boolean isResponsibleFor(String sourceType) {
    return (BUILT_IN_SQL_DDL_TYPE.equals(sourceType));
  }

  @Override
  public String executeSource(final String ddl) {
    try {
      jdbc.execute(ddl);
    } 
    catch(DataAccessException e) {
      final String error = "Unable to execute DDL source type";
      log.error(error, e);
      return error;
    }
    return ddl;
  }
  
  @Override
  public String querySource(final String ddl) {
    throw new NotImplementedException("Cannot querySource with DDL! Maybe you want SqlDMLProcessor?");
  }
}
