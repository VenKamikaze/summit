package org.awiki.kamikaze.summit.dto.entry;


public class RegionFieldDto {

  private Integer      id;
  private FieldDto     field;
  private RegionDto    region;
  private Integer      fieldNum;
  
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  public FieldDto getField() {
    return field;
  }
  public void setField(FieldDto field) {
    this.field = field;
  }
  public RegionDto getRegion() {
    return region;
  }
  public void setRegion(RegionDto region) {
    this.region = region;
  }
  public Integer getFieldNum() {
    return fieldNum;
  }
  public void setFieldNum(Integer fieldNum) {
    this.fieldNum = fieldNum;
  }
  
  
}
