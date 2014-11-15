package org.awiki.kamikaze.asthmacontrolpack.controller;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.awiki.kamikaze.asthmacontrolpack.dto.entry.ControlPackWrapperDto;
import org.awiki.kamikaze.asthmacontrolpack.dto.validator.ControlPackWrapperValidator;
import org.awiki.kamikaze.asthmacontrolpack.repository.AgeGroupRepository;
import org.awiki.kamikaze.asthmacontrolpack.repository.CountryRepository;
import org.awiki.kamikaze.asthmacontrolpack.repository.StateRepository;
import org.awiki.kamikaze.asthmacontrolpack.repository.SuburbRepository;
import org.awiki.kamikaze.asthmacontrolpack.service.ControlPackService;
import org.awiki.kamikaze.asthmacontrolpack.util.AgeGroup;
import org.awiki.kamikaze.asthmacontrolpack.util.Country;
import org.awiki.kamikaze.asthmacontrolpack.util.Gender;
import org.awiki.kamikaze.asthmacontrolpack.util.State;
import org.awiki.kamikaze.asthmacontrolpack.util.Suburb;
import org.awiki.kamikaze.asthmacontrolpack.util.YesNoEnum;
import org.jadira.usertype.spi.utils.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ControlPackController {
	
	private static final Logger logger = LoggerFactory.getLogger(ControlPackController.class);
	private static final String ACTION_DELETE = "Delete";
	private static final String ACTION_CLEAR  = "Clear";

	@Service
	
	
	
   
  @InitBinder
  private void initBinder(WebDataBinder binder) {
      binder.setValidator(wrapperValidator);
  }
	
	private void addDropDowns(Model model)
	{
    model.addAttribute("optYesNo", YesNoEnum.getYesNoMap());
    model.addAttribute("optGender", Gender.getGenderMap());
    model.addAttribute("optCountry", Country.getCountryMap(countryRepo.findAll(new Sort(new Order("sortOrder")))));
    model.addAttribute("optState", State.getStateMap(stateRepo.findAllOrderBySortOrder()) );
    model.addAttribute("optAgeGroup", AgeGroup.getAgeGroupMap(ageGroupRepo.findAllOrderBySortOrder()) );

    Map<String,String> suburbMap = Suburb.getMap(suburbRepo.findDistinct());
    if (model.containsAttribute("wrapperDto"))
    {
      ControlPackWrapperDto wrapperDto = (ControlPackWrapperDto) model.asMap().get("wrapperDto");
      if (wrapperDto.getAddress() != null
          && StringUtils.isNotEmpty(wrapperDto.getAddress().getSuburb()) )
      {
        suburbMap.put(wrapperDto.getAddress().getSuburb(), wrapperDto.getAddress().getSuburb());
      }
    }
    model.addAttribute("optSuburb", suburbMap);
	}
	
  /**
   * Maps /list request path to "list" tile definition
   */
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String list(Locale locale, Model model) {
    return listPage(locale, 1, model);
  }
	
  @RequestMapping(value = "/list/{pageNumber}", method = RequestMethod.GET)
  public String listPage(Locale locale, @PathVariable Integer pageNumber, Model model) {
    logger.info("Hit page /list/" + pageNumber);
    Page<ControlPackWrapperDto> page = controlPackService.getControlPackDtoPage(pageNumber);
    int current = pageNumber;

    Date date = new Date();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
    
    String formattedDate = dateFormat.format(date);
    
    model.addAttribute("serverTime", formattedDate ); 
    model.addAttribute("headings", new ArrayList<String>(Arrays.asList("Full name", "Suburb", "Date Requested", "Wants follow up?"))); 
    model.addAttribute("items", page.getContent());
    model.addAttribute("pageNum", current);      // TODO
    model.addAttribute("pageTotalNum", (page.getTotalPages()) ); // TODO


    return "list";
  }

  @RequestMapping(value = "/list/search", method = RequestMethod.POST)
  public String list(Locale locale, Model model, @RequestParam String action, @RequestParam String search) {
    logger.info("Hit page /list/search");
    
    if (ACTION_CLEAR.equals(action))
    {
      return "redirect:/list"; // clear out the search
    }
    
    Integer pageNumber = 1;
    Page<ControlPackWrapperDto> page = controlPackService.getSearchControlPackDtoPage(pageNumber, search);
    int current = pageNumber;

    Date date = new Date();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
    
    String formattedDate = dateFormat.format(date);
    
    model.addAttribute("serverTime", formattedDate ); 
    model.addAttribute("headings", new ArrayList<String>(Arrays.asList("Full name", "Suburb", "Date Requested", "Wants follow up?"))); 
    model.addAttribute("items", page.getContent());
    model.addAttribute("pageNum", current);      // TODO
    model.addAttribute("pageTotalNum", (page.getTotalPages()) ); // TODO


    return "list";
  }
  
  
	/**
	 * Maps /entry request path to "entry" tile definition
	 */
	@RequestMapping(value = "/entry", method = RequestMethod.GET)
	public String entry(Locale locale, Model model) {
		logger.info("Hit page /entry");
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate ); 
		
		addDropDowns(model);
		
    if(!model.containsAttribute("wrapperDto"))
    {
  		ControlPackWrapperDto command = ControlPackWrapperDto.create();
  		command.getControlPack().setDateRequested(LocalDate.now());
  		model.addAttribute("wrapperDto", command);
    }

		return "entry";   // entry is the name of a tiles definition.
	}
	
	 /**
   * Maps /entry request path to "entry" tile definition
   */
  @RequestMapping(value = "/entry/{controlPackId}", method = RequestMethod.GET)
  public String entry(Locale locale, Model model, 
      @PathVariable String controlPackId) {
    logger.info("Hit page /entry/" + controlPackId);
    
    Date date = new Date();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
    
    String formattedDate = dateFormat.format(date);
    model.addAttribute("serverTime", formattedDate ); 

    addDropDowns(model);
    
    if(!model.containsAttribute("wrapperDto"))
    {
      ControlPackWrapperDto command = controlPackService.fetch(Integer.valueOf(controlPackId));
      model.addAttribute("wrapperDto", command);
    }
    
    return "entry";   // tiles definition.
  }
	
	 /**
   * Maps /entry request path to "entry" tile definition
   */
  @RequestMapping(value = "/entry/submit", method = RequestMethod.POST)
  public String save(@ModelAttribute @Valid ControlPackWrapperDto wrapperDto, 
      final BindingResult result, @RequestParam String action, final RedirectAttributes redirectAttributes) {

    if (ACTION_DELETE.equals(action))
    {
      return delete(wrapperDto);
    }
    
    logger.info("POST on /entry/submit - matched save");

    if (result.hasErrors())
    {
      redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.wrapperDto", result);
      redirectAttributes.addFlashAttribute("wrapperDto", wrapperDto);
      logger.debug("Found errors on submit!");
      return "redirect:/entry";
    }
    
    controlPackService.create(wrapperDto);

    return "redirect:/list";
  }

  /**
  * Maps /entry request path to "entry" tile definition
  */
 public String delete(ControlPackWrapperDto wrapperDto) 
 {
   logger.info("POST on /entry/submit - matched delete");

   controlPackService.delete(wrapperDto);
   
   return "redirect:/list";   // entry is the name of a tiles definition.
 }

  
}
