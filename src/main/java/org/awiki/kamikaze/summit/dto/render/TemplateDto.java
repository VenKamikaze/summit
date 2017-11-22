package org.awiki.kamikaze.summit.dto.render;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class TemplateDto {

  private long       id;
  private String     className;
  private String     source;
  private String     mimeType;
  
  @JsonBackReference("templateChildren")
  private TemplateDto parent;
  
  @JsonManagedReference("templateChildren")
  private Set<TemplateDto> children = new HashSet<>(0);
  
  private Set<PageDto> pages = new HashSet<>(0);
  
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String name) {
    this.className = name;
  }


  public String getSource() {
    return source;
  }

  public void setSource(String bodySource) {
    this.source = bodySource;
  }

  public Set<PageDto> getPages() {
    return pages;
  }

  public void setPages(Set<PageDto> pages) {
    this.pages = pages;
  }

  public String getMimeType()
  {
    return mimeType;
  }

  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }

  public TemplateDto getParent() {
    return parent;
  }

  public void setParent(TemplateDto parent) {
    this.parent = parent;
  }

  public Set<TemplateDto> getChildren()
  {
    return children;
  }

  public void setChildren(Set<TemplateDto> children)
  {
    this.children = children;
  }
  
}
