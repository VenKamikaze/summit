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
 * Home object for domain model class CodeFieldType.
 * @see .CodeFieldType
 * @author Hibernate Tools
 */
public class CodeFieldTypeHome
{

  private static final Log     log            = LogFactory
                                                  .getLog(CodeFieldTypeHome.class);

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

  public void persist(CodeFieldType transientInstance)
  {
    log.debug("persisting CodeFieldType instance");
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

  public void attachDirty(CodeFieldType instance)
  {
    log.debug("attaching dirty CodeFieldType instance");
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

  public void attachClean(CodeFieldType instance)
  {
    log.debug("attaching clean CodeFieldType instance");
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

  public void delete(CodeFieldType persistentInstance)
  {
    log.debug("deleting CodeFieldType instance");
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

  public CodeFieldType merge(CodeFieldType detachedInstance)
  {
    log.debug("merging CodeFieldType instance");
    try
    {
      CodeFieldType result = (CodeFieldType) sessionFactory.getCurrentSession()
          .merge(detachedInstance);
      log.debug("merge successful");
      return result;
    }
    catch (RuntimeException re)
    {
      log.error("merge failed", re);
      throw re;
    }
  }

  public CodeFieldType findById(java.lang.String id)
  {
    log.debug("getting CodeFieldType instance with id: " + id);
    try
    {
      CodeFieldType instance = (CodeFieldType) sessionFactory
          .getCurrentSession().get("CodeFieldType", id);
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

  public List findByExample(CodeFieldType instance)
  {
    log.debug("finding CodeFieldType instance by example");
    try
    {
      List results = sessionFactory.getCurrentSession()
          .createCriteria("CodeFieldType").add(Example.create(instance)).list();
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
