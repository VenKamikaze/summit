package org.awiki.kamikaze.summit.service;

import static org.awiki.kamikaze.summit.domain.Region.REGION_TYPE_REPORT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.service.field.FieldService;
import org.awiki.kamikaze.summit.service.formatter.FormatterService;
import org.awiki.kamikaze.summit.service.formatter.ProxyFormatterService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.mapper.PageToPageDtoMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 * This is a top level service that co-ordinates the other sub-services to build up a full page.
 * @author msaun
 *
 */
@Service
public class PageRenderingServiceImpl implements PageRenderingService {
  
  public static final String REQUEST = "REQUEST"; // reserved bind variable for referencing submit actions in a POST.
  
  private static final Logger log = LoggerFactory.getLogger(PageRenderingServiceImpl.class);
  
  @Autowired
  private ApplicationPageRepository appPageStore;
  
  @Autowired
  private ProxySourceProcessorService sourceProcessors;
  
  @Autowired
  private ProxyFormatterService sourceFormatters;
  
  @Autowired
  private Mapper mapper;
  
  @Autowired
  private PageToPageDtoMapper pageMapper;
  
  @Autowired
  private BindVarService bindVarService;
  
  @Autowired
  private FieldService fieldService;
  
  @Autowired
  private PageProcessingService pageProcessingService;
  
  /**
   * Overarching method that handles the page rendering
   * This one renders the entire page and all it's contents to a String
   * 1. Maps Page domain and linked objects to a PageDto
   * 2. Calls processPageItems, which gathers the processed page items, and then calls the formatters
   * (in processRegionsForRender) 3. Processes either the region source (ie. report regions) or calls method to process all field content
   * (in processFieldsForRender) 4. Processes each field source into processed content, ordered by sort order
   * (back in processPageItems) 5. Takes the processed content, and calls the appropriate formatter
   * (formatters) 6. Each formatter gets the appropriate template, and builds up a text representation of the page
   *                 appending content at the start of each format call.
   */
  public String renderPageToString(long applicationId, long pageId, final MultiValueMap<String, String> parameterMap) {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    if(appPage != null)
    {
      PageDto pageDto = pageMapper.map(appPage.getPage());
      return renderPageItems(pageDto, parameterMap);
    }
    return "Application " + applicationId + ", page " + pageId + "  does not exist.";
  }
  

  /**
   * Gathers the collection of PageItems, then applies formatting to all of them.
   * Renders this to a string. 
   * @param pageDto
   * @return
   */
  private String renderPageItems(final PageDto pageDto, final MultiValueMap<String, String> parameterMap) {
    // Get the collection of pageItems in each region
    //Map<String, List<PageItem<?>>> regionPageItems = processRegionsForRender(pageDto);
    long start = System.nanoTime();
    Map<String, PageProcessingSourceSelectDto> fieldsToPopulate = processPageProcessingSource(pageDto.getPageRenderPreRegionPageProcessings(), parameterMap);
    long end = System.nanoTime();
    log.info(Thread.currentThread().getStackTrace()[1].getMethodName().toString() + ": processPageRenderSource took: " + (end - start) / 1000000 + "ms");
    
    start = System.nanoTime();
    processRegionsForRender(pageDto, fieldsToPopulate, parameterMap);
    end = System.nanoTime();
    
    log.info(Thread.currentThread().getStackTrace()[1].getMethodName().toString() + ": processRegionsForRender took: " + (end - start) / 1000000 + "ms");
    
    //now start at the page formatter, which will coordinate
    // all sub-page-items through their respective formatters to apply each template
    final StringBuilder builder = new StringBuilder();
    start = System.nanoTime();
    FormatterService service = sourceFormatters.getFormatterService(PageDto.class.getCanonicalName());
    final String result = service.format(builder, pageDto, 0, new HashMap<String,String>(), fieldService.getAllFields(pageDto)).toString();
    end = System.nanoTime();

    log.info(Thread.currentThread().getStackTrace()[1].getMethodName().toString() + ": service.format took: " + (end - start) / 1000000 + "ms");

    return result;

    
    //throw new NotYetImplementedException("Run each pageItem through the formatters here.");
  }
  
  
  /*
  private String processTemplateForRender(final PageDto pageDto) {
    StringBuilder sb = new StringBuilder();
    sb.append(pageDto.getTemplate().getHeaderSource());
    sb.append(processBodyForRender(pageDto));
    sb.append(pageDto.getTemplate().getFooterSource());
    return sb.toString();
  }
  /*
  private String processBodyForRender(final PageDto pageDto) {
    StringBuilder sb = new StringBuilder();
    
    Map<String, List<String>> renderedRegions = processRegionsForRender(pageDto);
    String templateBody = pageDto.getTemplate().getSource();
    for(Map.Entry<String, List<String>> e : renderedRegions.entrySet())
    {
      for(String content : e.getValue())
      {
        sb.append(content);
      }
      templateBody = templateBody.replace(e.getKey(), sb.toString());
    }
    return templateBody;
  }
  */
  // Map of REGION_POSITION_X to List<String (rendered content)> 
  
  /**
   * Pages can have associated source on page render (a GET request or page render) and on page process (a POST request)
   * This method processes those sources and if any processes populate fields, it will return those field names with their
   * post-processed source.  
   * @param pageDto
   * @return Map<String, PageProcessingSourceSelectDto>
   */
  private Map<String, PageProcessingSourceSelectDto> processPageProcessingSource(final Collection<PageProcessingDto> pageProcesses, 
          final MultiValueMap<String, String> parameterMap) {
    Map<String, PageProcessingSourceSelectDto> allResults = new HashMap<>();
    for(PageProcessingDto process : pageProcesses) {
      for(PageProcessingSourceDto sourceDto : process.getPageProcessingSource()) {
        allResults.putAll(pageProcessingService.processSource(sourceDto, parameterMap));
      }
    }
    return allResults;
  }
  
