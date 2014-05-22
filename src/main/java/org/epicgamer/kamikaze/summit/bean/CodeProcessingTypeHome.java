// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class CodeProcessingType.
 * @see .CodeProcessingType
 * @author Hibernate Tools
 */
@Stateless
public class CodeProcessingTypeHome
{

  private static final Log log = LogFactory
                                   .getLog(CodeProcessingTypeHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(CodeProcessingType transientInstance)
  {
    log.debug("persisting CodeProcessingType instance");
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

  public void remove(CodeProcessingType persistentInstance)
  {
    log.debug("removing CodeProcessingType instance");
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

  public CodeProcessingType merge(CodeProcessingType detachedInstance)
  {
    log.debug("merging CodeProcessingType instance");
    try
    {
      CodeProcessingType result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public CodeProcessingType findById(String id)
  {
    log.debug("getting CodeProcessingType instance with id: " + id);
    try
    {
      CodeProcessingType instance = entityManager.find(
          CodeProcessingType.class, id);
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
