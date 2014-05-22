// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class CodeRegionPosition.
 * @see .CodeRegionPosition
 * @author Hibernate Tools
 */
@Stateless
public class CodeRegionPositionHome
{

  private static final Log log = LogFactory
                                   .getLog(CodeRegionPositionHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(CodeRegionPosition transientInstance)
  {
    log.debug("persisting CodeRegionPosition instance");
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

  public void remove(CodeRegionPosition persistentInstance)
  {
    log.debug("removing CodeRegionPosition instance");
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

  public CodeRegionPosition merge(CodeRegionPosition detachedInstance)
  {
    log.debug("merging CodeRegionPosition instance");
    try
    {
      CodeRegionPosition result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public CodeRegionPosition findById(String id)
  {
    log.debug("getting CodeRegionPosition instance with id: " + id);
    try
    {
      CodeRegionPosition instance = entityManager.find(
          CodeRegionPosition.class, id);
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
