package org.epicgamer.kamikaze.summit.bean.codetable;
// default package
// Generated Sep 14, 2013 6:38:07 PM by Hibernate Tools 3.4.0.CR1

// Generated Sep 14, 2013 6:38:07 PM by Hibernate Tools 3.4.0.CR1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class CodeRegionType.
 * @see .CodeRegionType
 * @author Hibernate Tools
 */
public class CodeRegionTypeHome
{

  private static final Log     log            = LogFactory
                                                  .getLog(CodeRegionTypeHome.class);

  private final SessionFactory sessionFactory = getSessionFactory();

  protected SessionFactory getSessionFactory()
  {
    try
    {
      return (SessionFactory) new InitialContext().lookup("SessionFactory");
    }
    catch (Exception e)
    {
      log.error("Could not locate SessionFactory in JNDI", e);
      throw new IllegalStateException("Could not locate SessionFactory in JNDI");
    }
  }

  public void persist(CodeRegionType transientInstance)
  {
    log.debug("persisting CodeRegionType instance");
    try
    {
      sessionFactory.getCurrentSession().persist(transientInstance);
      log.debug("persist successful");
    }
    catch (RuntimeException re)
    {
      log.error("persist failed", re);
      throw re;
    }
  }

  public void attachDirty(CodeRegionType instance)
  {
    log.debug("attaching dirty CodeRegionType instance");
    try
    {
      sessionFactory.getCurrentSession().saveOrUpdate(instance);
      log.debug("attach successful");
    }
    catch (RuntimeException re)
    {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void attachClean(CodeRegionType instance)
  {
    log.debug("attaching clean CodeRegionType instance");
    try
    {
      sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
      log.debug("attach successful");
    }
    catch (RuntimeException re)
    {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void delete(CodeRegionType persistentInstance)
  {
    log.debug("deleting CodeRegionType instance");
    try
    {
      sessionFactory.getCurrentSession().delete(persistentInstance);
      log.debug("delete successful");
    }
    catch (RuntimeException re)
    {
      log.error("delete failed", re);
      throw re;
    }
  }

  public CodeRegionType merge(CodeRegionType detachedInstance)
  {
    log.debug("merging CodeRegionType instance");
    try
    {
      CodeRegionType result = (CodeRegionType) sessionFactory
          .getCurrentSession().merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public CodeRegionType findById(java.lang.String id)
  {
    log.debug("getting CodeRegionType instance with id: " + id);
    try
    {
      CodeRegionType instance = (CodeRegionType) sessionFactory
          .getCurrentSession().get("CodeRegionType", id);
      if (instance == null)
      {
        log.debug("get successful, no instance found");
      }
      else
      {
        log.debug("get successful, instance found");
      }
      return instance;
    }
    catch (RuntimeException re)
    {
      log.error("get failed", re);
      throw re;
    }
  }

  public List findByExample(CodeRegionType instance)
  {
    log.debug("finding CodeRegionType instance by example");
    try
    {
      List results = sessionFactory.getCurrentSession()
          .createCriteria("CodeRegionType").add(Example.create(instance))
          .list();
      log.debug("find by example successful, result size: " + results.size());
      return results;
    }
    catch (RuntimeException re)
    {
      log.error("find by example failed", re);
      throw re;
    }
  }
}
