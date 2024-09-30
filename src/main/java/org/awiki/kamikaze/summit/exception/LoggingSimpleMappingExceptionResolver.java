package org.awiki.kamikaze.summit.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class LoggingSimpleMappingExceptionResolver extends  SimpleMappingExceptionResolver
{
  private ExceptionHandler exceptionHandler;
  private String message;
  private boolean showStackTraces;

  @Autowired
  public void setExceptionHelper(ExceptionHandler exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public void setShowStackTraces(String showTraces)
  {
    this.showStackTraces = BooleanUtils.toBoolean(showTraces);
  }

  @Override
  protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
          Exception ex)
  {
    exceptionHandler.setShowStackTraces(showStackTraces);
    exceptionHandler.exceptionLogging(request, ex, message);
    return super.doResolveException(request, response, handler, ex);
  }

}
