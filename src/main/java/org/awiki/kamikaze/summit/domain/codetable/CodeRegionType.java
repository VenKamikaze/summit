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

import org.awiki.kamikaze.summit.domain.Region;

/**
 * CodeRegionType generated by hbm2java
 */
@Entity
@Table(name = "code_region_type", schema = "public")
public class CodeRegionType implements java.io.Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -6028843442767053142L;
  
  private String      code;
  private String      description;
  private Long        sortOrder;
  private CodeSourceType codeSourceType;
  //private Set<Region> regions = new HashSet<Region>(0);
  // xx // TODO changed definition
  
  public CodeRegionType()
  {
  }

  public CodeRegionType(String code, String description)
  {
    this.code = code;
    this.description = description;
  }

  public CodeRegionType(String code, String description, Long sortOrder, CodeSourceType codeSourceType 
      /*Set<Region> regions*/)
  {
    this.code = code;
    this.description = description;
    this.sortOrder = sortOrder;
    this.codeSourceType = codeSourceType;
    //this.regions = regions;
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
  
  @ManyToOne(fetch=FetchType.EAGER)
  @Column(name = "source_type_code")
  public CodeSourceType getCodeSourceType() {
    return codeSourceType;
  }

  public void setCodeSourceProcessor(CodeSourceType codeSourceType) {
    this.codeSourceType = codeSourceType;
  }
  
  
/*
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "codeRegionType")
  public Set<Region> getRegions()
  {
    return this.regions;
  }

  public void setRegions(Set<Region> regions)
  {
    this.regions = regions;
  }
*/
}
