package org.awiki.kamikaze.summit.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.awiki.kamikaze.summit.domain.ApplicationPage;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageDto;
import org.awiki.kamikaze.summit.dto.render.PageRegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionDto;
import org.awiki.kamikaze.summit.dto.render.RegionFieldDto;
import org.awiki.kamikaze.summit.dto.render.SourceDto;
import org.awiki.kamikaze.summit.mapper.PageMapper;
import org.awiki.kamikaze.summit.repository.ApplicationPageRepository;
import org.awiki.kamikaze.summit.service.processor.ProxySourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.ReportSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.SingularSourceProcessorService;
import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable;
import org.awiki.kamikaze.summit.util.DebugUtils;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * This is a top level service that co-ordinates the other sub-services to build up a full page.
 * @author msaun
 *
 */
//@Service
public class OldPageRenderingServiceImplBeforeTemplateRefactor implements OldPageRenderingServiceBeforeTemplateRefactor {
  
  private static final Logger log = LoggerFactory.getLogger(OldPageRenderingServiceImplBeforeTemplateRefactor.class);
  
  private static final String REGION_TYPE_REPORT = "Report";
  
  private static final String TEMPLATE_REPLACEMENT_VAR = "__#DATA#__";
  
  private ApplicationPageRepository appPageStore;
  private ProxySourceProcessorService sourceProcessors;
  private PageMapper pageMapper;

  
  @Autowired
  public void setAppPageStore(ApplicationPageRepository appPageStore) {
    this.appPageStore = appPageStore;
  }

  @Autowired
  public void setSourceProcessors(ProxySourceProcessorService sourceProcessors) {
    this.sourceProcessors = sourceProcessors;
  }

  @Autowired
  public void setPageMapper(PageMapper pageMapper) {
    this.pageMapper = pageMapper;
  }


  public PageDto renderPage(long applicationPageId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  /**
   * Overarching method that begins the page rendering
   * Maps the page domain to the dto, .... nothing else yet implemented!
   */
  public PageDto renderPage(long applicationId, long pageId) {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    //PageDto pageDto = mapper.map(appPage.getPage(), PageDto.class);
    PageDto pageDto = pageMapper.map(appPage.getPage(), new CycleAvoidingMappingContext());
    
    DebugUtils.debugObjectGetters(pageDto);
    
    return pageDto;
  }
  
  /**
   * Overarching method that handles the page rendering
   * This one is mainly used for testing, renders the entire page and all it's contents to a String
   * 1. Maps Page domain and linked objects to a PageDto
   * 2. Calls ProcessTemplateForRender, which pulls down the template, and begins processing its components
   * (in processTemplateForRender) 3. (Should) pulls header, body and footer and processes these components
   * (in processBodyForRender) 4. Passes unprocessed region content to be processed, in order of sort order
   * (in processRegionsForRender) 5. Processes either the region source (ie. report regions) or calls method to process all field content
   * (in processFieldsForRender) 6. Processes each field source into processed content, ordered by sort order
   */
  public String renderPageAsString(long applicationId, long pageId) {
    ApplicationPage appPage = appPageStore.findByApplicationIdAndPageId(applicationId, pageId);
    if(appPage != null)
    {
      PageDto pageDto = pageMapper.map(appPage.getPage(), new CycleAvoidingMappingContext());
      
      DebugUtils.debugObjectGetters(pageDto);
      return processTemplateForRender(pageDto);
    }
    return "Application " + applicationId + " does not exist.";
  }
  
  private String processTemplateForRender(final PageDto pageDto) {
    StringBuilder sb = new StringBuilder();
    //sb.append(pageDto.getTemplate().getHeaderSource());
    sb.append(processBodyForRender(pageDto));
    //sb.append(pageDto.getTemplate().getFooterSource());
    return sb.toString();
  }
  
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
    //removeUnusedRegions();
    return templateBody;
  }
  
  // Map of REGION_POSITION_X to List<String (rendered content)>
  private Map<String, List<String>> processRegionsForRender(final PageDto pageDto) {
    Map<String, List<String>> renderedRegions = new HashMap<>();
    
    for(PageRegionDto pageRegionDto : pageDto.getPageRegions() ) {
      List<String> content = new ArrayList<>();
      if (renderedRegions.containsKey(pageRegionDto.getRegionDto().getCodeRegionPosition()))
      {
        content = renderedRegions.get(pageRegionDto.getRegionDto().getCodeRegionPosition());
      }
      
      StringBuffer sb = new StringBuffer();
      
      RegionDto regionDto = pageRegionDto.getRegionDto();
      if(REGION_TYPE_REPORT.equals(regionDto.getCodeRegionType()) )
      {
        ReportSourceProcessorService reportService = sourceProcessors.getReportSourceProcessorService(regionDto.getCodeSourceType());
        
        // TODO, split+call out to a formatter here to apply styling & formatting as needed to the results.
        SourceProcessorResultTable resultTable = reportService.querySource(regionDto.getId(), regionDto.getSource().iterator().next(), null);
        for(int y = 0; y < resultTable.getCount(); y++)
        {
          final SourceProcessorResultTable.Row row = resultTable.getRowByY(y);
          for(SourceProcessorResultTable.Cell cell : row.getCells())
          {
            sb.append(cell.getValue());
          }
        }
      }
      else
      {
        log.error("Found a " + regionDto.getCodeRegionType() + " ! FIXME: implement the processor!");
      }
      sb.append(processFieldsForRender(regionDto.getRegionFields()));
      content.add(sb.toString());
      renderedRegions.put(pageRegionDto.getRegionDto().getCodeRegionPosition(), content);
    }
    return renderedRegions;
  }
  
  
  private String processFieldsForRender(final Set<RegionFieldDto> regionFieldDtos) {
    StringBuilder sb = new StringBuilder();
    //List<FieldDto> processedFields = new ArrayList<>(regionFieldDtos.size());
    
    for(RegionFieldDto regionFieldDto : regionFieldDtos) {
      final FieldDto fieldDto = regionFieldDto.getField();
      FieldDto.PostProcessedFieldContentDto processedContent = fieldDto.new PostProcessedFieldContentDto();
      FieldDto.PostProcessedFieldContentDto processedDefaultContent = fieldDto.new PostProcessedFieldContentDto();
      SingularSourceProcessorService processor = sourceProcessors.getSingularSourceProcessorService(fieldDto.getCodeFieldSourceType());
      String processedSource = "";
      for(SourceDto source : fieldDto.getSource()) {
        processedSource += processor.processSource(source.getSource(), fieldDto.getCodeFieldSourceType(), null).getResultValue(); // TODO FIXME handle bind vars
      }
      processedContent.setPostProcessedContent(processedSource);
      String processedDefaultSource = "";
      for(SourceDto source : fieldDto.getDefaultValueSource()) {
        processedDefaultSource += processor.processSource( source.getSource() , fieldDto.getCodeFieldDefaultValueSourceType(), null ).getResultValue();  // TODO FIXME handle bind vars
      }
      processedDefaultContent.setPostProcessedContent(processedDefaultSource);
      
      fieldDto.setPostProcessedSource(processedContent);
      fieldDto.setPostProcessedDefaultValue(processedDefaultContent);
      
      //processedFields.add(fieldDto);
      
      if(StringUtils.isEmpty(processedContent.getPostProcessedContent()))
        sb.append(processedDefaultContent.getPostProcessedContent());
      else
        sb.append(processedContent.getPostProcessedContent());
    }
    return sb.toString();
  }

}
