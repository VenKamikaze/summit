package org.awiki.kamikaze.summit.domain.codetable;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * CodeProcessingType generated by hbm2java
 */
@Entity
@Table(name = "CODE_PROCESSING_TYPE")
public class CodeProcessingType implements java.io.Serializable, CodeTable
{
  public static final String CODE_PROCESS_BEFORE_REGIONS_1 = "RENDER_PG1";
  public static final String CODE_PROCESS_POST_1 = "POST1";
  public static final String CODE_PROCESS_POST_BRANCH_1 = "BRANCH1";
  
  private static final long serialVersionUID = -1417754221592950160L;
  
  private String              code;
  private String              description;
  private Long                sortOrder;
  
  public CodeProcessingType()
  {
  }

  public CodeProcessingType(String code, String description)
  {
    this.code = code;
    this.description = description;
  }

  public CodeProcessingType(String code, String description, Long sortOrder
      /*Set<PageProcessing> pageProcessings*/)
  {
    this.code = code;
    this.description = description;
    this.sortOrder = sortOrder;
    //this.pageProcessings = pageProcessings;
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
