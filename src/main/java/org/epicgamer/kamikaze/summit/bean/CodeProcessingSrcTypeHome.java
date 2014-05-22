// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class CodeProcessingSrcType.
 * @see .CodeProcessingSrcType
 * @author Hibernate Tools
 */
@Stateless
public class CodeProcessingSrcTypeHome
{

  private static final Log log = LogFactory
                                   .getLog(CodeProcessingSrcTypeHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(CodeProcessingSrcType transientInstance)
  {
    log.debug("persisting CodeProcessingSrcType instance");
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

  public void remove(CodeProcessingSrcType persistentInstance)
  {
    log.debug("removing CodeProcessingSrcType instance");
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

  public CodeProcessingSrcType merge(CodeProcessingSrcType detachedInstance)
  {
    log.debug("merging CodeProcessingSrcType instance");
    try
    {
      CodeProcessingSrcType result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public CodeProcessingSrcType findById(String id)
  {
    log.debug("getting CodeProcessingSrcType instance with id: " + id);
    try
    {
      CodeProcessingSrcType instance = entityManager.find(
          CodeProcessingSrcType.class, id);
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
