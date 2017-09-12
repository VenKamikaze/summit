package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CONDITIONAL")
public class Conditional implements java.io.Serializable
{
  private static final long serialVersionUID = -863872452L;
  
  private long     id;
  private Source   source = null;

  public Conditional()
  {
  }

  public Conditional(long id, Source source)
  {
    this.id = id;
    this.source = source;
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

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "SOURCE_ID")
  public Source getSource()
  {
    return this.source;
  }

  public void setSource(Source source)
  {
    this.source = source;
  }

}
