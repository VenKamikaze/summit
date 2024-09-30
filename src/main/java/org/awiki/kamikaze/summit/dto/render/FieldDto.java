package org.awiki.kamikaze.summit.dto.render;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.constraints.Size;

import org.apache.commons.collections4.MapUtils;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class FieldDto implements PageItem<String> {
  
  private Long               id;
  private String             name;
  
  @NotBlank(message = "Must not be empty.")
  @Size(max = 10)
  private String                codeFieldType;  // e.g. 'text','number','drop-down',etc
  
  @NotBlank(message = "Must not be empty.")
  @Size(max = 10)
  private String                codeFieldSourceType;

  private List<SourceDto>       source;
  private String                codeFieldDefaultValueSourceType;
  private List<SourceDto>       defaultValueSource;
  private String                notes;
  
  @JsonIgnore
  private Set<RegionFieldDto>   regionFields = new HashSet<>(0);
  
  @JsonIgnore
  private TemplateDto           template;
  
  private PostProcessedFieldContentDto postProcessedSource;
  private PostProcessedFieldContentDto postProcessedDefaultValue;
  
  private LabelDto              label = null;
  private ConditionalDto        conditional = new ConditionalDto();
  
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
  public List<SourceDto> getSource() {
    return source;
  }
  public void setSource(List<SourceDto> source) {
    this.source = source;
  }

  public String getCodeFieldDefaultValueSourceType() {
    return codeFieldDefaultValueSourceType;
  }
  public void setCodeFieldDefaultValueSourceType(
      String codeFieldDefaultValueSourceType) {
    this.codeFieldDefaultValueSourceType = codeFieldDefaultValueSourceType;
  }
  public List<SourceDto> getDefaultValueSource() {
    return defaultValueSource;
  }
  public void setDefaultValueSource(List<SourceDto> source) {
    this.defaultValueSource = source;
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
    
    public void addPostProcessedContent(String additionalPostProcessedContent) {
      if(this.postProcessedContent == null) {
        this.postProcessedContent = additionalPostProcessedContent;
      }
      else {
        this.postProcessedContent += additionalPostProcessedContent;
      }
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

  @Override
  public String getProcessedSource()
  {
    if(postProcessedSource != null) {
      return postProcessedSource.getPostProcessedContent();
    }
    return postProcessedDefaultValue != null ? postProcessedDefaultValue.getPostProcessedContent() : null;
                                         
  }
  
  @Override
  public boolean hasChildPageItems()
  {
    return false;
  }
  @SuppressWarnings("unchecked")
  @Override
  public List<PageItem<String>> getChildPageItems()
  {
    return Collections.EMPTY_LIST;
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

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, String> getCustomReplacementVariables()
  {
    return MapUtils.EMPTY_SORTED_MAP;
  }
  
  @Override
  public ConditionalDto getConditional()
  {
    return conditional;
  }
  
  public void setConditional(ConditionalDto c)
  {
    this.conditional = c;
  }
  
  public boolean hasLabel() {
    return label != null;
  }
  
  public LabelDto getLabel()
  {
    return label;
  }
  
  public void setLabel(LabelDto l)
  {
    this.label = l;
  }
  
}
