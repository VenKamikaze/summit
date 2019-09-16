package org.awiki.kamikaze.summit.dto.render.field;

import org.awiki.kamikaze.summit.dto.render.ConditionalDto;
import org.awiki.kamikaze.summit.dto.render.FieldDto;
import org.awiki.kamikaze.summit.dto.render.PageItem;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;

//2019-08-06: This looks unused. TB deleted.
public abstract class FieldInstance implements PageItem<String>
{
  private FieldDto associatedFieldDto;
  
  public FieldInstance(FieldDto field)
  {
    this.associatedFieldDto = field;
  }

  @Override
  public Object getId()
  {
    return associatedFieldDto.getId();
  }

  @Override
  public String getName()
  {
    return associatedFieldDto.getName();
  }

  @Override
  public TemplateDto getTemplateDto()
  {
    return associatedFieldDto.getTemplateDto();
  }

  @Override
  public ConditionalDto getConditional()
  {
    return associatedFieldDto.getConditional();
  }
  
  public FieldDto getAssociatedFieldDto() {
    return associatedFieldDto;
  }

}
