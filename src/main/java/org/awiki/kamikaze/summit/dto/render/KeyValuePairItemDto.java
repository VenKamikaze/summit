package org.awiki.kamikaze.summit.dto.render;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.service.formatter.FormatEnums;

public class KeyValuePairItemDto implements PageItem<String>
{
  private Map.Entry<String, String> optionsKeyValue = null;
  private TemplateDto         template;
  private String              processedSource;
  
  public KeyValuePairItemDto(String key, String value)
  {
    this.optionsKeyValue = new AbstractMap.SimpleImmutableEntry<String, String>(key, value);
  }
  
  public String getKey()
  {
    return this.optionsKeyValue == null ? StringUtils.EMPTY : this.optionsKeyValue.getKey();
  }
  
  public String getValue()
  {
    return this.optionsKeyValue == null ? StringUtils.EMPTY : this.optionsKeyValue.getValue();
  }

  public void setKeyValue(Map.Entry<String, String> kvp)
  {
    this.optionsKeyValue = kvp;
  }

  @Override
  public Object getId()
  {
    return this.hashCode(); // random ID, doesnt matter.
  }

  @Override
  public String getName()
  {
    return StringUtils.EMPTY; // has no name attr, does not matter.
  }

  @Override
  public Map<String, String> getReplacementVariables()
  {
    Map<String, String> result = new TreeMap<String, String>();
    result.put(FormatEnums.REPLACEMENT_KEY_VARIABLE.toString(), optionsKeyValue == null ? StringUtils.EMPTY : optionsKeyValue.getKey());
    // Value is output with ##__DATA__## variable.
    return result;
  }

  @Override
  public boolean hasChildPageItems()
  {
    return false;
  }

  @Override
  public Collection<PageItem<String>> getChildPageItems()
  {
    return new ArrayList<>(0);
  }

  public TemplateDto getTemplate()
  {
    return template;
  }
  public void setTemplate(TemplateDto template)
  {
    this.template = template;
  }
  
  @Override
  public TemplateDto getTemplateDto()
  {
    return getTemplate();
  }

  @Override
  public ConditionalDto getConditional()
  {
    return null;
  }

  @Override
  public String getProcessedSource()
  {
    return processedSource;
  }
}
