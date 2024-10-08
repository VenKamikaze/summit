package org.awiki.kamikaze.summit.domain.codetable;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * CodeFieldType generated by hbm2java
 */
@Entity
@Table(name = "code_field_type")
public class CodeFieldType implements java.io.Serializable, CodeTable
{

  /**
   * 
   */
  private static final long serialVersionUID = 567980658183817207L;
  
  private String     code;
  private String     description;
  private Long       sortOrder;
  //private Set<Field> fields = new HashSet<Field>(0);

  public CodeFieldType()
  {
  }

  public CodeFieldType(String code, String description)
  {
    this.code = code;
    this.description = description;
  }

  public CodeFieldType(String code, String description, Long sortOrder
      /*,Set<Field> fields*/)
  {
    this.code = code;
    this.description = description;
    this.sortOrder = sortOrder;
    //this.fields = fields;
  }

  @Id
  @Column(name = "code", unique = true, nullable = false, length = 10)
  public String getCode()
  {
    return this.code;
  }

  public void setCode(String code)
  {
    this.code = code;
  }

  @Column(name = "description", nullable = false)
  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @Column(name = "sort_order")
  public Long getSortOrder()
  {
    return this.sortOrder;
  }

  public void setSortOrder(Long sortOrder)
  {
    this.sortOrder = sortOrder;
  }
/*
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "codeFieldType")
  public Set<Field> getFields()
  {
    return this.fields;
  }

  public void setFields(Set<Field> fields)
  {
    this.fields = fields;
  }
*/
}
