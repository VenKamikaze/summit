package org.awiki.kamikaze.summit.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugUtils {

  private static final Logger logger = LoggerFactory.getLogger(DebugUtils.class);
  
  public static void debugObjectGetters(Object o)
  {
    try
    {
      if (o != null)
      {
        for (Method m : o.getClass().getMethods())
          if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
            final Object r = m.invoke(o);
            logger.info("DebugUtils: o.getClass()=" + o.getClass().getName());
            logger.info("DebugUtils: m.getName()=" + m.getName());
            if (r != null)
              logger.info("DebugUtils: r.toString()=" + r.toString());
            if(r instanceof Map<?,?>) {
              logger.info("DebugUtils: found map when calling "+ m.getName());
              Map<?,?> map = (Map<?,?>)r;
              if(map.keySet().size() > 0) {
                for(Object k : map.keySet()) {
                  logger.info("DebugUtils: mapkey= "+ k.toString());
                  logger.info("DebugUtils: mapval= "+ map.get(k).toString());
                }
              }
            }
            // do your thing with r
          }
      }
      
    } catch(Exception e)
    {
      logger.warn("DebugUtils: exception caught in debugging.", e);
    }
  }

  public static void traceObjectGetters(Object o)
  {
    try
    {
      if (o != null)
      {
        if (o instanceof Collection)
        {
          for(Object item : (Collection)o) 
          {
            logger.trace("DebugUtils: item.getClass()=" + item.getClass().getName());
            logger.trace("DebugUtils: item.toString()=" + item.toString());
          }
        }
        else {
          for (Method m : o.getClass().getMethods())
            if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
              final Object r = m.invoke(o);
              logger.trace("DebugUtils: o.getClass()=" + o.getClass().getName());
              logger.trace("DebugUtils: m.getName()=" + m.getName());
              if (r != null)
                logger.trace("DebugUtils: r.toString()=" + r.toString());
              if(r instanceof Map<?,?>) {
                logger.trace("DebugUtils: found map when calling "+ m.getName());
                Map<?,?> map = (Map<?,?>)r;
                if(map.keySet().size() > 0) {
                  for(Object k : map.keySet()) {
                    logger.trace("DebugUtils: mapkey= "+ k.toString());
                    logger.trace("DebugUtils: mapval= "+ map.get(k).toString());
                  }
                }
              }
              // do your thing with r
            }
        }
      }
      
    } catch(Exception e)
    {
      logger.warn("DebugUtils: exception caught in debugging.", e);
    }
  }

  
}
