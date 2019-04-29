package org.awiki.kamikaze.summit.dto.edit;

import org.hibernate.validator.constraints.NotBlank;



public class EditPageRegionDto {

  private Long    id;
  private EditPageDto    pageDto;
  private EditRegionDto  regionDto;
  
  @NotBlank(message = "Must not be empty.")
  private Long    regionNum;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public EditPageDto getPageDto() {
    return pageDto;
  }
  public void setPageDto(EditPageDto pageDto) {
    this.pageDto = pageDto;
  }
  public EditRegionDto getRegionDto() {
    return regionDto;
  }
  public void setRegionDto(EditRegionDto regionDto) {
    this.regionDto = regionDto;
  }
  public Long getRegionNum() {
    return regionNum;
  }
  public void setRegionNum(Long regionNum) {
    this.regionNum = regionNum;
  }
  
  
}
