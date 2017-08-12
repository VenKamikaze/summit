package org.awiki.kamikaze.summit.dto.render;

import java.util.ArrayList;
import java.util.List;


/**
* This DTO contains the source associated with a pageProcessing, and possibly the list of fields (PageProcessingSourceSelect items)
*   to auto-fill values with from this source.
*/
public class PageProcessingSourceDto {
  
  private Long                                id;
  private PageProcessingDto                   pageProcessing;
  private List<PageProcessingSourceSelectDto> pageProcessingSourceSelect = new ArrayList<>(); // fields to retrieve
  private String                              codeSourceType;
  private String                              source;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public PageProcessingDto getPageProcessing() {
    return pageProcessing;
  }
  public void setPageProcessing(PageProcessingDto page) {
    this.pageProcessing = page;
  }
  public List<PageProcessingSourceSelectDto> getPageProcessingSourceSelect() {
    return pageProcessingSourceSelect;
  }
  public void setPageProcessingSourceSelectDto(List<PageProcessingSourceSelectDto> pageProcessingSourceSelect) {
    this.pageProcessingSourceSelect = pageProcessingSourceSelect;
  }
  public String getCodeSourceType() {
    return codeSourceType;
  }
  public void setCodeSourceType(String codeSourceType) {
    this.codeSourceType = codeSourceType;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  
}
