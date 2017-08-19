package org.awiki.kamikaze.summit.service.processor;


public interface ProxySourceProcessorService {
  public ReportSourceProcessorService getReportSourceProcessorService(String sourceType);
  public SingularSourceProcessorService getSingularSourceProcessorService(String sourceType);
}
