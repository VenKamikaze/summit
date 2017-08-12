package org.awiki.kamikaze.summit.dto.render;



/**
* This DTO contains a collection of fields we wish to select values into, from the select query specified in the pageProcessingSource
*/
public class PageProcessingSourceSelectDto {
  
  private long                      id;
  private PageProcessingSourceDto   pageProcessingSource;
  private long                      fieldIndex;
  private String                    fieldName;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public PageProcessingSourceDto getPageProcessing() {
    return pageProcessingSource;
  }
  public void setPageProcessing(PageProcessingSourceDto pageProcessingSource) {
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
}
