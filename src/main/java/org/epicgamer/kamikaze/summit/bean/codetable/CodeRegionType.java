package org.epicgamer.kamikaze.summit.bean.codetable;
// default package
// Generated Sep 14, 2013 6:38:07 PM by Hibernate Tools 3.4.0.CR1

// Generated Sep 14, 2013 6:38:07 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

/**
 * CodeRegionType generated by hbm2java
 */
public class CodeRegionType implements java.io.Serializable
{

  private String  code;
  private String  description;
  private Integer sortOrder;
  private Set     regions = new HashSet(0);

  public CodeRegionType()
  {
  }

  public CodeRegionType(String code, String description)
  {
    this.code = code;
    this.description = description;
  }

  public CodeRegionType(String code, String description, Integer sortOrder,
      Set regions)
  {
    this.code = code;
    this.description = description;
    this.sortOrder = sortOrder;
    this.regions = regions;
  }

  public String getCode()
  {
    return this.code;
  }

  public void setCode(String code)
  {
    this.code = code;
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public Integer getSortOrder()
  {
    return this.sortOrder;
  }

  public void setSortOrder(Integer sortOrder)
  {
    this.sortOrder = sortOrder;
  }

  public Set getRegions()
  {
    return this.regions;
  }

  public void setRegions(Set regions)
  {
    this.regions = regions;
  }

}
