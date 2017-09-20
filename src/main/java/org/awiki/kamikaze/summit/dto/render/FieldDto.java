package org.awiki.kamikaze.summit.dto.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.Size;

import org.apache.commons.collections4.MapUtils;
import org.hibernate.validator.constraints.NotBlank;


public class FieldDto implements PageItem<String> {
  
  private Long               id;
  private String             name;
  
  @NotBlank(message = "Must not be empty.")
  @Size(max = 10)
  private String                codeFieldType;  // e.g. 'text','number','drop-down',etc
  
  @NotBlank(message = "Must not be empty.")
  @Size(max = 10)
  private String                codeFieldSourceType;  // e.g. HTML, SQL, JSP
  //private String                source; // actual SQL/HTML/JSP code
  private List<SourceDto>       source;
  private String                codeFieldDefaultValueSourceType; // e.g. HTML, SQL, JSP
  private String                defaultValueSource; // actual SQL/HTML/JSP code
  private String                notes;
  private Set<RegionFieldDto>   regionFields = new HashSet<>(0);
  private TemplateDto           template;
  
  private PostProcessedFieldContentDto postProcessedSource;
  private PostProcessedFieldContentDto postProcessedDefaultValue;
  
  private ConditionalDto        conditional = ConditionalDto.ALWAYS_TRUE; // the default.
  
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
  /*public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  // FIXME HACK for mapping Set<String> to singular string with dozer.
  public void setSource(Set<String> sources) {
    if(sources != null && ! sources.isEmpty()) {
      for(String src : sources) {
        this.source += src;
      }
    }
  }*/
  
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

  @Override
  public void setProcessedSource(String t)
  {
    postProcessedSource.setPostProcessedContent(t);
  }
  @Override
  public String getProcessedSource()
  {
    return postProcessedSource != null ? postProcessedSource.getPostProcessedContent() :
                                         postProcessedDefaultValue.getPostProcessedContent();
  }
  
  @Override
  public boolean hasChildPageItems()
  {
    return false;
  }
  @Override
  public List<PageItem<String>> getChildPageItems()
  {
    return new ArrayList<>(0);
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

  @Override
  public Map<String, String> getReplacementVariables()
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
}
