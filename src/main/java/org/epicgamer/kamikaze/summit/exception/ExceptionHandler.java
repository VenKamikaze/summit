package org.epicgamer.kamikaze.summit.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

//import org.springframework.security.core.context.SecurityContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler
{
  private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

  /* Request attribute where {@link LogException} is stored. */
  public static final String LOGGED_EXCEPTION = "loggedException";

  /* Request attribute indicating if stack traces should be shown. */
  public static final String SHOW_STACK_TRACE = "showStackTraces";

  private boolean showStackTraces = false;

  public void setShowStackTraces(boolean showStackTraces)
  {
    this.showStackTraces = showStackTraces;
  }
  
  /*
   * Stores exceptions to db and the logs, and also puts in request scope for use in the view.
   * 
   * @param request
   *          request being processed
   * @param ex
   *          exception to store
   * @param message
   *          optional message to store with the exception; eg indicating what was happening when the exception was
   *          caught
   */
  public void recordException(HttpServletRequest request, Exception ex, String message)
  {
    String stackTrace = getFullStackTrace(ex);
    if (!message.isEmpty())
    {
      stackTrace = message + "\n" + stackTrace;
    }

    //Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //logger.error("User:" +user.getName() +" hit exception: "+ message +". stacktrace is");
    logger.error(stackTrace);
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
