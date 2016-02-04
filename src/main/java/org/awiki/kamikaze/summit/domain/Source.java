package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Field generated by hbm2java
 */
@Entity
@Table(name = "source", schema = "public")
public class Source implements java.io.Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -7419252566793513396L;
  
  private long                id;
  private String              source;
  private Set<Region>         regions = new HashSet<Region>(0);
  private Set<Field>          fields = new HashSet<Field>(0);
  private Set<PageProcessing> pageProcessings = new HashSet<PageProcessing>(0);
  
  public Source()
  {
  }

  public Source(long id, String  source)
  {
    this.id = id;
    this.source = source;
  }

  public Source(long id, String source, Set<Region> regions, Set<Field> fields)
  {
    this(id,source);
    this.regions = regions;
    this.fields = fields;
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

  @Column(name = "source", nullable = false, length = 32000)
  public String  getSource()
  {
    return this.source;
  }

  public void setSource( String source)
  {
    this.source = source;
  }
  
  
  @ManyToMany(mappedBy="source")
  public Set<Region> getRegions()
  {
    return regions;
  }

  public void setRegions(Set<Region> regions)
  {
    this.regions = regions;
  }
  
  
  @ManyToMany(mappedBy="source")
  public Set<Field> getFields()
  {
    return fields;
  }

  public void setFields(Set<Field> fields)
  {
    this.fields = fields;
  }

  
  @ManyToMany(mappedBy="source")
  public Set<PageProcessing> getPageProcessings()
  {
    return pageProcessings;
  }

  public void setPageProcessings(Set<PageProcessing> pageProcessings)
  {
    this.pageProcessings = pageProcessings;
  }
  
}
