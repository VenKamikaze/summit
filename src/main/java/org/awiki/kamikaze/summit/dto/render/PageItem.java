package org.awiki.kamikaze.summit.dto.render;

import java.util.Collection;
import java.util.Map;

/*
public interface PageItem 
{

}
*/

public interface PageItem<T> // Where T is the processed source type
{
  public Object getId();
  
  public String getName();
  
  /**
   * Gets any unique replacement variables associated with this pageItem
   * Key = replacement variable name.
   * Value = value to replace with.
   * @return Map<String, String> or empty Map
   */
  public Map<String, String> getReplacementVariables();
  
  /**
   * whether this pageItem contains PageItems itself.
   * This helps control the order that the items are processed in (inner first, then outer).
   * @return
   */
  public boolean hasChildPageItems();
  
  /**
   * If this pageItem contains it's own page items, then this will return the list of those items.
   * Otherwise, should return an empty list.
   * @return
   */
  public Collection<PageItem<T>> getChildPageItems();
  
  /**
   * If the instance of this pageItem has an associated template, then return it.
   * @return TemplateDto
   */
  public TemplateDto getTemplateDto();
  
  /**
   * If the instance of this pageItem has a condition that must evaluate before it is rendered, then return it.
   * @return ConditionalDto
   */
  public ConditionalDto getConditional();
  
  public void setProcessedSource(T t); //unsure if needed

  /**
   * @return processed, but unformatted/styled source. E.g. if a field source consists of an expression "1+1"
   *           then this should return it's result, "2" rather than the raw source of "1+1".
   *         This can only happen after a processor has run on the PageItem
   */
  public T getProcessedSource(); //unsure if needed
}
