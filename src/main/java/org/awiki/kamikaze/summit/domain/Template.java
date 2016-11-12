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
  private String     className;
  private String     source;
  
  //private Set<Page>  pages = new HashSet<>(0);

  public Template()
  {
  }

  public Template(long id, final String name,final String bodySource)
  {
    this.id = id;
    this.className = name;
    this.source = bodySource;
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

  @Column(name = "class_name", nullable = true)
  public String getClassName() {
    return className;
  }

  public void setClassName(String name) {
    this.className = name;
  }


  @Column(name = "source", nullable = true)
  public String getSource()
  {
    return this.source;
  }

  public void setSource(final String bodySource)
  {
    this.source = bodySource;
  }


}
