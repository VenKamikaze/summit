// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ApplPageRegField generated by hbm2java
 */
@Entity
@Table(name = "appl_page_reg_field", schema = "public")
public class ApplPageRegField implements java.io.Serializable
{

  private ApplPageRegFieldId id;

  public ApplPageRegField()
  {
  }

  public ApplPageRegField(ApplPageRegFieldId id)
  {
    this.id = id;
  }

  @EmbeddedId
  @AttributeOverrides({
      @AttributeOverride(name = "appId", column = @Column(name = "app_id")),
      @AttributeOverride(name = "applicationNum", column = @Column(name = "application_num")),
      @AttributeOverride(name = "pageId", column = @Column(name = "page_id")),
      @AttributeOverride(name = "pageNum", column = @Column(name = "page_num")),
      @AttributeOverride(name = "regionId", column = @Column(name = "region_id")),
      @AttributeOverride(name = "regionNum", column = @Column(name = "region_num")),
      @AttributeOverride(name = "regionPosition", column = @Column(name = "region_position", length = 10)),
      @AttributeOverride(name = "regionType", column = @Column(name = "region_type", length = 10)),
      @AttributeOverride(name = "name", column = @Column(name = "name", length = 1000)),
      @AttributeOverride(name = "fieldId", column = @Column(name = "field_id")),
      @AttributeOverride(name = "fieldNum", column = @Column(name = "field_num")),
      @AttributeOverride(name = "fieldType", column = @Column(name = "field_type", length = 10)),
      @AttributeOverride(name = "fieldSourceType", column = @Column(name = "field_source_type", length = 10)),
      @AttributeOverride(name = "fieldSource", column = @Column(name = "field_source", length = 10000)) })
  public ApplPageRegFieldId getId()
  {
    return this.id;
  }

  public void setId(ApplPageRegFieldId id)
  {
    this.id = id;
  }

}
