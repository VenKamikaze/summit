// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class PageRegion.
 * @see .PageRegion
 * @author Hibernate Tools
 */
@Stateless
public class PageRegionHome
{

  private static final Log log = LogFactory.getLog(PageRegionHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(PageRegion transientInstance)
  {
    log.debug("persisting PageRegion instance");
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

  public void remove(PageRegion persistentInstance)
  {
    log.debug("removing PageRegion instance");
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

  public PageRegion merge(PageRegion detachedInstance)
  {
    log.debug("merging PageRegion instance");
    try
    {
      PageRegion result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public PageRegion findById(int id)
  {
    log.debug("getting PageRegion instance with id: " + id);
    try
    {
      PageRegion instance = entityManager.find(PageRegion.class, id);
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
