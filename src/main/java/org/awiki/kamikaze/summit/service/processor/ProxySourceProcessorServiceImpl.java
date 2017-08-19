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

  // TODO review: is this required at all?
  @Autowired
  List<BatchSourceProcessorService> batchSourceServices; // TODO: review, may not make sense to split these
  
  @Autowired
  List<ReportSourceProcessorService> reportSourceServices; // TODO: review, may not make sense to split these
  
  HashMap<String, SourceProcessorService> allSourceServiceCache;
  HashMap<String, SingularSourceProcessorService> singularSourceServiceCache;
  HashMap<String, ReportSourceProcessorService> reportSourceServiceCache;

  @PostConstruct
  private void initializeCache()
  {
    singularSourceServiceCache = new HashMap<>(singularSourceServices.size());
    reportSourceServiceCache   = new HashMap<>(reportSourceServices.size());
    allSourceServiceCache = new HashMap<>(singularSourceServices.size() + batchSourceServices.size() + reportSourceServices.size());
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
    
    for(ReportSourceProcessorService service : reportSourceServices)
    {
      for(String responsibility : service.getResponsibilities()){
        if (allSourceServiceCache.containsKey(responsibility))
          log.warn("Found " + responsibility + " in service cache already. It is served by: " + allSourceServiceCache.get(responsibility).getClass().getCanonicalName() + ". Overriding with: " + service.getClass().getCanonicalName());
        reportSourceServiceCache.put(responsibility, service);
        allSourceServiceCache.put(responsibility, service);
      }
    }
  }
  
  @Override
  public SingularSourceProcessorService getSingularSourceProcessorService(final String sourceType) {
    if (singularSourceServiceCache.get(sourceType) == null)
      throw new NoSuchMechanismException("No service found for sourceType=" + sourceType);

    return singularSourceServiceCache.get(sourceType);
  }
  
  @Override
  public ReportSourceProcessorService getReportSourceProcessorService(final String sourceType) {
    if (reportSourceServiceCache.get(sourceType) == null)
      throw new NoSuchMechanismException("No service found for sourceType=" + sourceType);

    return reportSourceServiceCache.get(sourceType);
  }
  
}
