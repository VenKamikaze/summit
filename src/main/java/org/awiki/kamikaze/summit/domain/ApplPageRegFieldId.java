package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * ApplPageRegFieldId generated by hbm2java
 */
@Embeddable
public class ApplPageRegFieldId implements java.io.Serializable
{

  private Integer appId;
  private Integer applicationNum;
  private Integer pageId;
  private Integer pageNum;
  private Integer regionId;
  private Integer regionNum;
  private String  regionPosition;
  private String  regionType;
  private String  name;
  private Integer fieldId;
  private Integer fieldNum;
  private String  fieldType;
  private String  fieldSourceType;
  private String  fieldSource;

  public ApplPageRegFieldId()
  {
  }

  public ApplPageRegFieldId(Integer appId, Integer applicationNum,
      Integer pageId, Integer pageNum, Integer regionId, Integer regionNum,
      String regionPosition, String regionType, String name, Integer fieldId,
      Integer fieldNum, String fieldType, String fieldSourceType,
      String fieldSource)
  {
    this.appId = appId;
    this.applicationNum = applicationNum;
    this.pageId = pageId;
    this.pageNum = pageNum;
    this.regionId = regionId;
    this.regionNum = regionNum;
    this.regionPosition = regionPosition;
    this.regionType = regionType;
    this.name = name;
    this.fieldId = fieldId;
    this.fieldNum = fieldNum;
    this.fieldType = fieldType;
    this.fieldSourceType = fieldSourceType;
    this.fieldSource = fieldSource;
  }

  @Column(name = "app_id")
  public Integer getAppId()
  {
    return this.appId;
  }

  public void setAppId(Integer appId)
  {
    this.appId = appId;
  }

  @Column(name = "application_num")
  public Integer getApplicationNum()
  {
    return this.applicationNum;
  }

  public void setApplicationNum(Integer applicationNum)
  {
    this.applicationNum = applicationNum;
  }

  @Column(name = "page_id")
  public Integer getPageId()
  {
    return this.pageId;
  }

  public void setPageId(Integer pageId)
  {
    this.pageId = pageId;
  }

  @Column(name = "page_num")
  public Integer getPageNum()
  {
    return this.pageNum;
  }

  public void setPageNum(Integer pageNum)
  {
    this.pageNum = pageNum;
  }

  @Column(name = "region_id")
  public Integer getRegionId()
  {
    return this.regionId;
  }

  public void setRegionId(Integer regionId)
  {
    this.regionId = regionId;
  }

  @Column(name = "region_num")
  public Integer getRegionNum()
  {
    return this.regionNum;
  }

  public void setRegionNum(Integer regionNum)
  {
    this.regionNum = regionNum;
  }

  @Column(name = "region_position", length = 10)
  public String getRegionPosition()
  {
    return this.regionPosition;
  }

  public void setRegionPosition(String regionPosition)
  {
    this.regionPosition = regionPosition;
  }

  @Column(name = "region_type", length = 10)
  public String getRegionType()
  {
    return this.regionType;
  }

  public void setRegionType(String regionType)
  {
    this.regionType = regionType;
  }

  @Column(name = "name", length = 1000)
  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @Column(name = "field_id")
  public Integer getFieldId()
  {
    return this.fieldId;
  }

  public void setFieldId(Integer fieldId)
  {
    this.fieldId = fieldId;
  }

  @Column(name = "field_num")
  public Integer getFieldNum()
  {
    return this.fieldNum;
  }

  public void setFieldNum(Integer fieldNum)
  {
    this.fieldNum = fieldNum;
  }

  @Column(name = "field_type", length = 10)
  public String getFieldType()
  {
    return this.fieldType;
  }

  public void setFieldType(String fieldType)
  {
    this.fieldType = fieldType;
  }

  @Column(name = "field_source_type", length = 10)
  public String getFieldSourceType()
  {
    return this.fieldSourceType;
  }

  public void setFieldSourceType(String fieldSourceType)
  {
    this.fieldSourceType = fieldSourceType;
  }

  @Column(name = "field_source", length = 10000)
  public String getFieldSource()
  {
    return this.fieldSource;
  }

  public void setFieldSource(String fieldSource)
  {
    this.fieldSource = fieldSource;
  }

