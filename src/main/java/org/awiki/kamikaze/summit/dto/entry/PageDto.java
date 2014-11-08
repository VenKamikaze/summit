package org.awiki.kamikaze.summit.dto.entry;

import java.util.HashSet;
import java.util.Set;

import org.awiki.kamikaze.summit.dto.entry.ApplicationPageDto;
import org.awiki.kamikaze.summit.dto.entry.PageProcessingDto;
import org.awiki.kamikaze.summit.dto.entry.PageRegionDto;

public class PageDto {
  
  private Integer                 id;
  private Set<PageRegionDto>      pageRegions      = new HashSet<>(0);
  private Set<PageProcessingDto>  pageProcessings  = new HashSet<>(0);
  private Set<ApplicationPageDto> applicationPages = new HashSet<>(0);
  
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
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