  private void processRegionsForRender(final PageDto pageDto, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final MultiValueMap<String, String> parameterMap)  {
    for(PageRegionDto pageRegionDto : pageDto.getPageRegions() ) {
      RegionDto regionDto = pageRegionDto.getRegionDto();
      Collection<PageItem<String>> regionItems = new ArrayList<>();

      // All regions can contain fields, and we need the fields to determine our bind variables in later queries.
      regionItems.addAll(fieldService.processFieldsForRender(regionDto.getRegionFields(), fieldsToPopulate, parameterMap));
      regionDto.setSubPageItems(regionItems);

      if(REGION_TYPE_REPORT.equals(regionDto.getCodeRegionType()) ) {
        ReportSourceProcessorService reportService = sourceProcessors.getReportSourceProcessorService(regionDto.getCodeSourceType());
        
        // All fields turned into bind variables, with variable name coming from the field name.
        SourceProcessorResultTable resultTable = reportService.querySource(regionDto.getId(), regionDto.getSource().iterator().next(), bindVarService.convertFieldsToBindVars(regionItems));
        resultTable.setTemplateDto(regionDto.getTemplateDto());
        regionItems.add(resultTable);
      }
      else {
        log.error("Found a " + regionDto.getCodeRegionType() + " ! FIXME: implement the processor!");
      }
    } 
  }

  
  ////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////// PAGE PROCESSING ON A POST BELOW //////////////////////////////////////////////////

  @Override
  public String processPageOnSubmit(long applicationId, long pageId, final MultiValueMap<String, String> submittedFormParams)
  {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    if(appPage != null)
    {
      PageDto pageDto = pageMapper.map(appPage.getPage());
      return processPageItemsOnSubmit(pageDto, submittedFormParams);
    }
    return "Application " + applicationId + ", page " + pageId + "  does not exist.";
  }
  
  /**
   * Pages (typically on a POST, but can also be on a GET) can have associated source that advises
   *   if they should branch to another target page.
   * This method processes the branching code, and returns the branchTarget (if any) that the client browser
   *   should be redirected into.
   * If more than one branch target is processed, the final one is returned only. 
   * @return String branchTarget or null
   */
  private String processPageBranch(final Collection<PageProcessingDto> pageProcesses, final MultiValueMap<String, String> parameterMap,
          final String submitAction) {
    String branchTarget = null;
    for(PageProcessingDto process : pageProcesses) {
      for(PageProcessingSourceDto sourceDto : process.getPageProcessingSource()) { 
        //branchTarget = pageProcessingService.processSource(sourceDto, parameterMap); // FIXME TODO
      }
    }
    return branchTarget;
  }
  
  /**
   * We validate that the submit action exists as a button in the submitted form.
   * This means forms MUST have a button type matching the name of the submit action
   *   for the action to be valid and processed by summit.
   * @param pageDto
   * @param submittedFormParams
   * @return buttonName or empty if not found.
   */
  private String determineSubmittedButton(final PageDto pageDto, final MultiValueMap<String, String> submittedFormParams) {
    final List<String> formIds = submittedFormParams.get(FieldService.FIELD_SUBMITTED_FORM_NAME);
    if(formIds != null && formIds.size() == 1) {
      if(StringUtils.isNotBlank(formIds.get(0)) && formIds.get(0).length() > FieldService.FORM_ID_PREFIX.length()) {
        Long regionIdSubmitted = Long.valueOf(formIds.get(0).substring(FieldService.FORM_ID_PREFIX.length()));
        RegionDto region = getRegionWithId(pageDto, regionIdSubmitted);
        Collection<RegionFieldDto> rfButtons = region.getButtons();
        for(RegionFieldDto rfButton: rfButtons) {
          if(submittedFormParams.containsKey(rfButton.getField().getName())) {
            return rfButton.getField().getName();
          }
        }
      }
    }
    return StringUtils.EMPTY;
  }
  
  private RegionDto getRegionWithId(PageDto pageDto, Long regionId) {
    for(PageRegionDto pr: pageDto.getPageRegions()) {
      if(regionId.equals(pr.getRegionDto().getId())) {
        return pr.getRegionDto();
      }
    }
    return null;
  }
  
  /**
   * 
   *  
   * @param pageDto, Map<String,String> (submittedFormParams)
   * @return String identifying target page to branch to.
   */
  private String processPageItemsOnSubmit(final PageDto pageDto, final MultiValueMap<String, String> submittedFormParams) {
    long start = System.nanoTime();
    final String submitAction = determineSubmittedButton(pageDto, submittedFormParams);
    long end = System.nanoTime();
    
    log.info(Thread.currentThread().getStackTrace()[1].getMethodName().toString() + ": determineSubmittedButton took: " + (end - start) / 1000000 + "ms");
    submittedFormParams.add(REQUEST, submitAction); // add it even if it's empty, so we don't get null pointers if :REQUEST is used in a source statement.
    
    start = System.nanoTime();
    processPageProcessingSource(pageDto.getPagePostProcessings(), submittedFormParams);
    end = System.nanoTime();
    
    log.info(Thread.currentThread().getStackTrace()[1].getMethodName().toString() + ": processPageRenderSource took: " + (end - start) / 1000000 + "ms");

    start = System.nanoTime();
    String branchTarget = processPageBranch(pageDto.getPagePostProcessingBranches(), submittedFormParams, submitAction); // FIXME TODO
    end = System.nanoTime();
    log.info(Thread.currentThread().getStackTrace()[1].getMethodName().toString() + ": processPageBranch took: " + (end - start) / 1000000 + "ms");

    return branchTarget;
  }  
  
}
