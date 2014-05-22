package org.epicgamer.kamikaze.summit.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.epicgamer.kamikaze.summit.bean.Page;
 
/**
 * Handles requests related to PAGES.
 */
@Controller
public class PagesController
{
  @PersistenceContext(type=PersistenceContextType.TRANSACTION)
  private EntityManager em;
  
  private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
  
  /**
   * Simply selects the home view to render by returning its name.
   */
  @RequestMapping(value = "/list/pages", method = RequestMethod.GET)
  public String list(Locale locale, @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      Model model) {
    logger.info("Hit /list/pages");
  
    Date date = new Date();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
    
    String formattedDate = dateFormat.format(date);
    
    model.addAttribute("serverTime", formattedDate );
    model.addAttribute("pages", doPagesQuery());
     
    return "home";
  }
  
  private List<Page> doPagesQuery()
  {
    String sql = " SELECT distinct p FROM Pages p ";
    List<Page> pages = null;
    
    try
    {
      TypedQuery<Page> query;
      query = em.createQuery(sql, Page.class);
  
        //what should happen for null returned
        //query.setParameter("agenciesVisible", CommonFunctions.getLoggedInUser().getAgenciesWithAgencyAdminRole());
        
        //query.setParameter("agencyrole", Roles.findRoles(Roles.AGENCY_ADMIN_ROLE)); // our role must be agency admin
      
      pages = query.getResultList();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return pages;
  }
}
