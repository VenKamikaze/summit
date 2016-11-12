package org.awiki.kamikaze.summit.service.formatter;

public enum FormatEnums
{
  REPLACEMENT_VARIABLE("##__DATA__##");
  
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
