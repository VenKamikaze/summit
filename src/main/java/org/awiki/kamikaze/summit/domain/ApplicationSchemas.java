package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * ApplicationSchemas generated by hbm2java
 */
@Entity
@Table(name = "APPLICATION_SCHEMAS", uniqueConstraints = @UniqueConstraint(columnNames = {
    "APPLICATION_ID", "SCHEMA_NAME" }))
public class ApplicationSchemas implements java.io.Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -7266889595101604635L;
  
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
      @AttributeOverride(name = "applicationId", column = @Column(name = "APPLICATION_ID", nullable = false)),
      @AttributeOverride(name = "schemaName", column = @Column(name = "SCHEMA_NAME", nullable = false, length = 100)) })
  public ApplicationSchemasId getId()
  {
    return this.id;
  }

  public void setId(ApplicationSchemasId id)
  {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "APPLICATION_ID", nullable = false, insertable = false, updatable = false)
  public Application getApplication()
  {
    return this.application;
  }

  public void setApplication(Application application)
  {
    this.application = application;
  }

}
