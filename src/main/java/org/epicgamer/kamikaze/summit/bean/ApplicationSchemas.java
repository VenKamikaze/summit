// default package
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * ApplicationSchemas generated by hbm2java
 */
@Entity
@Table(name = "application_schemas", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
    "application_id", "schema_name" }))
public class ApplicationSchemas implements java.io.Serializable
{

  private ApplicationSchemasId id;
  private Application          application;

  public ApplicationSchemas()
  {
  }

  public ApplicationSchemas(ApplicationSchemasId id, Application application)
  {
    this.id = id;
    this.application = application;
  }

  @EmbeddedId
  @AttributeOverrides({
      @AttributeOverride(name = "applicationId", column = @Column(name = "application_id", nullable = false)),
      @AttributeOverride(name = "schemaName", column = @Column(name = "schema_name", nullable = false, length = 100)) })
  public ApplicationSchemasId getId()
  {
    return this.id;
  }

  public void setId(ApplicationSchemasId id)
  {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
  public Application getApplication()
  {
    return this.application;
  }

  public void setApplication(Application application)
  {
    this.application = application;
  }

}
