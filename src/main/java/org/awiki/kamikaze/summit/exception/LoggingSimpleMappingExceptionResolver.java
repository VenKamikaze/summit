package org.awiki.kamikaze.summit.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class LoggingSimpleMappingExceptionResolver extends
		SimpleMappingExceptionResolver {
  private ExceptionHandler exceptionHandler;
  private String message;

  @Autowired
  public void setExceptionHelper(ExceptionHandler exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
  }

  @Autowired
  public void setMessage(String message)
  {
    this.message = message;
  }

  @Override
  protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
          Exception ex)
  {
	exceptionHandler.recordException(request, ex, message);
    return super.doResolveException(request, response, handler, ex);
  }

}
