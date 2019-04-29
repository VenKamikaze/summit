package org.awiki.kamikaze.summit.service.field;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.Closure;
import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.awiki.kamikaze.summit.dto.render.field.KeyValuePairItem;
import org.awiki.kamikaze.summit.service.field.processor.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemplateFetchForKeyValuePairItemClosure implements Closure<KeyValuePairItem>
{
  @Autowired
  private TemplateService templateService;
  
  // Local Template cache for speed
  private Map<String, TemplateDto> templateCache = new HashMap<>();
  
  @Override
  public void execute(final KeyValuePairItem input)
  {
    TemplateDto templateDto = templateCache.get(input.getClass().getCanonicalName());
    if(templateDto == null) {
      templateDto = templateService.getTemplateDtoForType(input.getClass().getCanonicalName());
      templateCache.put(input.getClass().getCanonicalName(), templateDto);
    }
    input.setTemplate(templateDto);
  }
}
