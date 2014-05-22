// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class RegionField.
 * @see .RegionField
 * @author Hibernate Tools
 */
@Stateless
public class RegionFieldHome
{

  private static final Log log = LogFactory.getLog(RegionFieldHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(RegionField transientInstance)
  {
    log.debug("persisting RegionField instance");
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

  public void remove(RegionField persistentInstance)
  {
    log.debug("removing RegionField instance");
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

  public RegionField merge(RegionField detachedInstance)
  {
    log.debug("merging RegionField instance");
    try
    {
      RegionField result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public RegionField findById(int id)
  {
    log.debug("getting RegionField instance with id: " + id);
    try
    {
      RegionField instance = entityManager.find(RegionField.class, id);
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
