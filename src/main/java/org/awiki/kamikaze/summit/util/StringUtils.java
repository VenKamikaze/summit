package org.awiki.kamikaze.summit.util;

public class StringUtils
{
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
}
