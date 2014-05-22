// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class ApplicationSchemas.
 * @see .ApplicationSchemas
 * @author Hibernate Tools
 */
@Stateless
public class ApplicationSchemasHome
{

  private static final Log log = LogFactory
                                   .getLog(ApplicationSchemasHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(ApplicationSchemas transientInstance)
  {
    log.debug("persisting ApplicationSchemas instance");
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

  public void remove(ApplicationSchemas persistentInstance)
  {
    log.debug("removing ApplicationSchemas instance");
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

  public ApplicationSchemas merge(ApplicationSchemas detachedInstance)
  {
    log.debug("merging ApplicationSchemas instance");
    try
    {
      ApplicationSchemas result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public ApplicationSchemas findById(ApplicationSchemasId id)
  {
    log.debug("getting ApplicationSchemas instance with id: " + id);
    try
    {
      ApplicationSchemas instance = entityManager.find(
          ApplicationSchemas.class, id);
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
