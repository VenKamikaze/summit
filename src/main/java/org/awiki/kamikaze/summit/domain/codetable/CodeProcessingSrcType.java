package org.awiki.kamikaze.summit.domain.codetable;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.awiki.kamikaze.summit.domain.PageProcessing;

/**
 * CodeProcessingSrcType generated by hbm2java
 */
@Entity
@Table(name = "code_processing_src_type", schema = "public")
public class CodeProcessingSrcType implements java.io.Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -229645634857873993L;
  
  private String              code;
  private String              description;
  private Integer             sortOrder;
  private CodeSourceType      codeSourceProcessor;  // CODE_SOURCE_CODE.CODE
  //private Set<PageProcessing> pageProcessings = new HashSet<PageProcessing>(0);

  public CodeProcessingSrcType()
  {
  }

  public CodeProcessingSrcType(String code, String description)
  {
    this.code = code;
    this.description = description;
  }

  public CodeProcessingSrcType(String code, String description,
      Integer sortOrder, CodeSourceType codeSourceProcessor)
  {
    this.code = code;
    this.description = description;
    this.sortOrder = sortOrder;
    this.codeSourceProcessor = codeSourceProcessor;
    //this.pageProcessings = pageProcessings;
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
  public Integer getSortOrder()
  {
    return this.sortOrder;
  }

  public void setSortOrder(Integer sortOrder)
  {
    this.sortOrder = sortOrder;
  }

  @ManyToOne(fetch=FetchType.EAGER)
  @Column(name = "code_source_processor")
  public CodeSourceType getCodeSourceProcessor() {
    return codeSourceProcessor;
  }

  public void setCodeSourceProcessor(CodeSourceType codeSourceType) {
    this.codeSourceProcessor = codeSourceType;
  }

  
/*
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "codeProcessingSrcType")
  public Set<PageProcessing> getPageProcessings()
  {
    return this.pageProcessings;
  }

  public void setPageProcessings(Set<PageProcessing> pageProcessings)
  {
    this.pageProcessings = pageProcessings;
  }
*/
}
