package org.awiki.kamikaze.summit.dto.entry;

import org.hibernate.validator.constraints.NotBlank;


public class PageRegionDto {

  private Integer    id;
  private PageDto    pageDto;
  private RegionDto  regionDto;
  
  @NotBlank(message = "Must not be empty.")
  private Integer    regionNum;
  
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
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
  public Integer getRegionNum() {
    return regionNum;
  }
  public void setRegionNum(Integer regionNum) {
    this.regionNum = regionNum;
  }
  
  
}
