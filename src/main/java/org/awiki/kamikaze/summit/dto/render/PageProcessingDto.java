package org.awiki.kamikaze.summit.dto.render;

import org.hibernate.validator.constraints.NotBlank;


/**
* This DTO is mapped from PageProcessing domain, it stores the request processing details of a particular summit page.
* E.g. you could have page processing function written in SQL that inserts values
* into a database table.
*/
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
