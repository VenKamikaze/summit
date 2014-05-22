// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class ApplicationPage.
 * @see .ApplicationPage
 * @author Hibernate Tools
 */
@Stateless
public class ApplicationPageHome
{

  private static final Log log = LogFactory.getLog(ApplicationPageHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(ApplicationPage transientInstance)
  {
    log.debug("persisting ApplicationPage instance");
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

  public void remove(ApplicationPage persistentInstance)
  {
    log.debug("removing ApplicationPage instance");
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

  public ApplicationPage merge(ApplicationPage detachedInstance)
  {
    log.debug("merging ApplicationPage instance");
    try
    {
      ApplicationPage result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public ApplicationPage findById(int id)
  {
    log.debug("getting ApplicationPage instance with id: " + id);
    try
    {
      ApplicationPage instance = entityManager.find(ApplicationPage.class, id);
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
