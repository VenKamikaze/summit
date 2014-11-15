package org.awiki.kamikaze.summit.controller;

import org.awiki.kamikaze.summit.service.PageRenderingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ControlPackController {
	
	private static final Logger logger = LoggerFactory.getLogger(ControlPackController.class);

	@Autowired
	PageRenderingService renderService;

	
	 /**
   * This renders the application pages to a String
   */
  @RequestMapping(value = "/application/${applicationId}/${pageId}", method = RequestMethod.GET)
  public String view(@PathVariable String applicationId, @PathVariable String pageId) {
    logger.info("Hit page /application/" + applicationId + "/" + pageId);
    return renderService.renderPageAsString(Long.parseLong(applicationId), Long.parseLong(pageId));
  }
  
}
