package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * PageRegion generated by hbm2java
 */
@Entity
@Table(name = "page_region", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "page_id", "region_id" }),
    @UniqueConstraint(columnNames = { "page_id", "region_num" }) })
public class PageRegion implements java.io.Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 7071227548283247644L;
  
  private long    id;
  private Page   page;
  private Region region;
  private long    regionNum;

  public PageRegion()
  {
  }

  public PageRegion(long id, Page page, Region region, long regionNum)
  {
    this.id = id;
    this.page = page;
    this.region = region;
    this.regionNum = regionNum;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  public long getId()
  {
    return this.id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "page_id", nullable = false)
  public Page getPage()
  {
    return this.page;
  }

  public void setPage(Page page)
  {
    this.page = page;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  public Region getRegion()
  {
    return this.region;
  }

  public void setRegion(Region region)
  {
    this.region = region;
  }

  @Column(name = "region_num", nullable = false)
  public long getRegionNum()
  {
    return this.regionNum;
  }

  public void setRegionNum(long regionNum)
  {
    this.regionNum = regionNum;
  }

}
