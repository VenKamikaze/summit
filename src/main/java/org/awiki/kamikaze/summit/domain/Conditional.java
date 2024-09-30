package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.awiki.kamikaze.summit.domain.codetable.CodeConditionalType;
import org.awiki.kamikaze.summit.domain.codetable.CodeSourceType;

@Entity
@Table(name = "CONDITIONAL")
public class Conditional implements java.io.Serializable
{
  private static final long serialVersionUID = -863872452L;
  
  private long     id;
  private Source   source = null;
  
  private CodeSourceType codeSourceType;
  private CodeConditionalType codeConditionalType;

  public Conditional()
  {
  }

  public Conditional(long id, Source source)
  {
    this.id = id;
    this.source = source;
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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "SOURCE_TYPE_CODE", nullable = false)
  public CodeSourceType getSourceTypeCode()
  {
    return this.codeSourceType;
  }

  public void setSourceTypeCode(
          CodeSourceType codeSourceType)
  {
    this.codeSourceType = codeSourceType;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "CONDITIONAL_TYPE_CODE", nullable = false)
  public CodeConditionalType getCodeConditionalType()
  {
    return codeConditionalType;
  }

  public void setCodeConditionalType(CodeConditionalType codeConditionalType)
  {
    this.codeConditionalType = codeConditionalType;
  }
}
