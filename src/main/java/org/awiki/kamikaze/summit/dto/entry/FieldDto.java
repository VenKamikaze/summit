package org.awiki.kamikaze.summit.dto.entry;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;


public class FieldDto {
  
  private Long               id;
  
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
  
  private PostProcessedFieldContentDto postProcessedSource;
  private PostProcessedFieldContentDto postProcessedDefaultValue;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
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


  public PostProcessedFieldContentDto getPostProcessedSource() {
    return postProcessedSource;
  }
  public void setPostProcessedSource(
      PostProcessedFieldContentDto postProcessedSource) {
    this.postProcessedSource = postProcessedSource;
  }
  public PostProcessedFieldContentDto getPostProcessedDefaultValue() {
    return postProcessedDefaultValue;
  }
  public void setPostProcessedDefaultValue(
      PostProcessedFieldContentDto postProcessedDefaultValue) {
    this.postProcessedDefaultValue = postProcessedDefaultValue;
  }
  
  public class PostProcessedFieldContentDto 
  {
    private String postProcessedContent;

    public String getPostProcessedContent() {
      return postProcessedContent;
    }

    public void setPostProcessedContent(String postProcessedContent) {
      this.postProcessedContent = postProcessedContent;
    }
    
    @Override
    public String toString() {
      return postProcessedContent;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof PostProcessedFieldContentDto)
      {
        PostProcessedFieldContentDto target = (PostProcessedFieldContentDto) obj;
        if(null != target.toString() && null != this.toString())
        {
          return this.toString().equals(target.toString());
        }
        else
        {
          return (null == target.toString() && null == this.toString());
        }
      }
      return false;
    }
  }
  
}
