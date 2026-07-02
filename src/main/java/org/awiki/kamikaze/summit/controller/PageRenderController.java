package org.awiki.kamikaze.summit.controller;

import org.awiki.kamikaze.summit.service.PageRenderingService;
import org.awiki.kamikaze.summit.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles requests for the application home page.
 */
@Controller
public class PageRenderController {
	
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
  @ResponseBody
  public String view(@PathVariable String applicationId, @PathVariable String pageId,
          @RequestParam(required=false,name="pageParams") final String pageParams,
          @RequestParam final MultiValueMap<String, String> requestParams) {
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
    return renderService.renderPageToString(Long.parseLong(applicationId), Long.parseLong(pageId), parameterMap);
  }
  
  /**
  * This processes submitted form values on a page and runs any associated post processing functions
  *   before re-rendering the current page (or possibly re-directing to another page)
  * Note: handle file uploads through a separate method, and use plupload to submit async (or something similar)
  */
 @RequestMapping(value = "/run/{applicationId}/{pageId}", method = RequestMethod.POST)
 public String process(@PathVariable String applicationId, @PathVariable String pageId,
         @RequestParam(required=false) MultiValueMap<String, String> formData,
         final RedirectAttributes redirectAttributes) {
   logger.info("Hit POST on page /run/" + applicationId + "/" + pageId);

   final String branchTo = renderService.processPageOnSubmit(Long.parseLong(applicationId), Long.parseLong(pageId), formData);
   if(branchTo == null) {
     // If we have no branch target, redirect back to this page including the specified form data.
     redirectAttributes.addAllAttributes(formData);
     return "redirect:/run/" + applicationId + "/" + pageId;
   }
   return branchTo; // TODO need attributes included.
 }
  
}
