package org.awiki.kamikaze.summit.dto.edit;

import java.util.HashSet;
import java.util.Set;

public class EditTemplateDto {

  private long       id;
  private String     className;
  private String     source;
  private String     mimeType;
  
  private EditTemplateDto parent;
  private Set<EditTemplateDto> children = new HashSet<>(0);
  
  private Set<EditPageDto> pages = new HashSet<>(0);
  
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

  public Set<EditPageDto> getPages() {
    return pages;
  }

  public void setPages(Set<EditPageDto> pages) {
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

  public EditTemplateDto getParent() {
    return parent;
  }

  public void setParent(EditTemplateDto parent) {
    this.parent = parent;
  }

  public Set<EditTemplateDto> getChildren()
  {
    return children;
  }

  public void setChildren(Set<EditTemplateDto> children)
  {
    this.children = children;
  }
  
}
