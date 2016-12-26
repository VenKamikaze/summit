package org.awiki.kamikaze.summit.service;

import java.util.HashMap;
import java.util.Map;

public class VariableManager
{
  private Map<String, String> replacementVars = new HashMap<String, String>();
  
  public VariableManager(Map<String, String> replacementVars) {
    this.replacementVars = replacementVars;
  }
  
  public Map<String,String> getReplacementVars() {
    return replacementVars;
  }
  
  public void putAll(Map<String,String> additionalVars) {
    replacementVars.putAll(additionalVars);
  }
}
