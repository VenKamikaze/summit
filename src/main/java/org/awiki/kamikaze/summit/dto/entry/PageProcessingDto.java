package org.awiki.kamikaze.summit.dto.entry;

import org.hibernate.validator.constraints.NotBlank;


public class PageProcessingDto {
  
  private Integer               id;
  
  @NotBlank(message = "Must not be empty.")
  private String                codeProcessingSrcType;
  
  private PageDto               page;
  
  @NotBlank(message = "Must not be empty.")
  private String                codeProcessingType;
  
  @NotBlank(message = "Must not be empty.")
  private Integer               processingNum;
  
  private String                source;
  
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  public String getCodeProcessingSrcType() {
    return codeProcessingSrcType;
  }
  public void setCodeProcessingSrcType(String codeProcessingSrcType) {
    this.codeProcessingSrcType = codeProcessingSrcType;
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
  public Integer getProcessingNum() {
    return processingNum;
  }
  public void setProcessingNum(Integer processingNum) {
    this.processingNum = processingNum;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  
}
