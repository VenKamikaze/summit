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
 * Home object for domain model class CodeFieldSourceType.
 * @see .CodeFieldSourceType
 * @author Hibernate Tools
 */
public class CodeFieldSourceTypeHome
{

  private static final Log     log            = LogFactory
                                                  .getLog(CodeFieldSourceTypeHome.class);

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

  public void persist(CodeFieldSourceType transientInstance)
  {
    log.debug("persisting CodeFieldSourceType instance");
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

  public void attachDirty(CodeFieldSourceType instance)
  {
    log.debug("attaching dirty CodeFieldSourceType instance");
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

  public void attachClean(CodeFieldSourceType instance)
  {
    log.debug("attaching clean CodeFieldSourceType instance");
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

  public void delete(CodeFieldSourceType persistentInstance)
  {
    log.debug("deleting CodeFieldSourceType instance");
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

  public CodeFieldSourceType merge(CodeFieldSourceType detachedInstance)
  {
    log.debug("merging CodeFieldSourceType instance");
    try
    {
      CodeFieldSourceType result = (CodeFieldSourceType) sessionFactory
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

  public CodeFieldSourceType findById(java.lang.String id)
  {
    log.debug("getting CodeFieldSourceType instance with id: " + id);
    try
    {
      CodeFieldSourceType instance = (CodeFieldSourceType) sessionFactory
          .getCurrentSession().get("CodeFieldSourceType", id);
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

  public List findByExample(CodeFieldSourceType instance)
  {
    log.debug("finding CodeFieldSourceType instance by example");
    try
    {
      List results = sessionFactory.getCurrentSession()
          .createCriteria("CodeFieldSourceType").add(Example.create(instance))
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
