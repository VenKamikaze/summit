package org.awiki.kamikaze.summit.controller;

import org.awiki.kamikaze.summit.dto.entry.PageItem;
import org.awiki.kamikaze.summit.service.PageFilteringService;
import org.awiki.kamikaze.summit.service.formatter.FormatterService;
import org.awiki.kamikaze.summit.service.formatter.ProxyFormatterService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for the application home page.
 */
@RestController
public class PageRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(PageRestController.class);

	@Autowired
	private PageFilteringService filterService;
	
  @Autowired
  private ProxyFormatterService sourceFormatters;
	
  /**
   * Checks all columns with an OR clause to find results.
   */
  @RequestMapping(value = "/api/filter/{regionId}", method = { RequestMethod.GET, RequestMethod.POST })
  public SourceProcessorResultTable filterRegion(@PathVariable String regionId, 
          @RequestParam(required=false,name="filterType") final String filterType,
          @RequestParam(required=false,name="search") final String searchValue,
          @RequestParam(required=false,name="page") final String pageNumber,
          @RequestParam(required=false,name="rows") final String rowsPerPage) {
    /*
     * 1) Store cached column names per page
     * 2) * When we have a front-end for building queries, clear out the column names on query update
     * 3) Allow searching on these column names
     * 4) Wrap query statement in an outer query, and search on individual columns, use setMaxResults(x) to limit results
     * 5) Return result as JSON, up to client to parse and use.
     */
    
    logger.info("Hit page /api/filter/" + regionId);
    final PageItem<String> filteredResults = filterService.filterRegion(Long.parseLong(regionId), filterType, searchValue, 
            (pageNumber != null && ( rowsPerPage != null && Long.parseLong(rowsPerPage) != 0L ) ? Long.parseLong(pageNumber) : 0), 
            (rowsPerPage != null ? Long.parseLong(rowsPerPage) : 0),
            (rowsPerPage != null)
           );
    return (SourceProcessorResultTable)filteredResults;
  }
	
  /**
   * Checks all columns with an OR clause to find results.
   */
  //@ResponseBody
  @RequestMapping(value = "/api/filter/{applicationId}/{pageId}", method = { RequestMethod.GET, RequestMethod.POST })
  public SourceProcessorResultTable filter(@PathVariable String applicationId, @PathVariable String pageId,
          @RequestParam(required=false,name="filterType") final String filterType,
          @RequestParam(required=false,name="search") final String searchValue,
          @RequestParam(required=false,name="page") final String pageNumber,
          @RequestParam(required=false,name="rows") final String rowsPerPage) {
    /*
     * 1) Store cached column names per page
     * 2) * When we have a front-end for building queries, clear out the column names on query update
     * 3) Allow searching on these column names
     * 4) Wrap query statement in an outer query, and search on individual columns, use setMaxResults(x) to limit results
     * 5) Return result as JSON, up to client to parse and use.
     */
    
    logger.info("Hit page /api/filter/" + applicationId + "/" + pageId);
    
    final PageItem<String> filteredResults = filterService.filterPage(Long.parseLong(applicationId), Long.parseLong(pageId), filterType, searchValue,
            (pageNumber != null && ( rowsPerPage != null && Long.parseLong(rowsPerPage) != 0L ) ? Long.parseLong(pageNumber) : 0), 
            (rowsPerPage != null ? Long.parseLong(rowsPerPage) : 0),
            (rowsPerPage != null));
    return (SourceProcessorResultTable)filteredResults;
  }
	
	 /**
	  * Checks specified columnName with a LIKE '%' + term + '%' clause to find results.
   */
  @RequestMapping(value = "/api/filter/{applicationId}/{pageId}/{columnName}", method = RequestMethod.POST)
  public SourceProcessorResultTable filterColumn(@PathVariable String applicationId, @PathVariable String pageId, @PathVariable String columnName,
          @RequestParam(required=false,name="filterType") final String filterType,
          @RequestParam(required=false,name="search") final String searchValue,
          @RequestParam(required=false,name="page") final String pageNumber,
          @RequestParam(required=false,name="rows") final String rowsPerPage) {
    /*
     * 1) Store cached column names per page
     * 2) * When we have a front-end for building queries, clear out the column names on query update
     * 3) Allow searching on these column names
     * 4) Wrap query statement in an outer query, and search on individual columns, use setMaxResults(x) to limit results
     * 5) Return result as JSON, up to client to parse and use.
     */
    
    logger.info("Hit page /api/filter/" + applicationId + "/" + pageId + "/" + columnName);
    final PageItem<String> filteredResults = filterService.filterPageByColumn(Long.parseLong(applicationId), Long.parseLong(pageId), filterType, columnName, searchValue,
            (pageNumber != null && ( rowsPerPage != null && Long.parseLong(rowsPerPage) != 0L ) ? Long.parseLong(pageNumber) : 0), 
            (rowsPerPage != null ? Long.parseLong(rowsPerPage) : 0),
            (rowsPerPage != null));
    return (SourceProcessorResultTable)filteredResults;
  }
  
}
