package org.awiki.kamikaze.summit.service.field.processor;

import org.awiki.kamikaze.summit.dto.render.TemplateDto;

public interface TemplateService
{
  public TemplateDto getTemplateDtoForType(final String className);
}
