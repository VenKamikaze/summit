package org.awiki.kamikaze.summit.dto.entry;

import org.hibernate.validator.constraints.NotBlank;


public class PageProcessingDto {
  
  private Long               id;
  
  //@NotBlank(message = "Must not be empty.")
  //private String                codeProcessingSrcType;
  
  private PageDto               page;
  
  @NotBlank(message = "Must not be empty.")
  private String                codeProcessingType;
  
  @NotBlank(message = "Must not be empty.")
  private Long               processingNum;
  
  private String                source;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public PageDto getPage() {
    return page;
  }
  public void setPage(PageDto page) {
    this.page = page;
  }
  public String getCodeProcessingType() {
    return codeProcessingType;
  }
  public void setCodeProcessingType(String codeProcessingType) {
    this.codeProcessingType = codeProcessingType;
  }
  public Long getProcessingNum() {
    return processingNum;
  }
  public void setProcessingNum(Long processingNum) {
    this.processingNum = processingNum;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  
}
