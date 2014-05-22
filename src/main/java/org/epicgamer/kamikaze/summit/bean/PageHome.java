// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class Page.
 * @see .Page
 * @author Hibernate Tools
 */
@Stateless
public class PageHome
{

  private static final Log log = LogFactory.getLog(PageHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(Page transientInstance)
  {
    log.debug("persisting Page instance");
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

  public void remove(Page persistentInstance)
  {
    log.debug("removing Page instance");
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

  public Page merge(Page detachedInstance)
  {
    log.debug("merging Page instance");
    try
    {
      Page result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public Page findById(int id)
  {
    log.debug("getting Page instance with id: " + id);
    try
    {
      Page instance = entityManager.find(Page.class, id);
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
