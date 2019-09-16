package org.awiki.kamikaze.summit.service.formatter;

public enum FormatEnums
{
  REPLACEMENT_DATA_AND_SUBREGION_VARIABLE("##__DATA__##"),
  REPLACEMENT_NAME_VARIABLE("##__NAME__##"),
  REPLACEMENT_ID_VARIABLE("##__ID__##"),
  REPLACEMENT_PAGE_ID_VARIABLE("##__PAGE-ID__##"),
  REPLACEMENT_REGION_ID_VARIABLE("##__REGION-ID__##"),
  
  // Label replacement vars
  REPLACEMENT_LABEL_LEFT_ID_VARIABLE("##__LABEL-LEFT__##"),
  REPLACEMENT_LABEL_RIGHT_ID_VARIABLE("##__LABEL-RIGHT__##"),
  REPLACEMENT_LABEL_CLASS_VARIABLE("##__LABEL-CLASS__##"),
  REPLACEMENT_LABEL_FOR_VARIABLE("##__LABEL-FOR__##"),
  
  // Drop down replacement vars
  REPLACEMENT_KEY_VARIABLE("##__KEY__##"),
  REPLACEMENT_SELECTED_VARIABLE("##__SELECTED__##");
  
  private final String replacementVariable;
  
  FormatEnums(final String replacementVariableSource) {
    this.replacementVariable = replacementVariableSource;
  }
  
  public String getReplacementVariable() {
    return this.replacementVariable;
  }
  
  public String toString() {
    return getReplacementVariable();
  }
}
