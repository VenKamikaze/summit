package org.awiki.kamikaze.summit.dto.entry;

import org.hibernate.validator.constraints.NotBlank;


public class PageRegionDto {

  private Long    id;
  private PageDto    pageDto;
  private RegionDto  regionDto;
  
  @NotBlank(message = "Must not be empty.")
  private Long    regionNum;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public PageDto getPageDto() {
    return pageDto;
  }
  public void setPageDto(PageDto pageDto) {
    this.pageDto = pageDto;
  }
  public RegionDto getRegionDto() {
    return regionDto;
  }
  public void setRegionDto(RegionDto regionDto) {
    this.regionDto = regionDto;
  }
  public Long getRegionNum() {
    return regionNum;
  }
  public void setRegionNum(Long regionNum) {
    this.regionNum = regionNum;
  }
  
  
}
