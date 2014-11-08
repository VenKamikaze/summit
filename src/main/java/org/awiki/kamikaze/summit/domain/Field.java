package org.awiki.kamikaze.summit.domain;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.awiki.kamikaze.summit.domain.codetable.CodeFieldSourceType;
import org.awiki.kamikaze.summit.domain.codetable.CodeFieldType;

/**
 * Field generated by hbm2java
 */
@Entity
@Table(name = "field", schema = "public")
public class Field implements java.io.Serializable
{

  private int                 id;
  private CodeFieldSourceType codeFieldSourceTypeBySourceType;
  private CodeFieldType       codeFieldType;
  private CodeFieldSourceType codeFieldSourceTypeByDefaultValueType;
  private String              source;
  private String              defaultValue;
  private String              notes;
  private Set<RegionField>    regionFields = new HashSet<RegionField>(0);

  public Field()
  {
  }

  public Field(int id, CodeFieldSourceType codeFieldSourceTypeBySourceType,
      CodeFieldType codeFieldType,
      CodeFieldSourceType codeFieldSourceTypeByDefaultValueType, String source)
  {
    this.id = id;
    this.codeFieldSourceTypeBySourceType = codeFieldSourceTypeBySourceType;
    this.codeFieldType = codeFieldType;
    this.codeFieldSourceTypeByDefaultValueType = codeFieldSourceTypeByDefaultValueType;
    this.source = source;
  }

  public Field(int id, CodeFieldSourceType codeFieldSourceTypeBySourceType,
      CodeFieldType codeFieldType,
      CodeFieldSourceType codeFieldSourceTypeByDefaultValueType, String source,
      String defaultValue, String notes, Set<RegionField> regionFields)
  {
    this.id = id;
    this.codeFieldSourceTypeBySourceType = codeFieldSourceTypeBySourceType;
    this.codeFieldType = codeFieldType;
    this.codeFieldSourceTypeByDefaultValueType = codeFieldSourceTypeByDefaultValueType;
    this.source = source;
    this.defaultValue = defaultValue;
    this.notes = notes;
    this.regionFields = regionFields;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  public int getId()
  {
    return this.id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_type", nullable = false)
  public CodeFieldSourceType getCodeFieldSourceTypeBySourceType()
  {
    return this.codeFieldSourceTypeBySourceType;
  }

  public void setCodeFieldSourceTypeBySourceType(
      CodeFieldSourceType codeFieldSourceTypeBySourceType)
  {
    this.codeFieldSourceTypeBySourceType = codeFieldSourceTypeBySourceType;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type", nullable = false)
  public CodeFieldType getCodeFieldType()
  {
    return this.codeFieldType;
  }

  public void setCodeFieldType(CodeFieldType codeFieldType)
  {
    this.codeFieldType = codeFieldType;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "default_value_type", nullable = false)
  public CodeFieldSourceType getCodeFieldSourceTypeByDefaultValueType()
  {
    return this.codeFieldSourceTypeByDefaultValueType;
  }

  public void setCodeFieldSourceTypeByDefaultValueType(
      CodeFieldSourceType codeFieldSourceTypeByDefaultValueType)
  {
    this.codeFieldSourceTypeByDefaultValueType = codeFieldSourceTypeByDefaultValueType;
  }

  @Column(name = "source", nullable = false, length = 10000)
  public String getSource()
  {
    return this.source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  @Column(name = "default_value", length = 10000)
  public String getDefaultValue()
  {
    return this.defaultValue;
  }

  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  @Column(name = "notes", length = 4000)
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

}