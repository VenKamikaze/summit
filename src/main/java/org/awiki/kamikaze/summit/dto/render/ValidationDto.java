package org.awiki.kamikaze.summit.dto.render;

/**
 * An APEX-style page validation: runs on POST before any POST1 processing.
 * See the Validation domain class for semantics.
 */
public class ValidationDto
{
  private Long           id;
  private PageDto        page;
  private String         name;
  private Long           validationNum;
  private String         codeValidationType;   // e.g. NOT_NULL, TEXT_TRUE, EXISTS, NOTEXISTS
  private String         fieldName;
  private String         errorMessage;
  private SourceDto      source = null;
  private String         sourceTypeCode;       // e.g. dml_selcel
  private ConditionalDto conditional = null;

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public PageDto getPage() {
    return page;
  }
  public void setPage(PageDto page) {
    this.page = page;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Long getValidationNum() {
    return validationNum;
  }
  public void setValidationNum(Long validationNum) {
    this.validationNum = validationNum;
  }
  public String getCodeValidationType() {
    return codeValidationType;
  }
  public void setCodeValidationType(String codeValidationType) {
    this.codeValidationType = codeValidationType;
  }
  public String getFieldName() {
    return fieldName;
  }
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
  public String getErrorMessage() {
    return errorMessage;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  public SourceDto getSource() {
    return source;
  }
  public void setSource(SourceDto source) {
    this.source = source;
  }
  public String getSourceTypeCode() {
    return sourceTypeCode;
  }
  public void setSourceTypeCode(String sourceTypeCode) {
    this.sourceTypeCode = sourceTypeCode;
  }
  public ConditionalDto getConditional() {
    return conditional;
  }
  public void setConditional(ConditionalDto conditional) {
    this.conditional = conditional;
  }
}
