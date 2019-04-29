package org.awiki.kamikaze.summit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class DatabaseUtils implements InitializingBean
{
  private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
  private static DatabaseUtils instance;
  
  @Autowired
  private DriverManagerDataSource dataSource;
  
  private DatabaseTypeEnum detectedDbType = null;
  
  public static final String ORACLE_VARCHAR = "varchar2(4000)";
  public static final String VARCHAR = "varchar";
  
  public static DatabaseUtils get() {
    return instance;
  }

  @Override
  public void afterPropertiesSet() throws Exception
  {
    detectDbType();
    instance = this;
  }
  
  /**
   * Gets the database specific type for varchar (as e.g. it is varchar2 and needs length for oracle)
   * @return
   */
  public String getVarcharCastType() {
    switch(detectedDbType) {
      case ORACLE:
        return ORACLE_VARCHAR;
        
      default:
        return VARCHAR;
    }
  }
  
  // TODO consider changing this to perform database specific queries to determine the DB type, e.g. select 1 from dual; for oracle.
  private void detectDbType() {
    final String dbConnectionUrl = dataSource.getUrl() != null ? dataSource.getUrl().toLowerCase() : null;
    if(dbConnectionUrl != null) {
      if(dbConnectionUrl.contains("oracle")) {
        detectedDbType = DatabaseTypeEnum.ORACLE;
      }
      else if (dbConnectionUrl.contains("postgres")) {
        detectedDbType = DatabaseTypeEnum.POSTGRES;
      }
      else if (dbConnectionUrl.contains("mysql")) {
        detectedDbType = DatabaseTypeEnum.MYSQL;
      }
      else if (dbConnectionUrl.contains("sqlserver")) {
        detectedDbType = DatabaseTypeEnum.SQLSERVER;
      }
      else {
        logger.warn("Unknown database type detected! This is determined from the database connection URL, which is detected as: " + dbConnectionUrl);
        logger.warn("Falling back to postgres style pagination. Pagination may not function correctly.");
        detectedDbType = DatabaseTypeEnum.POSTGRES;
      }
      logger.info("Detected DB type enum is: " + detectedDbType);
    }
  }
  
  public DatabaseTypeEnum getDetectedDBType() {
    return detectedDbType;
  }

 
}
