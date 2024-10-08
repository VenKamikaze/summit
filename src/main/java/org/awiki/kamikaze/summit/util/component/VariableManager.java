package org.awiki.kamikaze.summit.util.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

// 2019-09: does not seem to be used
//  consider removing this. I think the idea was we could have simple kvp replacement occurring
//  via this class.
// 2024-09 - I at least see usages of it? I think it is used?
// 2024-09-30: pretty sure this drives replacement variables for contextPath and apiPath
@Component
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
