package org.epicgamer.kamikaze.summit.exception;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ExceptionInterceptor extends HandlerInterceptorAdapter
{
  private ExceptionHandler exceptionHandler;
  private String message;

  @Autowired
  public void setExceptionHelper(ExceptionHandler exceptionHelper)
  {
    this.exceptionHandler = exceptionHelper;
  }

  @Autowired
  public void setMessage(String message)
  {
    this.message = message;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
          throws Exception
  {
    if (ex != null)
    {
      exceptionHandler.recordException(request, ex, message);
    }
  }
}
