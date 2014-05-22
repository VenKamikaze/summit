// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class CodeFieldSourceType.
 * @see .CodeFieldSourceType
 * @author Hibernate Tools
 */
@Stateless
public class CodeFieldSourceTypeHome
{

  private static final Log log = LogFactory
                                   .getLog(CodeFieldSourceTypeHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(CodeFieldSourceType transientInstance)
  {
    log.debug("persisting CodeFieldSourceType instance");
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

  public void remove(CodeFieldSourceType persistentInstance)
  {
    log.debug("removing CodeFieldSourceType instance");
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

  public CodeFieldSourceType merge(CodeFieldSourceType detachedInstance)
  {
    log.debug("merging CodeFieldSourceType instance");
    try
    {
      CodeFieldSourceType result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public CodeFieldSourceType findById(String id)
  {
    log.debug("getting CodeFieldSourceType instance with id: " + id);
    try
    {
      CodeFieldSourceType instance = entityManager.find(
          CodeFieldSourceType.class, id);
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
