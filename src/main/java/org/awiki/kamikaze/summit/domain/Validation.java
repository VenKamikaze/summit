package org.awiki.kamikaze.summit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.awiki.kamikaze.summit.domain.codetable.CodeSourceType;
import org.awiki.kamikaze.summit.domain.codetable.CodeValidationType;

/**
 * An APEX-style page validation: runs on POST after the submit button is
 * determined but before any POST1 processing. A failure aborts processing and
 * re-renders the page with the error message in the notification area.
 * Optionally gated by a CONDITIONAL (via VALIDATION_CONDITIONAL), typically
 * on :REQUEST.
 */
@Entity
@Table(name = "VALIDATION")
public class Validation implements java.io.Serializable
{
  private static final long serialVersionUID = 6469821377193517103L;

  private long                id;
  private Page                page;
  private String              name;
  private long                validationNum;      // run order within the page
  private CodeValidationType  codeValidationType;
  private String              fieldName;          // submitted parameter to check (NOT_NULL) / associate errors with
  private String              errorMessage;
  private Source              source = null;      // for the source-based validation types
  private CodeSourceType      codeSourceType;
  private Conditional         conditional = null;

  public Validation()
  {
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

  @Column(name = "NAME", nullable = false)
  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @Column(name = "VALIDATION_NUM", nullable = false)
  public long getValidationNum()
  {
    return this.validationNum;
  }

  public void setValidationNum(long validationNum)
  {
    this.validationNum = validationNum;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "VALIDATION_TYPE_CODE", nullable = false)
  public CodeValidationType getCodeValidationType()
  {
    return this.codeValidationType;
  }

  public void setCodeValidationType(CodeValidationType codeValidationType)
  {
    this.codeValidationType = codeValidationType;
  }

  @Column(name = "FIELD_NAME")
  public String getFieldName()
  {
    return this.fieldName;
  }

  public void setFieldName(String fieldName)
  {
    this.fieldName = fieldName;
  }

  @Column(name = "ERROR_MESSAGE", nullable = false)
  public String getErrorMessage()
  {
    return this.errorMessage;
  }

  public void setErrorMessage(String errorMessage)
  {
    this.errorMessage = errorMessage;
  }

  @OneToOne(fetch = FetchType.EAGER)
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
  @JoinColumn(name = "SOURCE_TYPE_CODE")
  public CodeSourceType getSourceTypeCode()
  {
    return this.codeSourceType;
  }

  public void setSourceTypeCode(CodeSourceType codeSourceType)
  {
    this.codeSourceType = codeSourceType;
  }

  @OneToOne
  @JoinTable(name = "VALIDATION_CONDITIONAL",
          joinColumns = { @JoinColumn(name = "VALIDATION_ID", referencedColumnName = "ID") },
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
