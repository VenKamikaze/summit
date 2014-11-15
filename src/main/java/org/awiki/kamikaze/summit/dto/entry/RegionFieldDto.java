package org.awiki.kamikaze.summit.dto.entry;


public class RegionFieldDto {

  private Long      id;
  private FieldDto     field;
  private RegionDto    region;
  private Long      fieldNum;
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
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
  public Long getFieldNum() {
    return fieldNum;
  }
  public void setFieldNum(Long fieldNum) {
    this.fieldNum = fieldNum;
  }
  
  
}
