package org.awiki.kamikaze.summit.domain.codetable;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CodeProcessingType generated by hbm2java
 */
@Entity
@Table(name = "CODE_CONDITIONAL_TYPE")
public class CodeConditionalType implements java.io.Serializable, CodeTable
{
  public static final String CODE_CONDITIONAL_TEXT_TRUE = "TEXT_TRUE";
  public static final String CODE_CONDITIONAL_EXISTS = "EXISTS";
  
  public static final String CODE_CONDITIONAL_TEXT_TRUE_VALUE = "true"; // TEXT_TRUE codes should return exactly this value if true.
  
  private static final long serialVersionUID = -76485377820160L;
  
  private String              code;
  private String              description;
  private Long                sortOrder;
  
  public CodeConditionalType()
  {
  }

  public CodeConditionalType(String code, String description)
  {
    this.code = code;
    this.description = description;
  }

  public CodeConditionalType(String code, String description, Long sortOrder)
  {
    this.code = code;
    this.description = description;
    this.sortOrder = sortOrder;
  }

  @Id
  @Column(name = "CODE", unique = true, nullable = false, length = 10)
  public String getCode()
  {
    return this.code;
  }

  public void setCode(String code)
  {
    this.code = code;
  }

  @Column(name = "DESCRIPTION", nullable = false)
  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @Column(name = "SORT_ORDER")
  public Long getSortOrder()
  {
    return this.sortOrder;
  }

  public void setSortOrder(Long sortOrder)
  {
    this.sortOrder = sortOrder;
  }
  
}
