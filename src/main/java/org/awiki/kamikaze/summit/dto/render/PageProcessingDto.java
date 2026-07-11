package org.awiki.kamikaze.summit.dto.render;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;


/**
* This DTO is mapped from PageProcessing domain, it stores the request processing details of a particular summit page.
* E.g. you could have page processing function written in SQL that inserts values
* into a database table.
*/
public class PageProcessingDto {
  
  private Long               id;
  
  private PageDto               page;
  
  @NotBlank(message = "Must not be empty.")
  private String                codeProcessingType;
  
  @NotBlank(message = "Must not be empty.")
  private Long                  processingNum;
  
  private String                successMessage; // APEX "process success message", shown after the post-POST redirect

  private List<PageProcessingSourceDto> pageProcessingSourceDtos = new ArrayList<>();

  private ConditionalDto conditionalDto = null; // the default.
  
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
  public String getSuccessMessage() {
    return successMessage;
  }
  public void setSuccessMessage(String successMessage) {
    this.successMessage = successMessage;
  }
  public List<PageProcessingSourceDto> getPageProcessingSource() {
    return pageProcessingSourceDtos;
  }
  public void setPageProcessingSource(List<PageProcessingSourceDto> pageProcessingSourceDtos) {
    this.pageProcessingSourceDtos = pageProcessingSourceDtos;
  }
  public ConditionalDto getConditional() {
    return conditionalDto;
  }
  public void setConditional(ConditionalDto conditionalDto)  {
    this.conditionalDto = conditionalDto;
  }
  
}
