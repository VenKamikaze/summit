package org.awiki.kamikaze.summit.service.edit;

import org.awiki.kamikaze.summit.dto.edit.EditPageDto;

public interface PageEditService {
  
  public EditPageDto loadPage(long applicationId, long pageId);
  
}
