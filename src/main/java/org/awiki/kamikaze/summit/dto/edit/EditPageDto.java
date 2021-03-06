package org.awiki.kamikaze.summit.dto.edit;

import java.util.HashSet;
import java.util.Set;

public class EditPageDto {
  
  private Long                        id;
  private Set<EditPageRegionDto>      pageRegions      = new HashSet<>(0);
  //not yet impl private Set<PageProcessingDto>  pageProcessings  = new HashSet<>(0); // add later!
  private Set<EditApplicationPageDto> applicationPages = new HashSet<>(0);

  //changed definition
  private String                      name;
  private EditTemplateDto             template;
  
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
  
  public EditTemplateDto getTemplate() {
    return template;
  }
  public void setTemplate(EditTemplateDto template) {
    this.template = template;
  }
  
  public Set<EditPageRegionDto> getPageRegions() {
    return pageRegions;
  }
  public void setPageRegions(Set<EditPageRegionDto> pageRegions) {
    this.pageRegions = pageRegions;
  }
  /*
  public Set<PageProcessingDto> getPageProcessings() {
    return pageProcessings;
  }
  public void setPageProcessings(Set<PageProcessingDto> pageProcessings) {
    this.pageProcessings = pageProcessings;
  }
  */
  public Set<EditApplicationPageDto> getApplicationPages() {
    return applicationPages;
  }
  public void setApplicationPages(Set<EditApplicationPageDto> applicationPages) {
    this.applicationPages = applicationPages;
  }
  
  public boolean hasSubPageItems()
  {
    return pageRegions.size() > 0;
  }
  
  public EditTemplateDto getTemplateDto()
  {
    return getTemplate();
  }

}
