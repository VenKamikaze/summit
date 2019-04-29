package org.awiki.kamikaze.summit.service.field.processor;


public interface ProxyFieldProcessorService
{
  public FieldProcessorService getFieldProcessorService(String fieldCanonicalClassName);
}
