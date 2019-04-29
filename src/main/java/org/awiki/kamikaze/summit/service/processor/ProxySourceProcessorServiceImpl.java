package org.awiki.kamikaze.summit.service.processor;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProxySourceProcessorServiceImpl implements ProxySourceProcessorService {

  private Logger log = LoggerFactory.getLogger(ProxySourceProcessorServiceImpl.class);
  
  @Autowired
  List<SingularSourceProcessorService> singularSourceServices; // TODO: review, may not make sense to split these

  // TODO review: is this required at all?
  @Autowired
  List<BatchSourceProcessorService> batchSourceServices; // TODO: review, may not make sense to split these
  
  @Autowired
  List<TabularQuerySourceProcessorService> tabularSourceServices; // TODO: review, may not make sense to split these
  
  HashMap<String, SourceProcessorService> allSourceServiceCache;
  HashMap<String, SingularSourceProcessorService> singularSourceServiceCache;
  HashMap<String, TabularQuerySourceProcessorService> tabularSourceServiceCache;

  @PostConstruct
  private void initializeCache()
  {
    singularSourceServiceCache = new HashMap<>(singularSourceServices.size());
    tabularSourceServiceCache   = new HashMap<>(tabularSourceServices.size());
    allSourceServiceCache = new HashMap<>(singularSourceServices.size() + batchSourceServices.size() + tabularSourceServices.size());
    for(SingularSourceProcessorService service : singularSourceServices)
    {
      for(String responsibility : service.getResponsibilities()){
        if (allSourceServiceCache.containsKey(responsibility))
          log.warn("Found " + responsibility + " in service cache already. It is served by: " + allSourceServiceCache.get(responsibility).getClass().getCanonicalName() + ". Overriding with: " + service.getClass().getCanonicalName());
        singularSourceServiceCache.put(responsibility, service);
        allSourceServiceCache.put(responsibility, service);
      }
    }
    
    for(BatchSourceProcessorService service : batchSourceServices)
    {
      for(String responsibility : service.getResponsibilities()){
        if (allSourceServiceCache.containsKey(responsibility))
          log.warn("Found " + responsibility + " in service cache already. It is served by: " + allSourceServiceCache.get(responsibility).getClass().getCanonicalName() + ". Overriding with: " + service.getClass().getCanonicalName());
        // reportS.put(responsibility, service);
        allSourceServiceCache.put(responsibility, service);
      }
    }
    /*
    for(SourceProcessorService s : reportSourceServices) {
      for(String responsibility : s.getResponsibilities()) {
        log.info("m2s Found " + responsibility + " to service cache. Served by: " + s.getClass().getCanonicalName());
      }
    }
      */
    
    for(TabularQuerySourceProcessorService service : tabularSourceServices)
    {
      for(String responsibility : service.getResponsibilities()){
        if (allSourceServiceCache.containsKey(responsibility))
          log.warn("Found " + responsibility + " in service cache already. It is served by: " + allSourceServiceCache.get(responsibility).getClass().getCanonicalName() + ". Overriding with: " + service.getClass().getCanonicalName());
        tabularSourceServiceCache.put(responsibility, service);
        allSourceServiceCache.put(responsibility, service);
      }
    }
  }
  
  @Override
  public SingularSourceProcessorService getSingularSourceProcessorService(final String sourceType) {
    if (! singularSourceServiceCache.containsKey(sourceType))
      throw new UnsupportedOperationException("No service found for sourceType=" + sourceType);

    return singularSourceServiceCache.get(sourceType);
  }
  
  @Override
  public TabularQuerySourceProcessorService getTabularSourceProcessorService(final String sourceType) {
    if (! tabularSourceServiceCache.containsKey(sourceType))
      throw new UnsupportedOperationException("No service found for sourceType=" + sourceType);

    return tabularSourceServiceCache.get(sourceType);
  }
  
  @Override
  public ReportSourceProcessorService getReportSourceProcessorService(final String sourceType) {
    if (! tabularSourceServiceCache.containsKey(sourceType))
      throw new UnsupportedOperationException("No service found for sourceType=" + sourceType);
    else if (! (tabularSourceServiceCache.get(sourceType) instanceof ReportSourceProcessorService))
      throw new UnsupportedOperationException("No ReportSourceProcessor for sourceType=" + sourceType);

    return (ReportSourceProcessorService) tabularSourceServiceCache.get(sourceType);
  }
}
