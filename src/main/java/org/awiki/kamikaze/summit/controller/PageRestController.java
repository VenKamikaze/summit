package org.awiki.kamikaze.summit.controller;

import org.awiki.kamikaze.summit.service.PageRenderingService;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class PageRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(PageRestController.class);

	@Autowired
	PageRenderingService renderService;

  /**
   * Checks all columns with an OR clause to find results.
   */
  @RequestMapping(value = "/api/filter/{applicationId}/{pageId}", method = RequestMethod.POST)
  @ResponseBody
  public String filter(@PathVariable String applicationId, @PathVariable String pageId) {
    /*
     * 1) Store cached column names per page
     * 2) * When we have a front-end for building queries, clear out the column names on query update
     * 3) Allow searching on these column names
     * 4) Wrap query statement in an outer query, and search on individual columns, use setMaxResults(x) to limit results
     * 5) Return result as JSON, up to client to parse and use.
     */
    
    logger.info("Hit page /api/filter/" + applicationId + "/" + pageId);
    throw new NotYetImplementedException("filter");
  }

	
	 /**
	  * Checks specified columnName with a LIKE '%' + term + '%' clause to find results.
   */
  @RequestMapping(value = "/api/filter/{applicationId}/{pageId}/{columnName}", method = RequestMethod.POST)
  @ResponseBody
  public String filterColumn(@PathVariable String applicationId, @PathVariable String pageId, @PathVariable String columnName) {
    /*
     * 1) Store cached column names per page
     * 2) * When we have a front-end for building queries, clear out the column names on query update
     * 3) Allow searching on these column names
     * 4) Wrap query statement in an outer query, and search on individual columns, use setMaxResults(x) to limit results
     * 5) Return result as JSON, up to client to parse and use.
     */
    
    logger.info("Hit page /api/filter/" + applicationId + "/" + pageId + "/" + columnName);
    throw new NotYetImplementedException("filterColumn");
  }
  
}
