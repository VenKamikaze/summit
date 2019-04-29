package org.awiki.kamikaze.summit.dto.edit;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;


public class EditRegionDto {

  private Long                  id;
  private String                codeRegionPosition; // e.g. header,bodyX,footer
  private String                codeRegionType;     // e.g. HTML,SQL,JSP
  private String                codeSourceType;     // e.g. dml_report
  
  @NotBlank(message = "Must not be empty.")
  private String                name;
  
  private Set<String>           source = new LinkedHashSet<>(0);
  
  private Set<EditPageRegionDto>    pageRegions  = new HashSet<>(0);
  // not yet impl private Set<RegionFieldDto>   regionFields = new LinkedHashSet<>(0);
  private EditTemplateDto           template;
  
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
  public Set<String> getSource() {
    return source;
  }
  public void setSource(Set<String> source) {
    this.source = source;
  }
  public Set<EditPageRegionDto> getPageRegions() {
    return pageRegions;
  }
  public void setPageRegions(Set<EditPageRegionDto> pageRegions) {
    this.pageRegions = pageRegions;
  }
  /*
  public Set<RegionFieldDto> getRegionFields() {
    return regionFields;
  }
  public void setRegionFields(Set<RegionFieldDto> regionFields) {
    this.regionFields = regionFields;
  }
  */
  public String getCodeSourceType()
  {
    return codeSourceType;
  }
  public void setCodeSourceType(String codeSourceType)
  {
    this.codeSourceType = codeSourceType;
  }
  
  public EditTemplateDto getTemplate()
  {
    return template;
  }
  public void setTemplate(EditTemplateDto template)
  {
    this.template = template;
  }

  public EditTemplateDto getTemplateDto()
  {
    return getTemplate();
  }

}
