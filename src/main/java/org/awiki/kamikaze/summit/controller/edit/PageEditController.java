package org.awiki.kamikaze.summit.controller.edit;

import org.awiki.kamikaze.summit.dto.edit.EditPageDto;
import org.awiki.kamikaze.summit.service.edit.PageEditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class PageEditController {
	
  private static final Logger logger = LoggerFactory.getLogger(PageEditController.class);
	
  private PageEditService pageEditService;

	
	/*
	 * page
     page_region
     region
     region_source
     source
	 */
	
	// TODO perhaps, for now, we should output a DTO and render a JSP page for the backend edit?
	// later we can look at making this generic, when we have edit forms rendered on the front-end to end-users.

  @Autowired	
	 public void setPageEditService(PageEditService pageEditService) {
    this.pageEditService = pageEditService;
  }



  /**
   * This loads and renders the edit page for modifying basic pages
   */
  @RequestMapping(value = "/edit/{applicationId}/{pageId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
  public String viewPage(@PathVariable String applicationId, @PathVariable String pageId, Model model) {
    logger.info("Hit page /edit/" + applicationId + "/" + pageId);
    final EditPageDto dto = pageEditService.loadPage(Long.parseLong(applicationId), Long.parseLong(pageId));
    model.addAttribute("dto", dto);
    return "/edit/page";
  }
  
}
