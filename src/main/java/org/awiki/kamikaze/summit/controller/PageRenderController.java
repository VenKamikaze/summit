package org.awiki.kamikaze.summit.controller;

import java.util.List;

import org.awiki.kamikaze.summit.service.PageRenderingService;
import org.awiki.kamikaze.summit.service.PageSubmitResult;
import org.awiki.kamikaze.summit.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles requests for the application home page.
 */
@Controller
public class PageRenderController {

  /** Flash attribute carrying process success messages across the post-POST redirect. */
  public static final String FLASH_SUCCESS_MESSAGES = "summitSuccessMessages";

  private static final Logger logger = LoggerFactory.getLogger(PageRenderController.class);

  private PageRenderingService renderService;

  @Autowired
  public void setRenderService(PageRenderingService renderService) {
    this.renderService = renderService;
  }

  /**
   * This renders the application pages to a String
   */
  @RequestMapping(value = "/run/{applicationId}/{pageId}", method = RequestMethod.GET)
  public ResponseEntity<String> view(@PathVariable String applicationId, @PathVariable String pageId,
          @RequestParam(required=false,name="pageParams") final String pageParams,
          @RequestParam final MultiValueMap<String, String> requestParams,
          final Model model) {
    logger.info("Hit page /run/" + applicationId + "/" + pageId);
    final MultiValueMap<String, String> parameterMap = StringUtils.toParameterMap(pageParams);
    // Also accept plain query parameters as page parameters (pageParams format wins on
    // duplicate keys). The redirect after a form POST carries the submitted form data as
    // plain query parameters, so without this any page whose render processing uses bind
    // variables fails after a submit.
    requestParams.forEach((key, values) -> {
      if(!"pageParams".equals(key) && !parameterMap.containsKey(key)) {
        parameterMap.put(key, values);
      }
    });
    // Process success messages flashed across the redirect from a POST; shown once.
    @SuppressWarnings("unchecked")
    final List<String> successMessages = (List<String>) model.asMap().get(FLASH_SUCCESS_MESSAGES);
    final String page = successMessages == null
            ? renderService.renderPageToString(Long.parseLong(applicationId), Long.parseLong(pageId), parameterMap)
            : renderService.renderPageToString(Long.parseLong(applicationId), Long.parseLong(pageId), parameterMap, successMessages, false);
    return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(page);
  }

  /**
  * This processes submitted form values on a page: validations first (a failure
  * re-renders the page inline with the error messages and the submitted values),
  * then any associated post processing, then a redirect to the branch target
  * (or back to the current page) carrying the process success messages as
  * flash attributes.
  * Note: handle file uploads through a separate method, and use plupload to submit async (or something similar)
  */
 @RequestMapping(value = "/run/{applicationId}/{pageId}", method = RequestMethod.POST)
 public Object process(@PathVariable String applicationId, @PathVariable String pageId,
         @RequestParam(required=false) MultiValueMap<String, String> formData,
         final RedirectAttributes redirectAttributes) {
   logger.info("Hit POST on page /run/" + applicationId + "/" + pageId);

   final PageSubmitResult result = renderService.processPageOnSubmit(Long.parseLong(applicationId), Long.parseLong(pageId), formData);

   if(result.hasValidationErrors()) {
     // Re-render this page directly (no redirect): the submitted values are the
     // parameter map, so the fields keep what the user typed, and the errors
     // render in the notification area.
     final String page = renderService.renderPageToString(Long.parseLong(applicationId), Long.parseLong(pageId),
             formData, result.getValidationErrors(), true);
     return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(page);
   }

   if(!result.getSuccessMessages().isEmpty()) {
     redirectAttributes.addFlashAttribute(FLASH_SUCCESS_MESSAGES, result.getSuccessMessages());
   }

   if(result.getBranchTarget() == null) {
     // If we have no branch target, redirect back to this page including the specified form data.
     redirectAttributes.addAllAttributes(formData);
     return "redirect:/run/" + applicationId + "/" + pageId;
   }
   // Branch targets are context-relative paths with their parameters already substituted
   // into the URL by the branch processing, so no redirect attributes are added here.
   return "redirect:" + result.getBranchTarget();
 }

}
