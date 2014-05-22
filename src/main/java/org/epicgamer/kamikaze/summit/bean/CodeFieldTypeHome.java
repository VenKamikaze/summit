// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class CodeFieldType.
 * @see .CodeFieldType
 * @author Hibernate Tools
 */
@Stateless
public class CodeFieldTypeHome
{

  private static final Log log = LogFactory.getLog(CodeFieldTypeHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(CodeFieldType transientInstance)
  {
    log.debug("persisting CodeFieldType instance");
    try
    {
      entityManager.persist(transientInstance);
      log.debug("persist successful");
    }
    catch (RuntimeException re)
    {
      log.error("persist failed", re);
      throw re;
    }
  }

  public void remove(CodeFieldType persistentInstance)
  {
    log.debug("removing CodeFieldType instance");
    try
    {
      entityManager.remove(persistentInstance);
      log.debug("remove successful");
    }
    catch (RuntimeException re)
    {
      log.error("remove failed", re);
      throw re;
    }
  }

  public CodeFieldType merge(CodeFieldType detachedInstance)
  {
    log.debug("merging CodeFieldType instance");
    try
    {
      CodeFieldType result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public CodeFieldType findById(String id)
  {
    log.debug("getting CodeFieldType instance with id: " + id);
    try
    {
      CodeFieldType instance = entityManager.find(CodeFieldType.class, id);
      log.debug("get successful");
      return instance;
    }
    catch (RuntimeException re)
    {
      log.error("get failed", re);
      throw re;
    }
  }
}
