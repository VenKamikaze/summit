package org.awiki.kamikaze.summit.dto.render;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;


public class RegionDto implements PageItem<String> {

  private Long                  id;
  private String                codeRegionPosition; // e.g. header,bodyX,footer
  private String                codeRegionType;     // e.g. HTML,SQL,JSP
  private String                codeSourceType;     // e.g. dml_report
  
  @NotBlank(message = "Must not be empty.")
  private String                name;
  
  private Set<String>           source = new LinkedHashSet<>(0);
  
  private Set<PageRegionDto>    pageRegions  = new HashSet<>(0);
  private Set<RegionFieldDto>   regionFields = new LinkedHashSet<>(0);
  private TemplateDto           template;
  
  ////// post-processed page items, not mapped.
  private Collection<PageItem<String>> subPageItems = new LinkedHashSet<>(0);
  
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
  public String getCodeSourceType()
  {
    return codeSourceType;
  }
  public void setCodeSourceType(String codeSourceType)
  {
    this.codeSourceType = codeSourceType;
  }
  
  public void setSubPageItems(Collection<PageItem<String>> subPageItems) {
    this.subPageItems = subPageItems;
  }
  
  @Override
  public boolean hasSubPageItems()
  {
    return subPageItems.size() > 0;
  }
  
  @Override
  public Collection<PageItem<String>> getSubPageItems()
  {
    return subPageItems;
    /*
    Collection<PageItem<String>> fields = new ArrayList<>(regionFields.size());
    for(RegionFieldDto regionField : regionFields) {
      fields.add(regionField.getField());
    }
    return fields;
    */
  }
  
  @Override
  public void setProcessedSource(String t)
  {
    // postProcessedValue = t;
  }
  @Override
  public String getProcessedSource()
  {
    // TODO Auto-generated method stub
    return null;
  }
  public TemplateDto getTemplate()
  {
    return template;
  }
  public void setTemplate(TemplateDto template)
  {
    this.template = template;
  }

  @Override
  public TemplateDto getTemplateDto()
  {
    return getTemplate();
  }

}
