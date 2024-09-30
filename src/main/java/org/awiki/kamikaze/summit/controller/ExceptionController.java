package org.awiki.kamikaze.summit.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ExceptionController {
	
  private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

  @RequestMapping(value = "/error/uncaughtException", method = RequestMethod.GET)
  public String test(Locale locale, Model model) {
    logger.info("Uncaught Exception!");
		
    return commonException(locale, model);
  }

  private String commonException(Locale locale, Model model)
  {
    Date date = new Date();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
    String formattedDate = dateFormat.format(date);
		
    model.addAttribute("serverTime", formattedDate );
		 
    return "error/uncaughtException";  // tiles definition.
  }
	
  @RequestMapping(value = "/error/resourceNotFound", method = RequestMethod.GET)
  public String resourceNotFound(HttpServletRequest request, Locale locale, Model model) {
    logger.info("Resource Not Found Exception! ");
    
    logger.info("RNFE, URL: "+request.getRequestURL().toString());
    
    return commonException(locale, model);
  }
}
