package org.awiki.kamikaze.summit.dto.entry;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;


public class FieldDto {
  
  private Integer               id;
  
  @NotBlank(message = "Must not be empty.")
  @Size(max = 10)
  private String                codeFieldType;  // ???
  
  @NotBlank(message = "Must not be empty.")
  @Size(max = 10)
  private String                codeFieldSourceType;  // e.g. HTML, SQL, JSP
  private String                source; // actual SQL/HTML/JSP code
  private String                codeFieldDefaultValueSourceType; // e.g. HTML, SQL, JSP
  private String                defaultValueSource; // actual SQL/HTML/JSP code
  private String                notes;
  private Set<RegionFieldDto>   regionFields = new HashSet<>(0);
  
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  public String getCodeFieldType() {
    return codeFieldType;
  }
  public void setCodeFieldType(String codeFieldType) {
    this.codeFieldType = codeFieldType;
  }
  public String getCodeFieldSourceType() {
    return codeFieldSourceType;
  }
  public void setCodeFieldSourceType(String codeFieldSourceType) {
    this.codeFieldSourceType = codeFieldSourceType;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public String getCodeFieldDefaultValueSourceType() {
    return codeFieldDefaultValueSourceType;
  }
  public void setCodeFieldDefaultValueSourceType(
      String codeFieldDefaultValueSourceType) {
    this.codeFieldDefaultValueSourceType = codeFieldDefaultValueSourceType;
  }
  public String getDefaultValueSource() {
    return defaultValueSource;
  }
  public void setDefaultValueSource(String defaultValueSource) {
    this.defaultValueSource = defaultValueSource;
  }
  public String getNotes() {
    return notes;
  }
  public void setNotes(String notes) {
    this.notes = notes;
  }
  public Set<RegionFieldDto> getRegionFields() {
    return regionFields;
  }
  public void setRegionFields(Set<RegionFieldDto> regionFields) {
    this.regionFields = regionFields;
  }
  
}
