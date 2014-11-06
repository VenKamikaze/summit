package org.awiki.kamikaze.summit.util;

import java.lang.reflect.Method;

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
            // do your thing with r
          }
      }
      
    } catch(Exception e)
    {
      logger.warn("DebugUtils: exception caught in debugging.", e);
    }
  }
  
}
