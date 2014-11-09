package org.awiki.kamikaze.summit.service.processor;

import java.util.List;

import javax.xml.crypto.NoSuchMechanismException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProxySourceProcessorServiceImpl {

  @Autowired
  List<SourceProcessorService> sourceServices;
  
  public SourceProcessorService getSourceProcessorService(String sourceType) {
    for(SourceProcessorService service : sourceServices)
    {
      if (service.isResponsibleFor(sourceType))
        return service;
    }
    throw new NoSuchMechanismException("No service found for sourceType=" + sourceType);
  }
}
