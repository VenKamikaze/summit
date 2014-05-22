package org.epicgamer.kamikaze.summit.transaction;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionDefinition;

public class BasicTransactionManager extends JpaTransactionManager
{

//  private static final String AUDIT_FUNCTION = "OPEN";
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(BasicTransactionManager.class);
  
  private SimpleJdbcCall auditProcedure;;

  @Override
  public void afterPropertiesSet()
  {
    super.afterPropertiesSet();
    //auditProcedure = new SimpleJdbcCall(getDataSource()).withProcedureName(OPEN);
  }

  @Override
  protected void doBegin(Object transaction, TransactionDefinition definition)
  {
    super.doBegin(transaction, definition);

    Map<String, String> params = new HashMap<String, String>();
    
    if (SecurityContextHolder.getContext().getAuthentication()==null)
    {
      logger.debug("SecurityContextHolder has no authentication");
    }
    else
    {
      /*Users */ Object user = /*(Users)*/ SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      
      //params.put("username", user.getUsername().toUpperCase());   
      //auditProcedure.execute(params);
    }
    
    
  }

  /*
   * If any participant in a transaction (eg a nested {@link org.springframework.transaction.annotation.Transactional}
   * method) throws an exception we want to rollback the entire transaction. If a particular participant wishes to avoid
   * causing the rollback then it needs to handle its exceptions accordingly (eg see
   * {@link org.springframework.transaction.annotation.Transactional#noRollbackFor()} ).
   */
  @Override
  protected boolean shouldCommitOnGlobalRollbackOnly()
  {
    return false;
  }
}
