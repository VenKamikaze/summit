package org.awiki.kamikaze.summit.service.formatter;

import java.util.List;

public interface FormatterService<T>
{
  /**
   * Gets a list of java classnames the FormatterService is responsible for handling.
   * Please ensure a 1-1 relationship between java class and FormatterService.
   * @return List<String> of java classnames this formatter is responsible for processing.
   */
  public List<String> getResponsibilities();
  
  /**
   * 
   * @param builder
   * @param item
   * @return the StringBuilder object passed into this function.
   */
  public StringBuilder format(final StringBuilder builder, final T item, int insertAt);
}
