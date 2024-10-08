package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * Page generated by hbm2java
 */
@Entity
@Table(name = "PAGE")
public class Page implements java.io.Serializable
{
  private static final long serialVersionUID = -2500329708942281643L;
  
  private long                  id;
  private Set<PageRegion>       pageRegions      = new LinkedHashSet<PageRegion>(0);
  private List<PageProcessing>  pageProcessings  = new ArrayList<PageProcessing>(0);
  private Set<ApplicationPage>  applicationPages = new HashSet<ApplicationPage>(0);

  //changed definition
  private String               name;
  private Template             template;
  
  public Page()
  {
  }

  public Page(int id)
  {
    this.id = id;
  }

  public Page(long id, final String name, Set<PageRegion> pageRegions,
          List<PageProcessing> pageProcessings, Set<ApplicationPage> applicationPages)
  {
    this.id = id;
    this.name = name;
    this.pageRegions = pageRegions;
    this.pageProcessings = pageProcessings;
    this.applicationPages = applicationPages;
  }

  @Id
  @Column(name = "ID", unique = true, nullable = false)
  public long getId()
  {
    return this.id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "page")
  @OrderBy(value="regionNum")
  public Set<PageRegion> getPageRegions()
  {
    return this.pageRegions;
  }

  public void setPageRegions(Set<PageRegion> pageRegions)
  {
    this.pageRegions = pageRegions;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "page")
  @OrderBy(value="processingNum")
  public List<PageProcessing> getPageProcessings()
  {
    return this.pageProcessings;
  }

  public void setPageProcessings(List<PageProcessing> pageProcessings)
  {
    this.pageProcessings = pageProcessings;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "page")
  public Set<ApplicationPage> getApplicationPages()
  {
    return this.applicationPages;
  }

  public void setApplicationPages(Set<ApplicationPage> applicationPages)
  {
    this.applicationPages = applicationPages;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "TEMPLATE_ID", nullable = false)
  public Template getTemplate() {
    return template;
  }

  public void setTemplate(Template template) {
    this.template = template;
  }

}
