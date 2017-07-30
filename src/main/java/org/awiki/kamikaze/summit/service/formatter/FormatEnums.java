package org.awiki.kamikaze.summit.service.formatter;

public enum FormatEnums
{
  REPLACEMENT_DATA_AND_SUBREGION_VARIABLE("##__DATA__##"),
  REPLACEMENT_NAME_VARIABLE("##__NAME__##"),
  REPLACEMENT_ID_VARIABLE("##__ID__##"),
  REPLACEMENT_PAGE_ID_VARIABLE("##__PAGE-ID__##"),
  REPLACEMENT_REGION_ID_VARIABLE("##__REGION-ID__##");
  
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
