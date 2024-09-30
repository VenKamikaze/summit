package org.awiki.kamikaze.summit.service.formatter;

import java.util.HashMap;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ProxyFormatterServiceImpl implements ProxyFormatterService {

  private Logger log = LoggerFactory.getLogger(ProxyFormatterServiceImpl.class);
  
  private List<FormatterService<PageItem<?>>> formatterServices; // TODO: review, may not make sense to split these
  
  HashMap<String, FormatterService<PageItem<?>>> formatterServiceCache;

//  @Autowired
//  public ProxyFormatterServiceImpl(List<FormatterService<PageItem<?>>> formatterServices)
//  {
//    this.formatterServices = formatterServices;
//  }
  
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
  
  @Autowired
  public void setFormatterServices(@Lazy List<FormatterService<PageItem<?>>> formatterServices)
  {
    this.formatterServices = formatterServices;
  }
  
  public FormatterService<PageItem<?>> getFormatterService(final String canonicalClassNameToFormat) {
    if (! formatterServiceCache.containsKey(canonicalClassNameToFormat))
      throw new UnsupportedOperationException("No service found for formatter type=" + canonicalClassNameToFormat);

    return formatterServiceCache.get(canonicalClassNameToFormat);
  }
}
