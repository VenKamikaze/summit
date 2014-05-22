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
 * Home object for domain model class CodeProcessingSrcType.
 * @see .CodeProcessingSrcType
 * @author Hibernate Tools
 */
public class CodeProcessingSrcTypeHome
{

  private static final Log     log            = LogFactory
                                                  .getLog(CodeProcessingSrcTypeHome.class);

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

  public void persist(CodeProcessingSrcType transientInstance)
  {
    log.debug("persisting CodeProcessingSrcType instance");
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

  public void attachDirty(CodeProcessingSrcType instance)
  {
    log.debug("attaching dirty CodeProcessingSrcType instance");
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

  public void attachClean(CodeProcessingSrcType instance)
  {
    log.debug("attaching clean CodeProcessingSrcType instance");
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

  public void delete(CodeProcessingSrcType persistentInstance)
  {
    log.debug("deleting CodeProcessingSrcType instance");
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

  public CodeProcessingSrcType merge(CodeProcessingSrcType detachedInstance)
  {
    log.debug("merging CodeProcessingSrcType instance");
    try
    {
      CodeProcessingSrcType result = (CodeProcessingSrcType) sessionFactory
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

  public CodeProcessingSrcType findById(java.lang.String id)
  {
    log.debug("getting CodeProcessingSrcType instance with id: " + id);
    try
    {
      CodeProcessingSrcType instance = (CodeProcessingSrcType) sessionFactory
          .getCurrentSession().get("CodeProcessingSrcType", id);
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

  public List findByExample(CodeProcessingSrcType instance)
  {
    log.debug("finding CodeProcessingSrcType instance by example");
    try
    {
      List results = sessionFactory.getCurrentSession()
          .createCriteria("CodeProcessingSrcType")
          .add(Example.create(instance)).list();
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
