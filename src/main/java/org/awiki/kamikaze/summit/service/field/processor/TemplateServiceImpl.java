package org.awiki.kamikaze.summit.service.field.processor;

import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.awiki.kamikaze.summit.repository.TemplateRepository;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService
{
  @Autowired
  private Mapper mapper;
  
  @Autowired
  private TemplateRepository repository;
  
  @Override
  public TemplateDto getTemplateDtoForType(final String className)
  {
    return mapper.map(repository.findByClassName(className), TemplateDto.class);
  }
}
