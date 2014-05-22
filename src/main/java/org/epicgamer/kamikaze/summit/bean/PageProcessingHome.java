// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class PageProcessing.
 * @see .PageProcessing
 * @author Hibernate Tools
 */
@Stateless
public class PageProcessingHome
{

  private static final Log log = LogFactory.getLog(PageProcessingHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(PageProcessing transientInstance)
  {
    log.debug("persisting PageProcessing instance");
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

  public void remove(PageProcessing persistentInstance)
  {
    log.debug("removing PageProcessing instance");
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

  public PageProcessing merge(PageProcessing detachedInstance)
  {
    log.debug("merging PageProcessing instance");
    try
    {
      PageProcessing result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public PageProcessing findById(int id)
  {
    log.debug("getting PageProcessing instance with id: " + id);
    try
    {
      PageProcessing instance = entityManager.find(PageProcessing.class, id);
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
