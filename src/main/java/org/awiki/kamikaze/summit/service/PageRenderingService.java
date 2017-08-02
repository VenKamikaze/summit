package org.awiki.kamikaze.summit.service;

import java.util.Map;


public interface PageRenderingService {
  
  public String renderPageToString(long applicationId, long pageId, Map<String, String> parameterMap);
  
}
