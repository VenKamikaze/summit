package org.awiki.kamikaze.summit.dto.entry;

import java.util.HashSet;
import java.util.Set;

public class PageDto implements PageItem {
  
  private Long                 id;
  private Set<PageRegionDto>      pageRegions      = new HashSet<>(0);
  private Set<PageProcessingDto>  pageProcessings  = new HashSet<>(0);
  private Set<ApplicationPageDto> applicationPages = new HashSet<>(0);

  //changed definition
  private String               name;
  private TemplateDto          template;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
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
  public Set<ApplicationPageDto> getApplicationPages() {
    return applicationPages;
  }
  public void setApplicationPages(Set<ApplicationPageDto> applicationPages) {
    this.applicationPages = applicationPages;
  }
  
  
}
