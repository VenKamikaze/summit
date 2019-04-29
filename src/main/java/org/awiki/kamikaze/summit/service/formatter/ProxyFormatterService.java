package org.awiki.kamikaze.summit.service.formatter;

import org.awiki.kamikaze.summit.dto.render.PageItem;


public interface ProxyFormatterService {
  public FormatterService<PageItem<?>> getFormatterService(String sourceType);
}
