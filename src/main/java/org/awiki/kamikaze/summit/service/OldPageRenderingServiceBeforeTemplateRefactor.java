package org.awiki.kamikaze.summit.service;

import org.awiki.kamikaze.summit.dto.render.PageDto;

public interface OldPageRenderingServiceBeforeTemplateRefactor {
  
  public PageDto renderPage(long applicationPageId);
  public PageDto renderPage(long applicationId, long pageId);
  public String renderPageAsString(long applicationId, long pageId);
  
}
