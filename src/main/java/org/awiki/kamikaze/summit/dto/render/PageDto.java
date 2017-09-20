package org.awiki.kamikaze.summit.dto.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Transient;

import org.apache.commons.collections4.CollectionUtils;
import org.awiki.kamikaze.summit.service.PageProcessingService;
import org.awiki.kamikaze.summit.service.formatter.FormatEnums;

public class PageDto implements PageItem<String> {
  
  private Long                    id;
  private Set<PageRegionDto>      pageRegions      = new LinkedHashSet<>(0);
  private Set<PageProcessingDto>  pageProcessings  = new LinkedHashSet<>(0);
  private Set<ApplicationPageDto> applicationPages = new HashSet<>(0);

  //changed definition
  private String                  name;
  private TemplateDto             template;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  @Override
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  
  public TemplateDto getTemplate() {
    return template;
  }
  public void setTemplate(TemplateDto template) {
    this.template = template;
  }
  
  public Set<PageRegionDto> getPageRegions() {
    return pageRegions;
  }
  public void setPageRegions(Set<PageRegionDto> pageRegions) {
    this.pageRegions = pageRegions;
  }
  public Set<PageProcessingDto> getPageProcessings() {
    return pageProcessings;
  }
  public void setPageProcessings(Set<PageProcessingDto> pageProcessings) {
    this.pageProcessings = pageProcessings;
  }

  /**
   * Gets all page processing items associated with this page that should execute before regions are rendered.
   * @return
   */
  @Transient
  public Collection<PageProcessingDto> getPageRenderPreRegionPageProcessings() {
    return CollectionUtils.select(this.getPageProcessings(), PageProcessingService.PAGE_RENDER_PRE_REGION_PREDICATE);
  }

  /**
   * Gets all page processing items associated with this page that should execute on a POST.
   * @return
   */
  @Transient
  public Collection<PageProcessingDto> getPagePostProcessings() {
    return CollectionUtils.select(this.getPageProcessings(), PageProcessingService.PAGE_POST_PROCESS_PREDICATE);
  }
  
  /**
   * Gets all page processing items associated with this page that execute on a POST and return a page branch target.
   * @return
   */
  @Transient
  public Collection<PageProcessingDto> getPagePostProcessingBranches() {
    return CollectionUtils.select(this.getPageProcessings(), PageProcessingService.PAGE_POST_PROCESS_BRANCH_PREDICATE);
  }
  
  public Set<ApplicationPageDto> getApplicationPages() {
    return applicationPages;
  }
  public void setApplicationPages(Set<ApplicationPageDto> applicationPages) {
    this.applicationPages = applicationPages;
  }
  @Override
  public void setProcessedSource(String t)
  {
    // TODO Auto-generated method stub
    
  }
  @Override
  public String getProcessedSource()
  {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  public boolean hasChildPageItems()
  {
    return pageRegions.size() > 0;
  }
  @Override
  public Collection<PageItem<String>> getChildPageItems()
  {
    Collection<PageItem<String>> regions = new ArrayList<>(pageRegions.size());
    for(PageRegionDto pageRegionDto : pageRegions) {
      regions.add(pageRegionDto.getRegionDto());
    }
    return regions;
  }
  
  @Override
  public TemplateDto getTemplateDto()
  {
    return getTemplate();
  }
  
  @Override
  public Map<String, String> getReplacementVariables()
  {
    return new TreeMap<String, String>() {{ put(FormatEnums.REPLACEMENT_PAGE_ID_VARIABLE.toString(), getId().toString()); }};
  }
  
  @Override
  public ConditionalDto getConditional()
  {
    return null; // pages cannot be conditional.
  }
}
