package org.awiki.kamikaze.summit.util;

import java.util.Arrays;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class StringUtils
{
  public static final String PAGE_PARAM_KEYVALUE_SEPARATOR = ":";
  public static final String PAGE_PARAM_ITEM_SEPARATOR = ",";
  
  /**
   * Given a StringBuilder builder, find string 'target' and replace it with 'replacement'.
   * Replaces all instances.
   * @param target
   * @param replacement
   * @param builder
   * @see http://stackoverflow.com/questions/4965513/stringbuilder-vs-string-considering-replace
   */
  public static void replace( String target, String replacement, StringBuilder builder ) 
  { 
    int indexOfTarget = -1;
    while( ( indexOfTarget = builder.indexOf( target ) ) >= 0 ) { 
      builder.replace( indexOfTarget, indexOfTarget + target.length() , replacement );
    }
  }
  
  public static MultiValueMap<String, String> toParameterMap(final String pageParams)
  {
    if(pageParams == null || pageParams.indexOf(PAGE_PARAM_KEYVALUE_SEPARATOR) < 1)
      return new LinkedMultiValueMap<String, String>(0);
    
    final String varsAndValues[] = pageParams.split(PAGE_PARAM_KEYVALUE_SEPARATOR);
    final String vars[] = varsAndValues[0].split(PAGE_PARAM_ITEM_SEPARATOR);
    final String values[] = varsAndValues[1].split(PAGE_PARAM_ITEM_SEPARATOR);
    
    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>(vars.length);
    
    for(int i = 0; i < vars.length; i++) {
      paramMap.put(vars[i], Arrays.asList(values[i])); // TODO handle multiple values in pageParams
    }
    
    return paramMap;
  }
}
