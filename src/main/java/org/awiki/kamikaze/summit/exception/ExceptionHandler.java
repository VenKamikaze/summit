package org.awiki.kamikaze.summit.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler
{
  private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

  /* Request attribute where {@link LogException} is stored. */
  public static final String EXCEPTION_ATTR = "exceptionStackTrace";

  /* Request attribute indicating if stack traces should be shown. */
  public static final String SHOW_STACK_TRACE = "showStackTraces";

  private boolean showStackTraces = false;

  public void setShowStackTraces(boolean showStackTraces)
  {
    this.showStackTraces = showStackTraces;
  }

  public void exceptionLogging(HttpServletRequest request, Exception ex, String message)
  {
    String stackTrace = getFullStackTrace(ex);
    if (!message.isEmpty())
    {
      stackTrace = message + "\n" + stackTrace;
    }
    logger.error("Exception encountered: " + message + ". stacktrace to follow");
    logger.error(stackTrace);
    request.setAttribute(EXCEPTION_ATTR, stackTrace);
    request.setAttribute(SHOW_STACK_TRACE, showStackTraces);
  }

  private static String getFullStackTrace(Exception ex)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    ex.printStackTrace(pw);
    if (ex instanceof ServletException)
    {
      pw.write("Root Cause:");
      ((ServletException) ex).getRootCause().printStackTrace(pw);
    }

    return sw.toString();
  }

}
