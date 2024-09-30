package org.awiki.kamikaze.summit.dto.render;

import org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResult;



/**
* This DTO contains field metadata (name, index) that we wish to select values into, from the select query specified in the pageProcessingSource
*/
public class PageProcessingSourceSelectDto {
  
  private long                      id;
  private PageProcessingSourceDto   pageProcessingSource;
  private long                      fieldIndex;
  private String                    fieldName;
  private SourceProcessorResult     fieldValue;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public PageProcessingSourceDto getPageProcessingSource() {
    return pageProcessingSource;
  }
  public void setPageProcessingSource(PageProcessingSourceDto pageProcessingSource) {
    this.pageProcessingSource = pageProcessingSource;
  }
  public long getFieldIndex() {
    return fieldIndex;
  }
  public void setFieldIndex(long fieldIndex) {
    this.fieldIndex = fieldIndex;
  }
  public String getFieldName() {
    return fieldName;
  }
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
  public SourceProcessorResult getFieldValue()  {
    return fieldValue;
  }
  public void setFieldValue(SourceProcessorResult fieldValue)  {
    this.fieldValue = fieldValue;
  }
}
