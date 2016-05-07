package org.awiki.kamikaze.summit.service.processor;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.crypto.NoSuchMechanismException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProxySourceProcessorServiceImpl implements ProxySourceProcessorService {

  private Logger log = LoggerFactory.getLogger(ProxySourceProcessorServiceImpl.class);
  
  @Autowired
  List<SingularSourceProcessorService> singularSourceServices; // TODO: review, may not make sense to split these

  @Autowired
  List<BatchSourceProcessorService> batchSourceServices; // TODO: review, may not make sense to split these
  
  HashMap<String, SourceProcessorService> sourceServiceCache;

  @PostConstruct
  private void initializeCache()
  {
    sourceServiceCache = new HashMap<>(singularSourceServices.size() + batchSourceServices.size());
    for(SingularSourceProcessorService service : singularSourceServices)
    {
      for(String responsibility : service.getResponsibilities()){
        if (sourceServiceCache.containsKey(responsibility))
          log.warn("Found " + responsibility + " in service cache already. It is served by: " + sourceServiceCache.get(responsibility).getClass().getCanonicalName() + ". Overriding with: " + service.getClass().getCanonicalName());
        sourceServiceCache.put(responsibility, service);
      }
    }
    
    for(BatchSourceProcessorService service : batchSourceServices)
    {
      for(String responsibility : service.getResponsibilities()){
        if (sourceServiceCache.containsKey(responsibility))
          log.warn("Found " + responsibility + " in service cache already. It is served by: " + sourceServiceCache.get(responsibility).getClass().getCanonicalName() + ". Overriding with: " + service.getClass().getCanonicalName());
        sourceServiceCache.put(responsibility, service);
      }
    }
  }
  
  // TODO: review, because we've split the singular and batch, we can only return the base type.
  // this makes execution of code difficult at lower levels
  public SourceProcessorService getSourceProcessorService(String sourceType) {
    if (sourceServiceCache.get(sourceType) == null)
      throw new NoSuchMechanismException("No service found for sourceType=" + sourceType);

    return sourceServiceCache.get(sourceType);
  }
}
