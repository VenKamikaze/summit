package org.awiki.kamikaze.summit.dto.entry;

import java.util.Collection;

/*
public interface PageItem 
{

}
*/

public interface PageItem<T> // Where T is the processed source type
{
  /**
   * whether this pageItem contains PageItems itself.
   * This helps control the order that the items are processed in (inner first, then outer).
   * @return
   */
  public boolean hasSubPageItems();
  
  /**
   * If this pageItem contains it's own page items, then this will return the list of those items.
   * Otherwise, should return an empty list.
   * @return
   */
  public Collection<PageItem<T>> getSubPageItems();
  
  /**
   * If the instance of this pageItem has an associated template, then return it.
   * @return TemplateDto
   */
  public TemplateDto getTemplateDto();
  
  public void setProcessedSource(T t); //unsure if needed

  /**
   * @return processed, but unformatted/styled source. E.g. if a field source consists of an expression "1+1"
   *           then this should return it's result, "2" rather than the raw source of "1+1".
   *         This can only happen after a processor has run on the PageItem
   */
  public T getProcessedSource(); //unsure if needed
}
