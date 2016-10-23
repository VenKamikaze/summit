package org.awiki.kamikaze.summit.service.formatter;

/**
 * Indicates an object (e.g. a SourceProcessorResultTable) is formattable.
 * 
 * 2016-10-23: I am not sure what this interface should expose. TBD.
 * 
 * Should this be database driven via a table of className -> Template?
 * @author msaun
 *
 */
public interface Formattable
{
  /*public String getHeader();
  public String getBody();
  public String getFooter(); */
  public String format();
}
