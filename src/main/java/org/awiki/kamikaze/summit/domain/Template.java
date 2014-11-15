package org.awiki.kamikaze.summit.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "template", schema = "public")
public class Template  implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6184401135662692768L;
  
  private long       id;
  private String     name;
  private String     headerSource;
  private String     bodySource;
  private String     footerSource;
  
  private Set<Page>  pages = new HashSet<>(0);

  public Template()
  {
  }

  public Template(long id, final String name, final String headerSource,
      final String bodySource,final String footerSource)
  {
    this.id = id;
    this.name = name;
    this.headerSource = headerSource;
    this.bodySource = bodySource;
    this.footerSource = footerSource;
  }

  public Template(long id, final String name, final String headerSource,
      final String bodySource,final String footerSource, Set<Page> pages)
  {
    this(id, name, headerSource, bodySource, footerSource);
    this.pages = pages;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "header_source", nullable = true)
  public String getHeaderSource()
  {
    return this.headerSource;
  }

  public void setHeaderSource(final String headerSource)
  {
    this.headerSource = headerSource;
  }

  @Column(name = "body_source", nullable = true)
  public String getBodySource()
  {
    return this.bodySource;
  }

  public void setBodySource(final String bodySource)
  {
    this.bodySource = bodySource;
  }

  @Column(name = "footer_source", nullable = true)
  public String getFooterSource()
  {
    return this.footerSource;
  }

  public void setFooterSource(final String footerSource)
  {
    this.footerSource = footerSource;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "template")
  public Set<Page> getPages()
  {
    return this.pages;
  }

}
