package org.awiki.kamikaze.summit.dto.render.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.awiki.kamikaze.summit.dto.render.DropDownOptionItemDto;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;


// 2019-08-06: This looks unused. TB deleted.
public class DropDownFieldInstance extends FieldInstance {
  
  public DropDownFieldInstance(FieldDto field)
  {
    super(field);
  }

  private List<PageItem<String>> options = new ArrayList<>();
  
  public void setOptions(List<DropDownOptionItemDto> options)
  {
    this.options.clear();
    this.options.addAll(options);
  }
  
  public void setKeyAsSelected(String key) {
    for(PageItem<String> item : options) {
      DropDownOptionItemDto optionItem = ((DropDownOptionItemDto) item);
      if(key.equals( optionItem.getKey() ) ) {
        optionItem.setSelected(true);
      }
    }
  }
  
  public boolean containsKey(String key) {
    for(PageItem<String> item : options) {
      if(key.equals( ((DropDownOptionItemDto) item).getKey() ) ) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public boolean hasChildPageItems()
  {
    return options != null && options.size() > 0;
  }
  
  @Override
  public List<PageItem<String>> getChildPageItems()
  {
    return options;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, String> getCustomReplacementVariables()
  {
    return MapUtils.EMPTY_SORTED_MAP; 
  }


  @Override
  public String getProcessedSource()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
