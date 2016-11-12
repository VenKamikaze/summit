package org.awiki.kamikaze.summit.service.formatter;


public interface ProxyFormatterService {
  public FormatterService<?> getFormatterService(String sourceType);
}
