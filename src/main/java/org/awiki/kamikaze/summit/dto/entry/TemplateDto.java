package org.awiki.kamikaze.summit.dto.entry;

import java.util.HashSet;
import java.util.Set;

public class TemplateDto {

  private long       id;
  private String     name;
  private String     headerSource;
  private String     bodySource;
  private String     footerSource;
  
  private Set<PageDto> pages = new HashSet<>(0);
  
  private Set<TemplateForListsDto> listTemplates = new HashSet<>(0);

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHeaderSource() {
    return headerSource;
  }

  public void setHeaderSource(String headerSource) {
    this.headerSource = headerSource;
  }

  public String getBodySource() {
    return bodySource;
  }

  public void setBodySource(String bodySource) {
    this.bodySource = bodySource;
  }

  public String getFooterSource() {
    return footerSource;
  }

  public void setFooterSource(String footerSource) {
    this.footerSource = footerSource;
  }

  public Set<PageDto> getPages() {
    return pages;
  }

  public void setPages(Set<PageDto> pages) {
    this.pages = pages;
  }
  
  public Set<TemplateForListsDto> getTemplatesForLists() {
    return listTemplates;
  }

  public void setTemplatesForLists(Set<TemplateForListsDto> listTemplates) {
    this.listTemplates = listTemplates;
  }
}
