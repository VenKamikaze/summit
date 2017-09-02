package org.awiki.kamikaze.summit.service;

import static org.awiki.kamikaze.summit.domain.Region.REGION_TYPE_REPORT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.domain.PageProcessing;
import org.awiki.kamikaze.summit.domain.codetable.CodeProcessingType;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.PageProcessingDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceDto;
import org.awiki.kamikaze.summit.dto.render.PageProcessingSourceSelectDto;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.service.formatter.FormatterService;
import org.awiki.kamikaze.summit.service.formatter.ProxyFormatterService;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.DebugUtils;
import org.awiki.kamikaze.summit.util.mapper.PageToPageDtoMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is a top level service that co-ordinates the other sub-services to build up a full page.
 * @author msaun
 *
 */
@Service
public class PageRenderingServiceImpl implements PageRenderingService {
  
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
  public String renderPageToString(long applicationId, long pageId, final Map<String, String> parameterMap) {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    if(appPage != null)
    {
      PageDto pageDto = pageMapper.map(appPage.getPage());
      
      DebugUtils.debugObjectGetters(pageDto);
      
      return processPageItems(pageDto, parameterMap);
    }
    return "Application " + applicationId + " does not exist.";
  }
  

  /**
   * Gathers the collection of PageItems, then applies formatting to all of them.
   * Renders this to a string. 
   * @param pageDto
   * @return
   */
  private String processPageItems(final PageDto pageDto, final Map<String, String> parameterMap) {
    // Get the collection of pageItems in each region
    //Map<String, List<PageItem<?>>> regionPageItems = processRegionsForRender(pageDto);
    long start = System.nanoTime();
    Map<String, PageProcessingSourceSelectDto> fieldsToPopulate = processPageRenderSource(pageDto.getPageRenderPreRegionPageProcessings(), parameterMap);
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
    final String result = service.format(builder, pageDto, 0, new HashMap<String,String>()).toString();
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
  private Map<String, PageProcessingSourceSelectDto> processPageRenderSource(final Collection<PageProcessingDto> pageProcesses, final Map<String, String> parameterMap) {
    Map<String, PageProcessingSourceSelectDto> allResults = new HashMap<>();
    for(PageProcessingDto process : pageProcesses) {
      for(PageProcessingSourceDto sourceDto : process.getPageProcessingSource()) {
        allResults.putAll(pageProcessingService.processSource(sourceDto, parameterMap));
      }
    }
    return allResults;
  }
  
  private void processRegionsForRender(final PageDto pageDto, final Map<String, PageProcessingSourceSelectDto> fieldsToPopulate, final Map<String, String> parameterMap)  {
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
  
  /*
  private Map<String, List<PageItem<?>>> processRegionsForRender(final PageDto pageDto) {
    Map<String, List<PageItem<?>>> processedRegions = new LinkedHashMap<>();
    
    
    for(PageRegionDto pageRegionDto : pageDto.getPageRegions() ) {
      List<PageItem<?>> content = new ArrayList<>();
      if (processedRegions.containsKey(pageRegionDto.getRegionDto().getCodeRegionPosition()))
      {
        content = processedRegions.get(pageRegionDto.getRegionDto().getCodeRegionPosition());
      }
      
      RegionDto regionDto = pageRegionDto.getRegionDto();
      if(REGION_TYPE_REPORT.equals(regionDto.getCodeRegionType()) )
      {
        ReportSourceProcessorService reportService = (ReportSourceProcessorService) sourceProcessors.getSourceProcessorService(regionDto.getCodeSourceType());
        SourceProcessorResultTable resultTable = reportService.querySource(regionDto.getSource().iterator().next(), null);
        content.add(resultTable);
      }
      else
      {
        log.error("Found a " + regionDto.getCodeRegionType() + " ! FIXME: implement the processor!");
      }
      content.addAll(processFieldsForRender(regionDto.getRegionFields()));
      processedRegions.put(pageRegionDto.getRegionDto().getCodeRegionPosition(), content);
    }
    return processedRegions;
  }*/
  
  
}
