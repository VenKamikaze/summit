package org.awiki.kamikaze.summit.dto.entry;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;


public class RegionDto {

  private Long               id;
  private String                codeRegionPosition; // e.g. header,bodyX,footer
  private String                codeRegionType;     // e.g. HTML,SQL,JSP
  
  @NotBlank(message = "Must not be empty.")
  private String                name;
  
  private String             source;
  
  private Set<PageRegionDto>    pageRegions  = new HashSet<>(0);
  private Set<RegionFieldDto>   regionFields = new HashSet<>(0);
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getCodeRegionPosition() {
    return codeRegionPosition;
  }
  public void setCodeRegionPosition(String codeRegionPosition) {
    this.codeRegionPosition = codeRegionPosition;
  }
  public String getCodeRegionType() {
    return codeRegionType;
  }
  public void setCodeRegionType(String codeRegionType) {
    this.codeRegionType = codeRegionType;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public Set<PageRegionDto> getPageRegions() {
    return pageRegions;
  }
  public void setPageRegions(Set<PageRegionDto> pageRegions) {
    this.pageRegions = pageRegions;
  }
  public Set<RegionFieldDto> getRegionFields() {
    return regionFields;
  }
  public void setRegionFields(Set<RegionFieldDto> regionFields) {
    this.regionFields = regionFields;
  }

}
