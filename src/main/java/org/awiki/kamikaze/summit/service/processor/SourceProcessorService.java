package org.awiki.kamikaze.summit.service.processor;

import java.util.List;

public interface SourceProcessorService
{
  //public boolean isResponsibleFor(final String sourceType);
  public List<String> getResponsibilities();
}
