package org.awiki.kamikaze.summit.service;

import java.util.List;

import org.springframework.util.MultiValueMap;


public interface PageRenderingService {

  public String renderPageToString(long applicationId, long pageId, final MultiValueMap<String, String> parameterMap);

  /**
   * Render the page with notification messages substituted into the page
   * template's ##__NOTIFICATION__## placeholder (validation errors after a
   * failed POST, or flashed success messages after a redirect).
   * @param isError true renders the messages with the error style, false with the success style
   */
  public String renderPageToString(long applicationId, long pageId, final MultiValueMap<String, String> parameterMap,
          final List<String> notificationMessages, final boolean isError);

  public PageSubmitResult processPageOnSubmit(long applicationId, long pageId, final MultiValueMap<String, String> submittedFormParams);

}
