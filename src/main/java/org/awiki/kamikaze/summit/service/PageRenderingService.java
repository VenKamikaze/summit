package org.awiki.kamikaze.summit.service;


public interface PageRenderingService {
  
  public String renderPageToString(long applicationId, long pageId);
  
}
