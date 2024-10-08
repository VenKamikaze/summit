package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.awiki.kamikaze.summit.domain.codetable.CodeProcessingType;

/**
 * PageProcessing generated by hbm2java
 */
@Entity
@Table(name = "PAGE_PROCESSING")
public class PageProcessing implements java.io.Serializable
{

  /**
   * This domain stores the request processing details of a particular summit page.
   * E.g. you could have page processing function written in SQL that inserts values
   * into a database table.
   */
  private static final long serialVersionUID = -4307936142895462501L;
  
  private long                       id;
  private Page                       page;
  private CodeProcessingType         codeProcessingType;    // e.g. clear session state, DML, redirect to another page
  private long                       processingNum;         // in which order this processing occurs.
  private List<PageProcessingSource> pageProcessingSource = new ArrayList<>();
  
  private Conditional                conditional = null;

  public PageProcessing()
  {
  }

  public PageProcessing(long id, Page page, CodeProcessingType codeProcessingType, long processingNum)
  {
    this.id = id;
    //this.codeProcessingSrcType = codeProcessingSrcType;
    this.page = page;
    this.codeProcessingType = codeProcessingType;
    this.processingNum = processingNum;
  }

  public PageProcessing(long id,
      Page page, CodeProcessingType codeProcessingType, long processingNum,
      List<PageProcessingSource> pageProcessingSource)
  {
    this.id = id;
    this.page = page;
    this.codeProcessingType = codeProcessingType;
    this.processingNum = processingNum;
    this.pageProcessingSource = pageProcessingSource;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PAGE_ID", nullable = false)
  public Page getPage()
  {
    return this.page;
  }

  public void setPage(Page page)
  {
    this.page = page;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "PROCESSING_TYPE_CODE", nullable = false)
  public CodeProcessingType getCodeProcessingType()
  {
    return this.codeProcessingType;
  }

  public void setCodeProcessingType(CodeProcessingType codeProcessingType)
  {
    this.codeProcessingType = codeProcessingType;
  }

  @Column(name = "PROCESSING_NUM", nullable = false)
  public long getProcessingNum()
  {
    return this.processingNum;
  }

  public void setProcessingNum(long processingNum)
  {
    this.processingNum = processingNum;
  }

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "pageProcessing")
  public List<PageProcessingSource> getPageProcessingSource()
  {
    return this.pageProcessingSource;
  }

  public void setPageProcessingSource(List<PageProcessingSource> pageProcessingSource)
  {
    this.pageProcessingSource = pageProcessingSource;
  }

  @OneToOne
  @JoinTable(name = "PAGE_PROCESSING_CONDITIONAL",
          joinColumns = { @JoinColumn(name = "PAGE_PROCESSING_ID", referencedColumnName = "ID") },
          inverseJoinColumns = { @JoinColumn(name = "CONDITIONAL_ID", referencedColumnName = "ID") })
  public Conditional getConditional()
  {
    return conditional;
  }

  public void setConditional(Conditional conditional)
  {
    this.conditional = conditional;
  }
}
