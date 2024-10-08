package org.awiki.kamikaze.summit.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "TEMPLATE")
public class Template  implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6184401135662692768L;
  
  private long       id;
  private String     className;
  private String     source;
  private String     mimeType;
  
  private Template   parent; // associated parent template (self-referencing join)
  private Set<Template> children = new HashSet<>(0); // associated child templates (self-referencing join)
  
  //private Set<Page>  pages = new HashSet<>(0);

  public Template()
  {
  }

  public Template(long id, final String name,final String bodySource, final String mimeType,Template parent, Set<Template> children)
  {
    this.id = id;
    this.parent = parent;
    this.className = name;
    this.source = bodySource;
    this.mimeType = mimeType;
    this.children = children;
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


  //@Lob
  //@Type(type="org.hibernate.type.MaterializedClobType")
  @Column(name = "SOURCE", nullable = true)
  public String getSource()
  {
    return this.source;
  }

  public void setSource(final String bodySource)
  {
    this.source = bodySource;
  }

  @Column(name = "mime_type", nullable = true)
  public String getMimeType()
  {
    return mimeType;
  }

  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", nullable = false)
  public Template getParent() {
    return parent;
  }

  public void setParent(Template parent) {
    this.parent = parent;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
  public Set<Template> getChildren()
  {
    return children;
  }

  public void setChildren(Set<Template> children)
  {
    this.children = children;
  }



}
