package org.awiki.kamikaze.summit.dto.render.field;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.awiki.kamikaze.summit.service.formatter.FormatEnums;

public class KeyValuePairItem implements PageItem<String>
{
  private Map.Entry<String, String> optionsKeyValue = null;
  private TemplateDto         template;
  
  public KeyValuePairItem(String key, String value)
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
  public Map<String, String> getCustomReplacementVariables()
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
    return getValue();
  }
}
