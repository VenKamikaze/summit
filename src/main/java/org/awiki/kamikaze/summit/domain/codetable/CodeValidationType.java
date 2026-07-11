package org.awiki.kamikaze.summit.domain.codetable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Validation types for page validations (run on POST before POST1 processing).
 * NOT_NULL checks the submitted field value directly; the source-based types
 * evaluate the validation's source with the same semantics as conditionals.
 */
@Entity
@Table(name = "CODE_VALIDATION_TYPE")
public class CodeValidationType implements java.io.Serializable, CodeTable
{
  public static final String CODE_VALIDATION_NOT_NULL = "NOT_NULL";
  public static final String CODE_VALIDATION_TEXT_TRUE = "TEXT_TRUE";
  public static final String CODE_VALIDATION_EXISTS = "EXISTS";
  public static final String CODE_VALIDATION_NOT_EXISTS = "NOTEXISTS";

  private static final long serialVersionUID = -7248112203441563219L;

  private String              code;
  private String              description;
  private Long                sortOrder;

  public CodeValidationType()
  {
  }

  public CodeValidationType(String code, String description, Long sortOrder)
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
