package org.awiki.kamikaze.summit.service.formatter;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.crypto.NoSuchMechanismException;

import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProxyFormatterServiceImpl implements ProxyFormatterService {

  private Logger log = LoggerFactory.getLogger(ProxyFormatterServiceImpl.class);
  
  @Autowired
  List<FormatterService<PageItem<?>>> formatterServices; // TODO: review, may not make sense to split these
  
  HashMap<String, FormatterService<PageItem<?>>> formatterServiceCache;

  @PostConstruct
  private void initializeCache()
  {
    formatterServiceCache = new HashMap<>(formatterServices.size());
    for(FormatterService<PageItem<?>> service : formatterServices)
    {
      for(String responsibility : service.getResponsibilities()){
        if (formatterServiceCache.containsKey(responsibility))
          log.warn("Found " + responsibility + " in service cache already. It is served by: " + formatterServiceCache.get(responsibility).getClass().getCanonicalName() + ". Overriding with: " + service.getClass().getCanonicalName());
        formatterServiceCache.put(responsibility, service);
      }
    }
  }
  
  public FormatterService<PageItem<?>> getFormatterService(final String canonicalClassNameToFormat) {
    if (formatterServiceCache.get(canonicalClassNameToFormat) == null)
      throw new NoSuchMechanismException("No service found for formatter type=" + canonicalClassNameToFormat);

    return formatterServiceCache.get(canonicalClassNameToFormat);
  }
}
