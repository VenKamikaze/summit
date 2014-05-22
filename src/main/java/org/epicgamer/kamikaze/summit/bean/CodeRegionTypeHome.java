// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class CodeRegionType.
 * @see .CodeRegionType
 * @author Hibernate Tools
 */
@Stateless
public class CodeRegionTypeHome
{

  private static final Log log = LogFactory.getLog(CodeRegionTypeHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(CodeRegionType transientInstance)
  {
    log.debug("persisting CodeRegionType instance");
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

  public void remove(CodeRegionType persistentInstance)
  {
    log.debug("removing CodeRegionType instance");
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

  public CodeRegionType merge(CodeRegionType detachedInstance)
  {
    log.debug("merging CodeRegionType instance");
    try
    {
      CodeRegionType result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public CodeRegionType findById(String id)
  {
    log.debug("getting CodeRegionType instance with id: " + id);
    try
    {
      CodeRegionType instance = entityManager.find(CodeRegionType.class, id);
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
