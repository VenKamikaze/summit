package org.awiki.kamikaze.summit.dto.render;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.awiki.kamikaze.summit.dto.render.field.KeyValuePairItem;
import org.awiki.kamikaze.summit.service.formatter.FormatEnums;

public class DropDownOptionItemDto extends KeyValuePairItem
{
  private String selected = StringUtils.EMPTY;
  public static final String SELECTED_TRUE = "selected=\"selected\"";
  
  public DropDownOptionItemDto(String key, String value)
  {
    super(key, value);
  }

  public void setSelected(boolean isSelected) {
    setSelected(isSelected ? SELECTED_TRUE : StringUtils.EMPTY);
  }
  
  public void setSelected(String selected) {
    this.selected = selected;
  }
  
  public String getSelected() {
    return selected;
  }
  
  @Override
  public Map<String, String> getCustomReplacementVariables()
  {
    Map<String, String> replacementVars = super.getCustomReplacementVariables();
    replacementVars.put(FormatEnums.REPLACEMENT_SELECTED_VARIABLE.toString(), getSelected());
    return replacementVars;
  }
}
