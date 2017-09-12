package org.awiki.kamikaze.summit.service;

import org.springframework.util.MultiValueMap;


public interface PageRenderingService {
  
  public String renderPageToString(long applicationId, long pageId, final MultiValueMap<String, String> parameterMap);
  
  public String processPageOnSubmit(long applicationId, long pageId, final MultiValueMap<String, String> submittedFormParams);
  
}
