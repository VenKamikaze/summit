package org.awiki.kamikaze.summit.dto.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;


public class DropDownFieldDto extends FieldDto {
  
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
