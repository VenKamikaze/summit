package org.awiki.kamikaze.summit.service.field.processor;

import java.util.HashMap;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProxyFieldProcessorServiceImpl implements ProxyFieldProcessorService
{
  private Logger log = LoggerFactory.getLogger(ProxyFieldProcessorServiceImpl.class);

  private List<FieldProcessorService> fieldProcessorServices;
  
  private HashMap<String, FieldProcessorService> allFieldServicesCache;
  
  @Autowired
  public void setFieldProcessorServices(List<FieldProcessorService> fieldProcessorServices) {
    this.fieldProcessorServices = fieldProcessorServices;
  }

  @PostConstruct
  private void initializeCache()
  {
    allFieldServicesCache = new HashMap<>(fieldProcessorServices.size());
    for(FieldProcessorService service : fieldProcessorServices)
    {
      for(String responsibility : service.getResponsibilities()){
        if (allFieldServicesCache.containsKey(responsibility))
          log.warn("Found " + responsibility + " in field service cache already. It is served by: " + allFieldServicesCache.get(responsibility).getClass().getCanonicalName() + ". Overriding with: " + service.getClass().getCanonicalName());
        allFieldServicesCache.put(responsibility, service);
      }
    }
  }
  
  @Override
  public FieldProcessorService getFieldProcessorService(String fieldCanonicalClassName)
  {
    if(! allFieldServicesCache.containsKey(fieldCanonicalClassName))
      throw new UnsupportedOperationException("Field with class name: " + fieldCanonicalClassName + " has no field service capable of handling it!");
    
    return allFieldServicesCache.get(fieldCanonicalClassName);
  }

}
