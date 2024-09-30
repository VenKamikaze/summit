package org.awiki.kamikaze.summit.service.field.processor;

import org.awiki.kamikaze.summit.dto.render.TemplateDto;
import org.awiki.kamikaze.summit.mapper.TemplateMapper;
import org.awiki.kamikaze.summit.repository.TemplateRepository;
import org.awiki.kamikaze.summit.util.component.CycleAvoidingMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService
{
  private TemplateMapper mapper;
  private TemplateRepository repository;
  
  @Autowired
  public void setMapper(TemplateMapper mapper) {
    this.mapper = mapper;
  }

  @Autowired
  public void setRepository(TemplateRepository repository) {
    this.repository = repository;
  }

  @Override
  public TemplateDto getTemplateDtoForType(final String className)
  {
    return mapper.map(repository.findByClassName(className), new CycleAvoidingMappingContext());
  }
}
