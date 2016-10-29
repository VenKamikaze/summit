package org.awiki.kamikaze.summit.service.formatter;

import java.util.Collection;

/**
 * Indicates an object (e.g. a SourceProcessorResultTable) is formattable.
 * 
 * 2016-10-23: I am not sure what this interface should expose. TBD.
 * 
 * Should this be database driven via a table of className -> Template?
 * @author msaun
 * @param <E>
 *
 */
public interface Formattable<E>
{
  public Collection<E> getHeaderElements();
  public Collection<E> getBodyElements();
  public Collection<E> getFooterElements();
}
