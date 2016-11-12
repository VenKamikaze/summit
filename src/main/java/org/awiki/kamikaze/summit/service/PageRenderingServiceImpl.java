package org.awiki.kamikaze.summit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.dto.entry.FieldDto;
import org.awiki.kamikaze.summit.dto.entry.PageDto;
import org.awiki.kamikaze.summit.dto.entry.PageItem;
import org.awiki.kamikaze.summit.dto.entry.PageRegionDto;
import org.awiki.kamikaze.summit.dto.entry.RegionDto;
import org.awiki.kamikaze.summit.dto.entry.RegionFieldDto;
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
  
  private static final String REGION_TYPE_REPORT = "Report";
  
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
  
  @Override
  /**
   * Overarching method that begins the page rendering
   * Maps the page domain to the dto, .... nothing else yet implemented!
   */
  public PageDto renderPage(long applicationId, long pageId) {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    //PageDto pageDto = mapper.map(appPage.getPage(), PageDto.class);
    PageDto pageDto = pageMapper.map(appPage.getPage());
    
    DebugUtils.debugObjectGetters(pageDto);
    
    return pageDto;
  }
  
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
  public String renderPageToString(long applicationId, long pageId) {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    if(appPage != null)
    {
      PageDto pageDto = pageMapper.map(appPage.getPage());
      
      DebugUtils.debugObjectGetters(pageDto);
      
      return processPageItems(pageDto);
    }
    return "Application " + applicationId + " does not exist.";
  }
  

  /**
   * Gathers the collection of PageItems, then applies formatting to all of them.
   * Renders this to a string. 
   * @param pageDto
   * @return
   */
  private String processPageItems(final PageDto pageDto) {
    // Get the collection of pageItems in each region
    //Map<String, List<PageItem<?>>> regionPageItems = processRegionsForRender(pageDto);
    long start = System.nanoTime();
    processRegionsForRender(pageDto);
    long end = System.nanoTime();
    
    log.info(Thread.currentThread().getStackTrace()[1].getMethodName().toString() + ": processRegionsForRender took: " + (end - start) / 1000000 + "ms");
    
    //now run each page item through the formatters to apply each template
    final StringBuilder builder = new StringBuilder();
    start = System.nanoTime();
    FormatterService service = sourceFormatters.getFormatterService(PageDto.class.getCanonicalName());
    final String result = service.format(builder, pageDto, 0).toString();
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
  
  private void processRegionsForRender(final PageDto pageDto)  {
    for(PageRegionDto pageRegionDto : pageDto.getPageRegions() ) {
      RegionDto regionDto = pageRegionDto.getRegionDto();
      Collection<PageItem<String>> regionItems = new ArrayList<>();
      if(REGION_TYPE_REPORT.equals(regionDto.getCodeRegionType()) ) {
        ReportSourceProcessorService reportService = (ReportSourceProcessorService) sourceProcessors.getSourceProcessorService(regionDto.getCodeSourceType());
        SourceProcessorResultTable resultTable = reportService.querySource(regionDto.getSource().iterator().next(), null);
        regionItems.add(resultTable);
      }
      else {
        log.error("Found a " + regionDto.getCodeRegionType() + " ! FIXME: implement the processor!");
      }

      regionItems.addAll(processFieldsForRender(regionDto.getRegionFields()));
      regionDto.setSubPageItems(regionItems);
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
  
  
  private Collection<PageItem<String>> processFieldsForRender(final Set<RegionFieldDto> regionFieldDtos) {
    Collection<PageItem<String>> fields = new ArrayList<>();
    
    for(RegionFieldDto regionFieldDto : regionFieldDtos) {
      final FieldDto fieldDto = regionFieldDto.getField();
      FieldDto.PostProcessedFieldContentDto processedContent = fieldDto.new PostProcessedFieldContentDto();
      FieldDto.PostProcessedFieldContentDto processedDefaultContent = fieldDto.new PostProcessedFieldContentDto();
      SingularSourceProcessorService processor = (SingularSourceProcessorService) sourceProcessors.getSourceProcessorService(fieldDto.getCodeFieldSourceType());
      processedContent.setPostProcessedContent(processor.querySource(fieldDto.getSource(), null).getResultValue()); // TODO FIXME handle bind vars
      processedDefaultContent.setPostProcessedContent(processor.querySource( fieldDto.getDefaultValueSource() , null ).getResultValue() );  // TODO FIXME handle bind vars
      fieldDto.setPostProcessedSource(processedContent);
      fieldDto.setPostProcessedDefaultValue(processedDefaultContent);
      
      
      fields.add(fieldDto);
    }
    return fields;
  }

}
