package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.awiki.kamikaze.summit.domain.codetable.CodeFieldType;
import org.awiki.kamikaze.summit.domain.codetable.CodeSourceType;

/**
 * Field generated by hbm2java
 */
@Entity
@Table(name = "FIELD")
public class Field implements java.io.Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -7419252566793513396L;
  
  private long                id;
  private String              name;
  private CodeSourceType codeFieldSourceTypeBySourceType;
  private CodeFieldType       codeFieldType;
  private CodeSourceType codeFieldSourceTypeByDefaultValueType;
  
  private List<Source>        source;
  
  // TODO: change to ManyToMany 
  //private String              defaultValue;
  private String              notes;
  private Set<RegionField>    regionFields = new HashSet<RegionField>(0);
  private Template             template;
  
  private Conditional         conditional = null;

  public Field()
  {
  }

  public Field(long id, CodeSourceType codeFieldSourceTypeBySourceType,
      CodeFieldType codeFieldType,
      CodeSourceType codeFieldSourceTypeByDefaultValueType, List<Source> source)
  {
    this.id = id;
    this.codeFieldSourceTypeBySourceType = codeFieldSourceTypeBySourceType;
    this.codeFieldType = codeFieldType;
    this.codeFieldSourceTypeByDefaultValueType = codeFieldSourceTypeByDefaultValueType;
    this.source = source;
  }

  public Field(long id, CodeSourceType codeFieldSourceTypeBySourceType,
      CodeFieldType codeFieldType,
      CodeSourceType codeFieldSourceTypeByDefaultValueType, List<Source> source,
      /*String defaultValue,*/ String notes, Set<RegionField> regionFields)
  {
    this.id = id;
    this.codeFieldSourceTypeBySourceType = codeFieldSourceTypeBySourceType;
    this.codeFieldType = codeFieldType;
    this.codeFieldSourceTypeByDefaultValueType = codeFieldSourceTypeByDefaultValueType;
    this.source = source;
    //this.defaultValue = defaultValue;
    this.notes = notes;
    this.regionFields = regionFields;
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

  @Column(name = "NAME")
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SOURCE_TYPE_CODE", nullable = false)
  public CodeSourceType getCodeFieldSourceTypeBySourceType()
  {
    return this.codeFieldSourceTypeBySourceType;
  }

  public void setCodeFieldSourceTypeBySourceType(
          CodeSourceType codeFieldSourceTypeBySourceType)
  {
    this.codeFieldSourceTypeBySourceType = codeFieldSourceTypeBySourceType;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FIELD_TYPE_CODE", nullable = false)
  public CodeFieldType getCodeFieldType()
  {
    return this.codeFieldType;
  }

  public void setCodeFieldType(CodeFieldType codeFieldType)
  {
    this.codeFieldType = codeFieldType;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DEFAULT_SOURCE_TYPE_CODE", nullable = false)
  public CodeSourceType getCodeFieldSourceTypeByDefaultValueType()
  {
    return this.codeFieldSourceTypeByDefaultValueType;
  }

  public void setCodeFieldSourceTypeByDefaultValueType(
          CodeSourceType codeFieldSourceTypeByDefaultValueType)
  {
    this.codeFieldSourceTypeByDefaultValueType = codeFieldSourceTypeByDefaultValueType;
  }
  
  @ManyToMany
  @JoinTable(name="FIELD_SOURCE",
             joinColumns = { @JoinColumn(name="FIELD_ID", referencedColumnName="ID") },
             inverseJoinColumns = { @JoinColumn(name="SOURCE_ID", referencedColumnName="ID") })
  public List<Source> getSource()
  {
    return this.source;
  }

  public void setSource(List<Source> source)
  {
    this.source = source;
  }

  /*
  @Column(name = "default_value", length = 10000)
  public String getDefaultValue()
  {
    return this.defaultValue;
  }

  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }
  */

  @Column(name = "NOTES", length = 4000)
  public String getNotes()
  {
    return this.notes;
  }

  public void setNotes(String notes)
  {
    this.notes = notes;
  }

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "field")
  public Set<RegionField> getRegionFields()
  {
    return this.regionFields;
  }

  public void setRegionFields(Set<RegionField> regionFields)
  {
    this.regionFields = regionFields;
  }

  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "TEMPLATE_ID", nullable = false)
  public Template getTemplate() {
    return template;
  }

  public void setTemplate(Template template) {
    this.template = template;
  }

  @OneToOne
  @JoinTable(name = "FIELD_CONDITIONAL",
          joinColumns = { @JoinColumn(name = "FIELD_ID", referencedColumnName = "ID") },
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
