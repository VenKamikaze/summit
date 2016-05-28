package org.awiki.kamikaze.summit.domain.codetable;
// Generated Oct 31, 2013 9:11:16 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


/**
 * CodeFieldSourceType generated by hbm2java
 * This domain object represents the types of source code that can be processed
 * by summit. CodeFieldSourceType and CodeProcessingSrcType will link against codes
 * in this class, and the processorServiceClass is then instantiated to handle the processing
 * of the source code into rendered content. 
 */
@Entity
@Table(name = "code_source_type", schema = "public")
public class CodeSourceType implements java.io.Serializable, CodeTable
{

  private static final long serialVersionUID = 3888121341827987520L;
  
  private String     code;
  private String     description;
  private Long       sortOrder;
  private String     sourceIdentifier;
  
  // xx //TODO changed definition private String     processorServiceClass; // actual Class name
                                            // used in reflection to instantiate the class
                                            // that will process the field source into rendered content.
  //private Set<Field> fieldsForDefaultValueType = new HashSet<Field>(0);
  //private Set<Field> fieldsForSourceType       = new HashSet<Field>(0);

  public CodeSourceType()
  {
  }

  public CodeSourceType(String code, String description, Long sortOrder, String sourceIdentifier)
  {
    this.code = code;
    this.description = description;
    this.setSortOrder(sortOrder);
    this.setSourceIdentifier(sourceIdentifier);
    //this.processorServiceClass = processorServiceClass;
    //this.fieldsForDefaultValueType = fieldsForDefaultValueType;
    //this.fieldsForSourceType = fieldsForSourceType;
  }

  @Id
  @Column(name = "code", unique = true, nullable = false, length = 10)
  public String getCode()
  {
    return this.code;
  }

  public void setCode(String code)
  {
    this.code = code;
  }

  @Column(name = "description", nullable = false)
  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  @NotNull
  @Column(name = "sort_order")
  public Long getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Long sortOrder) {
    this.sortOrder = sortOrder;
  }

  @NotNull
  @Column(name = "source_identifier")
  public String getSourceIdentifier() {
    return sourceIdentifier;
  }

  public void setSourceIdentifier(String sourceIdentifier) {
    this.sourceIdentifier = sourceIdentifier;
  }

}
