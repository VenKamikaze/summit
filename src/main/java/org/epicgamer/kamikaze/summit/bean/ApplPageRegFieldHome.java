// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Home object for domain model class ApplPageRegField.
 * @see .ApplPageRegField
 * @author Hibernate Tools
 */
@Stateless
public class ApplPageRegFieldHome
{

  private static final Log log = LogFactory.getLog(ApplPageRegFieldHome.class);

  @PersistenceContext
  private EntityManager    entityManager;

  public void persist(ApplPageRegField transientInstance)
  {
    log.debug("persisting ApplPageRegField instance");
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

  public void remove(ApplPageRegField persistentInstance)
  {
    log.debug("removing ApplPageRegField instance");
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

  public ApplPageRegField merge(ApplPageRegField detachedInstance)
  {
    log.debug("merging ApplPageRegField instance");
    try
    {
      ApplPageRegField result = entityManager.merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public ApplPageRegField findById(ApplPageRegFieldId id)
  {
    log.debug("getting ApplPageRegField instance with id: " + id);
    try
    {
      ApplPageRegField instance = entityManager
          .find(ApplPageRegField.class, id);
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