  public boolean equals(Object other)
  {
    if ((this == other))
      return true;
    if ((other == null))
      return false;
    if (!(other instanceof ApplPageRegFieldId))
      return false;
    ApplPageRegFieldId castOther = (ApplPageRegFieldId) other;

    return ((this.getAppId() == castOther.getAppId()) || (this.getAppId() != null
        && castOther.getAppId() != null && this.getAppId().equals(
        castOther.getAppId())))
        && ((this.getApplicationNum() == castOther.getApplicationNum()) || (this
            .getApplicationNum() != null
            && castOther.getApplicationNum() != null && this
            .getApplicationNum().equals(castOther.getApplicationNum())))
        && ((this.getPageId() == castOther.getPageId()) || (this.getPageId() != null
            && castOther.getPageId() != null && this.getPageId().equals(
            castOther.getPageId())))
        && ((this.getPageNum() == castOther.getPageNum()) || (this.getPageNum() != null
            && castOther.getPageNum() != null && this.getPageNum().equals(
            castOther.getPageNum())))
        && ((this.getRegionId() == castOther.getRegionId()) || (this
            .getRegionId() != null && castOther.getRegionId() != null && this
            .getRegionId().equals(castOther.getRegionId())))
        && ((this.getRegionNum() == castOther.getRegionNum()) || (this
            .getRegionNum() != null && castOther.getRegionNum() != null && this
            .getRegionNum().equals(castOther.getRegionNum())))
        && ((this.getRegionPosition() == castOther.getRegionPosition()) || (this
            .getRegionPosition() != null
            && castOther.getRegionPosition() != null && this
            .getRegionPosition().equals(castOther.getRegionPosition())))
        && ((this.getRegionType() == castOther.getRegionType()) || (this
            .getRegionType() != null && castOther.getRegionType() != null && this
            .getRegionType().equals(castOther.getRegionType())))
        && ((this.getName() == castOther.getName()) || (this.getName() != null
            && castOther.getName() != null && this.getName().equals(
            castOther.getName())))
        && ((this.getFieldId() == castOther.getFieldId()) || (this.getFieldId() != null
            && castOther.getFieldId() != null && this.getFieldId().equals(
            castOther.getFieldId())))
        && ((this.getFieldNum() == castOther.getFieldNum()) || (this
            .getFieldNum() != null && castOther.getFieldNum() != null && this
            .getFieldNum().equals(castOther.getFieldNum())))
        && ((this.getFieldType() == castOther.getFieldType()) || (this
            .getFieldType() != null && castOther.getFieldType() != null && this
            .getFieldType().equals(castOther.getFieldType())))
        && ((this.getFieldSourceType() == castOther.getFieldSourceType()) || (this
            .getFieldSourceType() != null
            && castOther.getFieldSourceType() != null && this
            .getFieldSourceType().equals(castOther.getFieldSourceType())))
        && ((this.getFieldSource() == castOther.getFieldSource()) || (this
            .getFieldSource() != null && castOther.getFieldSource() != null && this
            .getFieldSource().equals(castOther.getFieldSource())));
  }

  public int hashCode()
  {
    int result = 17;

    result = 37 * result
        + (getAppId() == null ? 0 : this.getAppId().hashCode());
    result = 37
        * result
        + (getApplicationNum() == null ? 0 : this.getApplicationNum()
            .hashCode());
    result = 37 * result
        + (getPageId() == null ? 0 : this.getPageId().hashCode());
    result = 37 * result
        + (getPageNum() == null ? 0 : this.getPageNum().hashCode());
    result = 37 * result
        + (getRegionId() == null ? 0 : this.getRegionId().hashCode());
    result = 37 * result
        + (getRegionNum() == null ? 0 : this.getRegionNum().hashCode());
    result = 37
        * result
        + (getRegionPosition() == null ? 0 : this.getRegionPosition()
            .hashCode());
    result = 37 * result
        + (getRegionType() == null ? 0 : this.getRegionType().hashCode());
    result = 37 * result + (getName() == null ? 0 : this.getName().hashCode());
    result = 37 * result
        + (getFieldId() == null ? 0 : this.getFieldId().hashCode());
    result = 37 * result
        + (getFieldNum() == null ? 0 : this.getFieldNum().hashCode());
    result = 37 * result
        + (getFieldType() == null ? 0 : this.getFieldType().hashCode());
    result = 37
        * result
        + (getFieldSourceType() == null ? 0 : this.getFieldSourceType()
            .hashCode());
    result = 37 * result
        + (getFieldSource() == null ? 0 : this.getFieldSource().hashCode());
    return result;
  }

}